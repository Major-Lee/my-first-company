package com.bhu.vas.di.op.migrate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
public class DeviceSharedNetworksResendEnvOp {
	public static void main(String[] argv){
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, DeviceSharedNetworksResendEnvOp.class);
		ctx.start();
		System.out.println("Remote_auth_url:"+BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Remote_auth_url);
		System.out.println("Uplink:"+BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Uplink_Open_resource);
		System.out.println("SafeSecure:"+BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Open_resource);
		System.out.println("SafeSecure:"+BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
		
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		DeviceCMDGenFacadeService deviceCMDGenFacadeService = (DeviceCMDGenFacadeService)ctx.getBean("deviceCMDGenFacadeService");
		SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		IDaemonRpcService daemonRpcService =  (IDaemonRpcService)ctx.getBean("daemonRpcService");
		
		AtomicInteger matched = new AtomicInteger(0);
		AtomicInteger matched_online = new AtomicInteger(0);
		ModelCriteria mc_wdsnk = new ModelCriteria();
		mc_wdsnk.createCriteria().andColumnEqualTo("sharednetwork_type", "SafeSecure").andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc_wdsnk.setOrderByClause("id desc");
		mc_wdsnk.setPageNumber(1);
		mc_wdsnk.setPageSize(200);
		EntityIterator<String, WifiDeviceSharedNetwork> itit_wdsnk = new KeyBasedEntityBatchIterator<String, WifiDeviceSharedNetwork>(String.class, WifiDeviceSharedNetwork.class, sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().getEntityDao(), mc_wdsnk);
		while(itit_wdsnk.hasNext()){
			List<WifiDeviceSharedNetwork> list = itit_wdsnk.next();
			for(WifiDeviceSharedNetwork snk:list){
				//System.out.println(String.format("mac[%s] hdtype[%s]", snk.getId(),""));
				String sharednetwork_type = snk.getSharednetwork_type();
				if(StringUtils.isNotEmpty(sharednetwork_type)){
					SharedNetworkType snktype = SharedNetworkType.fromKey(sharednetwork_type);
					if(snktype  == SharedNetworkType.SafeSecure){//安全共享网络
						SharedNetworkSettingDTO snkDTO = snk.getInnerModel();
						if(snkDTO != null && snkDTO.getPsn() != null && snkDTO.isOn()){
							matched.incrementAndGet();
							WifiDevice wifiDevice = wifiDeviceService.getById(snk.getId());
							if(wifiDevice != null && wifiDevice.isOnline() && !"H106".equals(wifiDevice.getHdtype())){
								System.out.println(String.format("mac[%s] hdtype[%s]", snk.getId(),wifiDevice.getHdtype()));
								ParamSharedNetworkDTO psn = snkDTO.getPsn();
								matched_online.incrementAndGet();
								String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,
										OperationDS.DS_SharedNetworkWifi_Start, snk.getId(), CMDBuilder.AutoGen,
										JsonHelper.getJSONString(psn), DeviceStatusExchangeDTO
												.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),
										deviceCMDGenFacadeService);
								daemonRpcService.wifiDeviceCmdDown(null, snk.getId(), sharedNetworkCMD);
								System.out.println(String.format("mac[%s] CMD[%s]", snk.getId(),sharedNetworkCMD));
							}else{
								System.out.println(String.format("mac[%s] device not exist or offline", snk.getId()));
							}
							
						}
					}
				}
				
			}
		}
		System.out.println("total:"+itit_wdsnk.getTotalItemsCount()+ " macthed:"+matched.get() +" online:"+matched_online.get());
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
