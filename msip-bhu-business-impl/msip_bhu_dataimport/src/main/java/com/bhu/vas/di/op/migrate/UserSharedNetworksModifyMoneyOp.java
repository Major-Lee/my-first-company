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
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 更新设备以及用户模板中的共享网络中的默认打赏金额为系统定义的缺省配置
 * @author Yetao
 *
 */
public class UserSharedNetworksModifyMoneyOp {
	public final static String OLD_PC_DEFAULT = "0.8-0.9";
	public final static String OLD_MOBILE_DEFAULT = "0.4-0.5";
	
	public static boolean modifyMoney(ParamSharedNetworkDTO dto){
		if(dto == null)
			return false;
		if(OLD_PC_DEFAULT.equals(dto.getRange_cash_pc()) && OLD_MOBILE_DEFAULT.equals(dto.getRange_cash_mobile()) && 
				ParamSharedNetworkDTO.Default_AIT.equals(dto.getAit_mobile())){
			dto.setRange_cash_mobile(ParamSharedNetworkDTO.Default_City_Range_Cash_Mobile);
			dto.setRange_cash_pc(ParamSharedNetworkDTO.Default_City_Range_Cash_PC);
			return true;
		}
		return false;
	}
	
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworksResetOpenResourceOp.class);
		ctx.start();
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		DeviceCMDGenFacadeService deviceCMDGenFacadeService = (DeviceCMDGenFacadeService)ctx.getBean("deviceCMDGenFacadeService");
		SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		IDaemonRpcService daemonRpcService =  (IDaemonRpcService)ctx.getBean("daemonRpcService");

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
				boolean modified = false;
				if(safesecure_configs != null && !safesecure_configs.isEmpty()){
					for(ParamSharedNetworkDTO dto:safesecure_configs){
						if(UserSharedNetworksModifyMoneyOp.modifyMoney(dto)){
							System.out.println(String.format("modifed for uid:[%s], tpl[%s]", snks.getId(), dto.getTemplate()));
							modified = true;
						}
					}
					snks.put(SharedNetworkType.SafeSecure.getKey(), safesecure_configs);
				}
				if(modified)
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
				if(!SharedNetworkType.SafeSecure.getKey().equals(snk.getSharednetwork_type()))
					continue;

				SharedNetworkSettingDTO snkDTO = snk.getInnerModel();
				if(snkDTO == null || snkDTO.getPsn() == null)
					continue;
				ParamSharedNetworkDTO psn = snkDTO.getPsn();
				if(!UserSharedNetworksModifyMoneyOp.modifyMoney(psn))
					continue;
				
				System.out.println(String.format("modifed for dev:[%s]", snk.getId()));
				snk.putInnerModel(snkDTO);
				sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(snk);
//				{
//					if(!snkDTO.isOn()) continue;
//					WifiDevice wifiDevice = wifiDeviceService.getById(snk.getId());
//					if(wifiDevice != null && wifiDevice.isOnline()){
//						String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,
//								OperationDS.DS_SharedNetworkWifi_Start, snk.getId(), CMDBuilder.AutoGen,
//								JsonHelper.getJSONString(psn), DeviceStatusExchangeDTO
//										.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),
//								deviceCMDGenFacadeService);
//						daemonRpcService.wifiDeviceCmdDown(null, snk.getId(), sharedNetworkCMD);
//						System.out.println(String.format("mac[%s] CMD[%s]", snk.getId(),sharedNetworkCMD));
//					}else{
//						System.out.println(String.format("mac[%s] not online", snk.getId()));
//					}
//				}
			}
		}
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
