package com.bhu.vas.di.op.migrate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetwork;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.ds.device.facade.SharedNetworkFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 用户unique 
 * 		手机号重写入redis
 * 		昵称重写入redis
 * @author Edmond
 *
 */
public class UserSharedNetworksMigrateEnvOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworksMigrateEnvOp.class);
		ctx.start();
		UserService userService = (UserService)ctx.getBean("userService");
		SharedNetworkFacadeService sharedNetworkFacadeService = (SharedNetworkFacadeService)ctx.getBean("sharedNetworkFacadeService");
		SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		//UserSettingStateService userSettingStateService = (UserSettingStateService)ctx.getBean("userSettingStateService");
		WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService= (WifiDeviceIndexIncrementService)ctx.getBean("wifiDeviceIndexIncrementService");
		
		
		//迁移用户配置数据
		ModelCriteria mc_usersharednetwork = new ModelCriteria();
		mc_usersharednetwork.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc_usersharednetwork.setOrderByClause("id desc");
		mc_usersharednetwork.setPageNumber(1);
		mc_usersharednetwork.setPageSize(200);
    	EntityIterator<Integer, UserDevicesSharedNetwork> itit = new KeyBasedEntityBatchIterator<Integer, UserDevicesSharedNetwork>(Integer.class, UserDevicesSharedNetwork.class, sharedNetworkFacadeService.getUserDevicesSharedNetworkService().getEntityDao(), mc_usersharednetwork);
		while(itit.hasNext()){
			
			
			List<UserDevicesSharedNetwork> list = itit.next();
			for(UserDevicesSharedNetwork sdn:list){
				UserDevicesSharedNetworks current = sharedNetworksFacadeService.getUserDevicesSharedNetworksService().getById(sdn.getId());
				if(current != null) continue;
				
				ParamSharedNetworkDTO safeSecure = sdn.getInnerModel(SharedNetworkType.SafeSecure.getKey());
				ParamSharedNetworkDTO uplink = sdn.getInnerModel(SharedNetworkType.Uplink.getKey());
				
				UserDevicesSharedNetworks sdns = new UserDevicesSharedNetworks();				
				sdns.setId(sdn.getId());
				sdns.setUpdated_at(sdn.getUpdated_at());
				
				if(safeSecure != null){
					List<ParamSharedNetworkDTO> dtos = new ArrayList<>();
					safeSecure.setTemplate(SharedNetworksFacadeService.DefaultTemplate);
					dtos.add(safeSecure);
					sdns.put(SharedNetworkType.SafeSecure.getKey(), dtos);
				}
				if(uplink != null){
					List<ParamSharedNetworkDTO> dtos = new ArrayList<>();
					uplink.setTemplate(SharedNetworksFacadeService.DefaultTemplate);
					dtos.add(uplink);
					sdns.put(SharedNetworkType.Uplink.getKey(), dtos);
				}
				sharedNetworksFacadeService.getUserDevicesSharedNetworksService().insert(sdns);
			}
		}
		//修改设备配置数据 增加template
		ModelCriteria mc_devicesharednetwork = new ModelCriteria();
		mc_devicesharednetwork.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc_devicesharednetwork.setOrderByClause("id desc");
		mc_devicesharednetwork.setPageNumber(1);
		mc_devicesharednetwork.setPageSize(200);
    	EntityIterator<String, WifiDeviceSharedNetwork> iter = new KeyBasedEntityBatchIterator<String, WifiDeviceSharedNetwork>(String.class, WifiDeviceSharedNetwork.class, sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().getEntityDao(), mc_devicesharednetwork);
    	while(iter.hasNext()){
			List<WifiDeviceSharedNetwork> list = iter.next();
			for(WifiDeviceSharedNetwork sdn:list){
				sdn.setTemplate(SharedNetworksFacadeService.DefaultTemplate);
				SharedNetworkSettingDTO innerModel = sdn.getInnerModel();
				if(innerModel.getPsn() != null){
					innerModel.getPsn().setTemplate(SharedNetworksFacadeService.DefaultTemplate);
				}
				sdn.replaceInnerModel(innerModel);
				sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(sdn);
				wifiDeviceIndexIncrementService.sharedNetworkUpdIncrement(sdn.getId(), sdn.getSharednetwork_type(),sdn.getTemplate());
			}
    	}
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
