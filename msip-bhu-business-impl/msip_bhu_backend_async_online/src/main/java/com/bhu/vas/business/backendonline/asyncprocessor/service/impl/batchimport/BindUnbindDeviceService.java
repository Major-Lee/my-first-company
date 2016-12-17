package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.BusinessEnumType.TimPushChannel;
import com.bhu.vas.api.helper.BusinessEnumType.TimPushMsgType;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.SharedNetworksHelper;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.api.rpc.message.iservice.IMessageUserRpcService;
import com.bhu.vas.api.rpc.user.model.PushType;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.backendonline.asyncprocessor.buservice.BackendBusinessService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementService;
import com.bhu.vas.business.search.service.increment.WifiDeviceStatusIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class BindUnbindDeviceService {
	private final Logger logger = LoggerFactory.getLogger(BindUnbindDeviceService.class);
	
	@Resource
	private IMessageUserRpcService messageUserRpcService;

	@Resource
	private WifiDeviceService wifiDeviceService;

	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private UserWifiDeviceService userWifiDeviceService;

	@Resource
	private UserService userService;
	
	@Resource
	private WifiDeviceStatusIndexIncrementService wifiDeviceStatusIndexIncrementService;
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	@Resource
	private BackendBusinessService backendBusinessService;
	
	@Resource
	private TagGroupRelationService tagGroupRelationService;
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;
	
	
	public void bindDevice(List<String> macs, final int uid_willbinded){
		List<String> group_macs = new ArrayList<String>();
		List<String> snkMacs = new ArrayList<String>();
		final List<DownCmds> downCmds = new ArrayList<DownCmds>();

		try{
			for(String dmac:macs){
	
				WifiDevice wifiDevice = wifiDeviceService.getById(dmac);
				if(wifiDevice == null){
					logger.info(String.format("can't find device by mac[%s]", dmac));
					continue;
				}
				UserWifiDevice userWifiDevice = userWifiDeviceService.getById(dmac);
				if(userWifiDevice != null && userWifiDevice.getUid() == uid_willbinded)
					continue; //已经此用户绑定，不动作
				
				if(userWifiDevice != null){
					userWifiDeviceService.delete(userWifiDevice);
					group_macs.add(dmac);
				}
				
				userWifiDeviceFacadeService.insertUserWifiDevice(dmac, uid_willbinded);
	            deviceFacadeService.gainDeviceMobilePresentString(uid_willbinded,dmac);
	            snkMacs.add(dmac);
				//分成表上的owner字段需要更换
				chargingFacadeService.wifiDeviceBindedNotify(dmac, uid_willbinded);
			}
	
			//清除分组关系
			if(!group_macs.isEmpty()){
				for(String gmac:group_macs){
					deviceFacadeService.destoryDeviceMobilePresentString(gmac);
				}
				tagGroupRelationService.cleanDeviceGroupRels(group_macs);
				wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(group_macs, null);
			}
	
			if(snkMacs.size() == 0)
				return;
	        //绑定新的共享网络
			sharedNetworksFacadeService.addDevices2SharedNetwork(uid_willbinded, SharedNetworkType.SafeSecure, SharedNetworksHelper.DefaultTemplate,false,snkMacs,
					new ISharedNetworkNotifyCallback(){
						@Override
						public void notify(ParamSharedNetworkDTO current,List<String> rdmacs) {
							logger.info(String.format("notify callback uid[%s] rdmacs[%s] sharednetwork conf[%s]", uid_willbinded,rdmacs,JsonHelper.getJSONString(current)));
							if(rdmacs == null || rdmacs.isEmpty()){
								return;
							}
							for(String mac:rdmacs){
								WifiDevice wifiDevice = wifiDeviceService.getById(mac);
								if(wifiDevice == null) continue;
								//生成下发指令
								String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, mac, -1,JsonHelper.getJSONString(current),
										DeviceStatusExchangeDTO.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),deviceCMDGenFacadeService);
								downCmds.add(DownCmds.builderDownCmds(mac, cmd));
							}
						}
					});
			if(!downCmds.isEmpty()){
				daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
				downCmds.clear();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param macs
	 * @param needDoNothingWhenDevRegat	 false:强制给设备解绑， true：如果设备从从未上线则强制解绑，如果设备上线过则不动作
	 * @param opUserMobileNo 当前操作员手机号，如果非空，会通知设备当前绑定用户，设备被该手机号解绑
	 */
	public void unbindDevice(List<String>macs, boolean needDoNothingWhenDevRegat, String opUserMobileno){
		List<String> forceUnbindedDevices = new ArrayList<>();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
    	Calendar c1 = Calendar.getInstance();
    	c1.setTime(new Date()); 
		String dateStr = sdf.format(c1.getTime());


		try{
			List<WifiDevice> wifiDevices = wifiDeviceService.findByIds(macs);
			for(WifiDevice device:wifiDevices){
				if(!needDoNothingWhenDevRegat || (device.getLast_logout_at() == null 
						&& device.getLast_reged_at() == null
						&& device.getOem_swver().contains("P06V0.0.0Build0000"))){//从未上线
					forceUnbindedDevices.add(device.getId());
					if(opUserMobileno != null){
						UserWifiDevice userDevice = userWifiDeviceService.getById(device.getId());
						if(userDevice != null){
							String macstr = userDevice.getId();
							if(StringUtils.isNotEmpty(userDevice.getDevice_name()))
								macstr = String.format("%s(%s)", macstr, userDevice.getDevice_name());
							try{
								messageUserRpcService.send_single_msg(TimPushChannel.BHUUnion.getChannel(), 
										String.valueOf(userDevice.getUid()), TimPushMsgType.TIMTextElem.getMsgType(), 
										String.format(PushType.DeviceResetByOps.getText(), macstr, opUserMobileno, dateStr, opUserMobileno));
							}catch(Exception e){
								logger.error("error on send message to old bind user");
							}
						}
					}
					//分成表上的owner字段需要更换
					chargingFacadeService.wifiDeviceUnBindedNotify(device.getId());
				}
			}
			
			if(!forceUnbindedDevices.isEmpty()){
				//userDeviceFacadeService.doForceUnbindDevice(forceUnbindedDevices);
				userWifiDeviceService.deleteByIds(forceUnbindedDevices);
				for(String dmac:forceUnbindedDevices){
					deviceFacadeService.destoryDeviceMobilePresentString(dmac);
				}
				tagGroupRelationService.cleanDeviceGroupRels(forceUnbindedDevices);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
