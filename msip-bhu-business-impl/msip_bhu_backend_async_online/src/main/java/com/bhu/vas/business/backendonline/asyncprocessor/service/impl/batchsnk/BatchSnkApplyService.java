package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsnk;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.SharedNetworkChangeType;
import com.bhu.vas.api.helper.UPortalHttpHelper;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.SnkTurnStateEnum;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkModifyNotifyCallback;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.SnkChargingMarkerService;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class BatchSnkApplyService {
	private final Logger logger = LoggerFactory.getLogger(BatchSnkApplyService.class);
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;
	
	
	
	public void modify(final int userid, final String ssid, final int rate, List<String> dmacs){
		logger.info(String.format("modify sharednetwork conf uid[%s] ssid[%s] rate[%s] dmacs[%s]",userid, ssid, rate, dmacs));
		if(dmacs == null || dmacs.isEmpty()) return;
		try{
			final List<DownCmds> downCmds = new ArrayList<DownCmds>();
			
			sharedNetworksFacadeService.modifyDevicesSharedNetwork(userid, ssid, rate, dmacs,
					new ISharedNetworkModifyNotifyCallback(){
						@Override
						public void notify(List<WifiDeviceSharedNetwork> snks) {
							logger.info(String.format("modify snk notify callback"));
							if(snks == null || snks.isEmpty()){
								return;
							}
							for(WifiDeviceSharedNetwork snk:snks){
								if(snk == null)
									continue;
								SharedNetworkSettingDTO dto = snk.getInnerModel();
								if(dto == null)
									continue;
								ParamSharedNetworkDTO psn = dto.getPsn();
								if(psn == null)
									continue;
								if(!dto.isOn())
									continue;
								WifiDevice wifiDevice = wifiDeviceService.getById(snk.getId());
								if(wifiDevice == null) continue;
								if(!wifiDevice.isOnline())
									continue;
								//生成下发指令
								String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, snk.getId(), -1,JsonHelper.getJSONString(psn),
										DeviceStatusExchangeDTO.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),deviceCMDGenFacadeService);
								downCmds.add(DownCmds.builderDownCmds(snk.getId(), cmd));
							}
						}
					});
			
			if(!downCmds.isEmpty()){
				daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
				downCmds.clear();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
	}

	public void apply(final int userid, final char dtoType,List<String> dmacs, SharedNetworkType sharedNetwork,String template, final SharedNetworkChangeType configChanged) {
		logger.info(String.format("apply sharednetwork conf uid[%s] dtoType[%s] snk[%s] template[%s] dmacs[%s] configChanged[%s]",userid,dtoType,sharedNetwork.getKey(),template, dmacs, configChanged));
		if(dmacs == null || dmacs.isEmpty()) return;
		try{
			final List<DownCmds> downCmds = new ArrayList<DownCmds>();
			switch(dtoType){
				case IDTO.ACT_ADD:
				case IDTO.ACT_UPDATE:
					sharedNetworksFacadeService.addDevices2SharedNetwork(userid,sharedNetwork,template,false,dmacs,
							new ISharedNetworkNotifyCallback(){
								@Override
								public void notify(ParamSharedNetworkDTO current,List<String> rdmacs) {
									logger.info(String.format("notify callback uid[%s] rdmacs[%s] sharednetwork conf[%s]", userid,rdmacs,JsonHelper.getJSONString(current)));
									if(rdmacs == null || rdmacs.isEmpty()){
										return;
									}
									if(dtoType == IDTO.ACT_ADD || 
											(dtoType == IDTO.ACT_UPDATE && SharedNetworkChangeType.SHARE_NETWORK_DEVICE_PART_CHANGED == configChanged) //打赏时长合并到portal模板以后，可能只是修改了打赏时长，此时不需要给设备下发指令
											){ 
										for(String mac:rdmacs){
											WifiDevice wifiDevice = wifiDeviceService.getById(mac);
											if(wifiDevice == null) continue;
											//生成下发指令
											String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, mac, -1,JsonHelper.getJSONString(current),
													DeviceStatusExchangeDTO.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),deviceCMDGenFacadeService);
											downCmds.add(DownCmds.builderDownCmds(mac, cmd));
										}
									}
									wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(userid, rdmacs, current.getNtype(),current.getTemplate(),SnkTurnStateEnum.On.getType());
								}
							});
					break;
				case IDTO.ACT_DELETE:
					 sharedNetworksFacadeService.closeAndApplyDevicesFromSharedNetwork(userid,sharedNetwork,template,dmacs,new ISharedNetworkNotifyCallback(){
							@Override
							public void notify(ParamSharedNetworkDTO current,List<String> rdmacs) {
								logger.info(String.format("notify callback uid[%s] rdmacs[%s] sharednetwork conf[%s]", userid,rdmacs,JsonHelper.getJSONString(current)));
								if(rdmacs == null || rdmacs.isEmpty()){
									return;
								}
								for(String mac:rdmacs){
									WifiDevice wifiDevice = wifiDeviceService.getById(mac);
									if(wifiDevice == null) continue;
									//生成下发指令
									String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Stop, mac, -1,null,
											DeviceStatusExchangeDTO.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),deviceCMDGenFacadeService);
									downCmds.add(DownCmds.builderDownCmds(mac, cmd));
								}
								wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(userid, rdmacs, current.getNtype(),current.getTemplate(),SnkTurnStateEnum.Off.getType());
							}
						});
					 
					break;
				default:
					throw new UnsupportedOperationException(String.format("snk act type not supported"));
			}
			try{
				//TODO:如果为SmsSecure 则需要判定此用户id当前是否还存在此类型的网络处于开启状态，如果都关闭了，则需要重置通知开关并通知portal服务器
				 //logger.info(String.format("准备开始判定当前信息 共享网络状态类型 "));
				 //if(SharedNetworkType.SmsSecure == sharedNetwork){
				 //}
				logger.info(String.format("准备开始判定当前用户信息共享网络状态类型【%s】【%s】",userid,SharedNetworkType.SmsSecure.getKey()));
				long count = wifiDeviceDataSearchService.searchCountBySnkType(userid,SharedNetworkType.SmsSecure.getKey(),null,
						 WifiDeviceDocumentEnumType.SnkTurnStateEnum.On.getType());
				logger.info(String.format("当前用户信息共享网络状态类型【%s】【%s】 【%s】",userid,SharedNetworkType.SmsSecure.getKey(),count));
				if(count <= 0){//不提供sms认证服务
				 	logger.info(String.format("当前用户信息共享网络状态类型【%s】【%s】 【%s】开始清除标记",userid,SharedNetworkType.SmsSecure.getKey(),count));
				 	//清除标记
					SnkChargingMarkerService.getInstance().clear(userid);
					//通知uportal清除标记位
					UPortalHttpHelper.uPortalChargingStatusNotify(userid,UPortalHttpHelper.NoService);
					logger.info(String.format("当前用户信息共享网络状态类型【%s】【%s】 【%s】开始清除标记成功",userid,SharedNetworkType.SmsSecure.getKey(),count));
				}
			}catch(Exception ex){
				ex.printStackTrace(System.out);
			}
			
			if(!downCmds.isEmpty()){
				daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
				downCmds.clear();
			}
		}finally{
			/*if(downCmds != null){
				downCmds.clear();
				downCmds = null;
			}*/
		}
		//logger.info(String.format("process message[%s] successful", message));
	}
}
