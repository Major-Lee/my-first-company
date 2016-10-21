package com.bhu.vas.di.op.migrate;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.SharedNetworksHelper;
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
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 更新设备的uplink网络为打赏网络
 * @author Yetao
 *
 */
public class UserSharedNetworksUplinkToSafeSecureOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworksUplinkToSafeSecureOp.class);
		ctx.start();
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		DeviceCMDGenFacadeService deviceCMDGenFacadeService = (DeviceCMDGenFacadeService)ctx.getBean("deviceCMDGenFacadeService");
		SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService = (WifiDeviceSharedNetworkService)ctx.getBean("wifiDeviceSharedNetworkService");
		IDaemonRpcService daemonRpcService =  (IDaemonRpcService)ctx.getBean("daemonRpcService");
		
		ModelCriteria mc_wdsnk = new ModelCriteria();
		mc_wdsnk.createCriteria().andSimpleCaulse(" 1=1 ");
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
					if(snktype != SharedNetworkType.Uplink)
						continue;

					ParamSharedNetworkDTO psn = ParamSharedNetworkDTO.builderDefault(SharedNetworkType.SafeSecure.getKey());
					psn.setTemplate(SharedNetworksHelper.DefaultTemplate);
					psn.setTemplate_name(SharedNetworksHelper.buildTemplateName(SharedNetworkType.SafeSecure, SharedNetworksHelper.DefaultTemplate));
					psn.setTs(System.currentTimeMillis());
					snk.setSharednetwork_type(psn.getNtype());
					snk.setTemplate(SharedNetworksHelper.DefaultTemplate);
					SharedNetworkSettingDTO sharedNetworkSettingDTO = new SharedNetworkSettingDTO();
					if(snk.getInnerModel().isOn())
						sharedNetworkSettingDTO.turnOn(psn);
					else
						sharedNetworkSettingDTO.turnOff();
					snk.putInnerModel(sharedNetworkSettingDTO);
					wifiDeviceSharedNetworkService.update(snk);
					
					
					if(!sharedNetworkSettingDTO.isOn()) 
						continue;
					WifiDevice wifiDevice = wifiDeviceService.getById(snk.getId());
					if(wifiDevice != null && wifiDevice.isOnline()){
						String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,
								OperationDS.DS_SharedNetworkWifi_Start, snk.getId(), CMDBuilder.AutoGen,
								JsonHelper.getJSONString(psn), DeviceStatusExchangeDTO
										.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),
								deviceCMDGenFacadeService);
						daemonRpcService.wifiDeviceCmdDown(null, snk.getId(), sharedNetworkCMD);
						System.out.println(String.format("mac[%s] CMD[%s]", snk.getId(),sharedNetworkCMD));
					}else{
						System.out.println(String.format("mac[%s] not online", snk.getId()));
					}
				}
			}
		}
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
