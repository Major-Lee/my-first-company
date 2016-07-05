package com.bhu.vas.di.op.migrate;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 更新共享网络中的open_resource为系统定义的缺省配置
 * @author Edmond
 *
 */
public class UserSharedNetworksRepairEnvOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		//由于用户可能存在定制的authurl，所有把123.56.227.18 replace ucloud.bhuwifi.com
		//http://123.56.227.18:9158/portal/default/reward/index_before.html
		//sharednetworkwifi.default.remote.authurl = http://ucloud.bhuwifi.com:9158/portal/default/reward/index_before.html
		//sharednetworkwifi.default.openresource = isdspeed.qq.com,pay.qq.com,weixin.qq.com,jspay.qq.com,bhuwifi.com,bhunetworks.com,midas.gtimg.cn,alipay.com,123.57.26.170,hao.bhuwifi.com,api.unipay.qq.com,cnzz.com
		//sharednetworkwifi.default.remote.protalserverurl = uportal.bhuwifi.com:18085
		//user.portal.update.uportalapi = http://ucloud.bhuwifi.com:9158/portal/set/recreate
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworksRepairEnvOp.class);
		ctx.start();
		System.out.println("Remote_auth_url:"+BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Remote_auth_url);
		System.out.println("Uplink:"+BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Uplink_Open_resource);
		System.out.println("SafeSecure:"+BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Open_resource);
		System.out.println("SafeSecure:"+BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		DeviceCMDGenFacadeService deviceCMDGenFacadeService = (DeviceCMDGenFacadeService)ctx.getBean("deviceCMDGenFacadeService");
		SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		IDaemonRpcService daemonRpcService =  (IDaemonRpcService)ctx.getBean("daemonRpcService");
		//设备mac，84:82:f4:32:3b:dc   uid 10
		//106493  84:82:F4:1A:96:11
		/*List<Integer> users = new ArrayList<Integer>();
		users.add(10);
		users.add(106493);
		//.andColumnIn("id", users)
		//.andColumnIn("id", macs)
		List<String> macs = new ArrayList<String>();
		macs.add("84:82:f4:32:3b:dc");
		macs.add("84:82:f4:1a:96:11");*/
		ModelCriteria mc_udsnk = new ModelCriteria();
		mc_udsnk.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc_udsnk.setOrderByClause("id desc");
		mc_udsnk.setPageNumber(1);
		mc_udsnk.setPageSize(200);
    	EntityIterator<Integer, UserDevicesSharedNetworks> itit_udsnk = new KeyBasedEntityBatchIterator<Integer, UserDevicesSharedNetworks>(Integer.class, UserDevicesSharedNetworks.class, sharedNetworksFacadeService.getUserDevicesSharedNetworksService().getEntityDao(), mc_udsnk);
		while(itit_udsnk.hasNext()){
			List<UserDevicesSharedNetworks> list = itit_udsnk.next();
			for(UserDevicesSharedNetworks snks:list){
				List<ParamSharedNetworkDTO> safesecure_configs = snks.get(SharedNetworkType.SafeSecure.getKey());
				if(safesecure_configs != null && !safesecure_configs.isEmpty()){
					for(ParamSharedNetworkDTO dto:safesecure_configs){
						dto.setRemote_auth_url(dto.getRemote_auth_url().replace("123.56.227.18", "ucloud.bhuwifi.com"));
						dto.setPortal_server_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
						dto.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Open_resource);
					}
					snks.put(SharedNetworkType.SafeSecure.getKey(), safesecure_configs);
				}
				List<ParamSharedNetworkDTO> uplink_configs = snks.get(SharedNetworkType.Uplink.getKey());
				if(uplink_configs != null && !uplink_configs.isEmpty()){
					for(ParamSharedNetworkDTO dto:uplink_configs){
						dto.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Uplink_Open_resource);
					}
					snks.put(SharedNetworkType.Uplink.getKey(), uplink_configs);
				}
				List<ParamSharedNetworkDTO> sms_configs = snks.get(SharedNetworkType.SmsSecure.getKey());
				if(sms_configs != null && !sms_configs.isEmpty()){
					for(ParamSharedNetworkDTO dto:sms_configs){
						dto.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Open_resource);
					}
					snks.put(SharedNetworkType.SmsSecure.getKey(), sms_configs);
				}
				sharedNetworksFacadeService.getUserDevicesSharedNetworksService().update(snks);
			}
		}
		ModelCriteria mc_wdsnk = new ModelCriteria();
		//.andColumnEqualTo("sharednetwork_type", SharedNetworkType.SafeSecure.getKey())
		mc_wdsnk.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc_wdsnk.setOrderByClause("id desc");
		mc_wdsnk.setPageNumber(1);
		mc_wdsnk.setPageSize(200);
		EntityIterator<String, WifiDeviceSharedNetwork> itit_wdsnk = new KeyBasedEntityBatchIterator<String, WifiDeviceSharedNetwork>(String.class, WifiDeviceSharedNetwork.class, sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().getEntityDao(), mc_wdsnk);
		while(itit_wdsnk.hasNext()){
			List<WifiDeviceSharedNetwork> list = itit_wdsnk.next();
			for(WifiDeviceSharedNetwork snk:list){
				String sharednetwork_type = snk.getSharednetwork_type();
				if(StringUtils.isNotEmpty(sharednetwork_type)){
					SharedNetworkType snktype = SharedNetworkType.fromKey(sharednetwork_type);
					if(snktype != null){
						SharedNetworkSettingDTO snkDTO = snk.getInnerModel();
						if(snkDTO != null && snkDTO.getPsn() != null){
							ParamSharedNetworkDTO psn = snkDTO.getPsn();
							if(SharedNetworkType.SafeSecure == snktype){
								psn.setRemote_auth_url(psn.getRemote_auth_url().replace("123.56.227.18", "ucloud.bhuwifi.com"));
								psn.setPortal_server_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
								psn.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Open_resource);
							}else if(SharedNetworkType.SmsSecure == snktype){
								psn.setRemote_auth_url(psn.getRemote_auth_url().replace("123.56.227.18", "ucloud.bhuwifi.com"));
								psn.setPortal_server_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
								psn.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Open_resource);								
							}else if(SharedNetworkType.WeichatSecure == snktype){
								psn.setRemote_auth_url(psn.getRemote_auth_url().replace("123.56.227.18", "ucloud.bhuwifi.com"));
								psn.setPortal_server_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
								psn.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Open_resource);								
							}else{
								psn.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Uplink_Open_resource);
							}
							snk.putInnerModel(snkDTO);
							sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(snk);
							{
								if(!snkDTO.isOn()) continue;
								WifiDevice wifiDevice = wifiDeviceService.getById(snk.getId());
								if(wifiDevice != null && wifiDevice.isOnline()){
									String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,
											OperationDS.DS_SharedNetworkWifi_Start, snk.getId(), CMDBuilder.AutoGen,
											JsonHelper.getJSONString(psn), DeviceStatusExchangeDTO
													.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),
											deviceCMDGenFacadeService);
									daemonRpcService.wifiDeviceCmdDown(null, snk.getId(), sharedNetworkCMD);
									/*psn.switchWorkMode(WifiDeviceHelper.isWorkModeRouter(wifiDevice.getWork_mode()));
									//生成下发指令
									String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, 
											snk.getId(), -1,JsonHelper.getJSONString(psn),deviceCMDGenFacadeService);
									DaemonHelper.daemonCmdDown(snk.getId(), sharedNetworkCMD, daemonRpcService);*/
									//.afterDeviceOnline(mac, needLocationQuery,payloads, daemonRpcService);
									System.out.println(String.format("mac[%s] CMD[%s]", snk.getId(),sharedNetworkCMD));
								}else{
									System.out.println(String.format("mac[%s] not online", snk.getId()));
								}
							}
						}
					}else{
						//deleted
						sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().deleteById(snk.getId());
					}
				}
				
			}
		}
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
