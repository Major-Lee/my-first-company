package com.bhu.vas.di.op.migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.dto.DistributorType;
import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.SharedNetworksHelper;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * OPS系统城市运营商可管理设备portal模板.
 * 1. 所有非运营商用户的portla模板列表，金额也修改为0.1-0.2
 * 1. 把所有城市运营商设备的portal切换为该运营商的默认模板
 * 2. 所有渠道设备的portal模板，修改金额为0.1-0.2
 * @author Edmond
 *
 */
public class UserSharedNetworksForCityOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworksForCityOp.class);
		ctx.start();
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		UserService userService = (UserService)ctx.getBean("userService");
		DeviceCMDGenFacadeService deviceCMDGenFacadeService = (DeviceCMDGenFacadeService)ctx.getBean("deviceCMDGenFacadeService");
		SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		ChargingFacadeService chargingFacadeService = (ChargingFacadeService)ctx.getBean("chargingFacadeService");
		IDaemonRpcService daemonRpcService =  (IDaemonRpcService)ctx.getBean("daemonRpcService");

		ModelCriteria mc_user = new ModelCriteria();
		mc_user.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("utype", UserType.URBANOPERATORS.getIndex());
		mc_user.setOrderByClause("id desc");
		mc_user.setPageNumber(1);
		mc_user.setPageSize(200);
		System.out.println("init city users portal template");
    	EntityIterator<Integer, User> itit_user = new KeyBasedEntityBatchIterator<Integer, User>(Integer.class, User.class, userService.getEntityDao(), mc_user);
		while(itit_user.hasNext()){
			List<User> list = itit_user.next();
			for(User user:list){
				System.out.println("checking or init safesecure sharednetwork for user" + user.getId());
				//如果用户没有打赏网络，会触发创建一个默认的0001模板
		    	sharedNetworksFacadeService.fetchUserSharedNetworkConf(user.getId(), VapEnumType.SharedNetworkType.SafeSecure);
			}
		}
		
		ModelCriteria mc_udsnk = new ModelCriteria();
		mc_udsnk.createCriteria().andSimpleCaulse(" 1=1 ");
		mc_udsnk.setOrderByClause("id desc");
		mc_udsnk.setPageNumber(1);
		mc_udsnk.setPageSize(200);
		System.out.println("modify other users portal money range");
    	EntityIterator<Integer, UserDevicesSharedNetworks> itit_udsnk = new KeyBasedEntityBatchIterator<Integer, UserDevicesSharedNetworks>(Integer.class, UserDevicesSharedNetworks.class, sharedNetworksFacadeService.getUserDevicesSharedNetworksService().getEntityDao(), mc_udsnk);
		while(itit_udsnk.hasNext()){
			List<UserDevicesSharedNetworks> list = itit_udsnk.next();
			List<Integer> uidList = new ArrayList<Integer>(list.size());
			for(UserDevicesSharedNetworks snks:list){
				uidList.add(snks.getId());
			}
			List<User> userlist = userService.findByIds(uidList);
			Map<Integer, User> userMap = new HashMap<Integer, User>(userlist.size());
			for(User user:userlist)
				userMap.put(user.getId(), user);
			for(UserDevicesSharedNetworks snks:list){
				System.out.println(String.format("uid[%s]", snks.getId()));
				User user = userMap.get(snks.getId());
				if(user == null)
					continue;
				List<ParamSharedNetworkDTO> safesecure_configs = snks.get(SharedNetworkType.SafeSecure.getKey());
				if(safesecure_configs != null && !safesecure_configs.isEmpty()){
					for(ParamSharedNetworkDTO dto:safesecure_configs){
						if(UserType.URBANOPERATORS.getIndex() == user.getUtype()){
							
						} else {
							System.out.println(String.format("uid[%s] is not city user, modify it's template money range", snks.getId()));
							dto.setRange_cash_mobile(ParamSharedNetworkDTO.Default_Channel_Range_Cash_Mobile);
							dto.setRange_cash_pc(ParamSharedNetworkDTO.Default_Channel_Range_Cash_PC);
							dto.setFree_ait_mobile(ParamSharedNetworkDTO.Default_Free_AIT);
							dto.setFree_ait_pc(ParamSharedNetworkDTO.Default_Free_AIT);
							dto.setAit_mobile(ParamSharedNetworkDTO.Default_AIT);
							dto.setAit_pc(ParamSharedNetworkDTO.Default_AIT);
						}
					}
					snks.put(SharedNetworkType.SafeSecure.getKey(), safesecure_configs);
				}
				sharedNetworksFacadeService.getUserDevicesSharedNetworksService().update(snks);
			}
		}
		

		//修改设备的portal模板.
		ModelCriteria mc_wdsnk = new ModelCriteria();
		//.andColumnEqualTo("sharednetwork_type", SharedNetworkType.SafeSecure.getKey())
		mc_wdsnk.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc_wdsnk.setOrderByClause("id desc");
		mc_wdsnk.setPageNumber(1);
		mc_wdsnk.setPageSize(200);
		EntityIterator<String, WifiDeviceSharedNetwork> itit_wdsnk = new KeyBasedEntityBatchIterator<String, WifiDeviceSharedNetwork>(String.class, WifiDeviceSharedNetwork.class, sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().getEntityDao(), mc_wdsnk);
		while(itit_wdsnk.hasNext()){
			List<WifiDeviceSharedNetwork> list = itit_wdsnk.next();
			System.out.println("batch getting sharedealconfigs for devices:" + list.size());
			List<String> wdidlist = new ArrayList<String>(list.size());
			for(WifiDeviceSharedNetwork snk:list)
				wdidlist.add(snk.getId());
			List<WifiDeviceSharedealConfigs> wsdcfgList = chargingFacadeService.getWifiDeviceSharedealConfigsService().findByIds(wdidlist);
			Map<String, WifiDeviceSharedealConfigs> wsdcfgmap = new HashMap<String, WifiDeviceSharedealConfigs>(wsdcfgList.size());
			List<Integer> didlist = new ArrayList<Integer>(wsdcfgList.size());
			for(WifiDeviceSharedealConfigs wsdcfg:wsdcfgList){
				wsdcfgmap.put(wsdcfg.getId(),  wsdcfg);
				if(wsdcfg.getDistributor() != -1)
					didlist.add(Integer.valueOf(wsdcfg.getDistributor()));
			}
			Map<Integer, ParamSharedNetworkDTO> psnMap = new HashMap<Integer, ParamSharedNetworkDTO>(wsdcfgList.size());
			if(didlist.size() > 0){
				List<UserDevicesSharedNetworks> usnkList = sharedNetworksFacadeService.getUserDevicesSharedNetworksService().findByIds(didlist);
				for(UserDevicesSharedNetworks usnk:usnkList){
					List<ParamSharedNetworkDTO> models = usnk.get(VapEnumType.SharedNetworkType.SafeSecure.getKey(),new ArrayList<ParamSharedNetworkDTO>(),true);
					if(models.isEmpty()){
						System.out.println("Error: no default safesecure template for user:" + usnk.getId());
					}else{
						psnMap.put(usnk.getId(), models.get(0));
					}
				}
			}
			
			for(WifiDeviceSharedNetwork snk:list){
				WifiDeviceSharedealConfigs wsdcfg = wsdcfgmap.get(snk.getId());
				if(wsdcfg == null){
					System.out.println("dev:" + snk.getId() + " has no sharedel configs");
					continue;
				}
				ParamSharedNetworkDTO psn = psnMap.get(Integer.valueOf(wsdcfg.getDistributor()));
				if(!DistributorType.City.getType().equals(wsdcfg.getDistributor_type())){
					System.out.println("dev:" + snk.getId() + " not belong to a city distributor, modify device portal money");
					SharedNetworkSettingDTO snkDTO = snk.getInnerModel();
					ParamSharedNetworkDTO wpsn = snkDTO.getPsn();
					snk.setOwner(wsdcfg.getOwner());
					if(wpsn != null){
						wpsn.setRange_cash_mobile(ParamSharedNetworkDTO.Default_Channel_Range_Cash_Mobile);
						wpsn.setRange_cash_pc(ParamSharedNetworkDTO.Default_Channel_Range_Cash_PC);
						wpsn.setFree_ait_mobile(ParamSharedNetworkDTO.Default_Free_AIT);
						wpsn.setFree_ait_pc(ParamSharedNetworkDTO.Default_Free_AIT);
						wpsn.setAit_mobile(ParamSharedNetworkDTO.Default_AIT);
						wpsn.setAit_pc(ParamSharedNetworkDTO.Default_AIT);
						snkDTO.setPsn(wpsn);
						snk.putInnerModel(snkDTO);
					}
					sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(snk);
					continue;
				}
				if(wsdcfg.getDistributor() <= 0){
					System.out.println("dev:" + snk.getId() + " not a valid distributor");
					continue;
				}
				if(psn == null){
					System.out.println("dev:" + snk.getId() + " user:" + wsdcfg.getDistributor() + " has no portal template");
					continue;
				}
				SharedNetworkType snktype = SharedNetworkType.fromKey(snk.getSharednetwork_type());
				if(snktype != SharedNetworkType.SafeSecure){
					System.out.println("dev:" + snk.getId() + " is not a safesecure network");
					continue;
				}
				if(snk.getOwner() != null && snk.getOwner() == wsdcfg.getDistributor()){
					System.out.println("dev:" + snk.getId() + " already using it's distributor's portal template");
					continue;
				}
				System.out.println("dev:" + snk.getId() + " changing to it's distributor's portal template");
				snk.setOwner(wsdcfg.getDistributor());
				snk.setTemplate(SharedNetworksHelper.DefaultTemplate);
				SharedNetworkSettingDTO snkDTO = snk.getInnerModel();
				snkDTO.setPsn(psn);
				if(!wsdcfg.isCanbe_turnoff())
					snkDTO.setOn(true);
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
