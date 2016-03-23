package com.bhu.vas.di.op.migrate;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.dto.ret.param.ParamVapVistorWifiDTO;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.user.dto.UserVistorWifiSettingDTO;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.business.ds.device.facade.SharedNetworkFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
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
public class UserSharedNetworkMigrateEnvOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworkMigrateEnvOp.class);
		ctx.start();
		UserService userService = (UserService)ctx.getBean("userService");
		SharedNetworkFacadeService sharedNetworkFacadeService = (SharedNetworkFacadeService)ctx.getBean("sharedNetworkFacadeService");
		UserSettingStateService userSettingStateService = (UserSettingStateService)ctx.getBean("userSettingStateService");
		WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService= (WifiDeviceIndexIncrementService)ctx.getBean("wifiDeviceIndexIncrementService");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc.setOrderByClause("id desc");
    	mc.setPageNumber(1);
    	mc.setPageSize(1000);
    	EntityIterator<String, UserSettingState> itit = new KeyBasedEntityBatchIterator<String, UserSettingState>(String.class, UserSettingState.class, userSettingStateService.getEntityDao(), mc);
		while(itit.hasNext()){
			List<UserSettingState> list = itit.next();
			for(UserSettingState setting:list){
				UserVistorWifiSettingDTO vistorSetting = setting.getUserSetting(UserVistorWifiSettingDTO.Setting_Key, UserVistorWifiSettingDTO.class);
				if(vistorSetting != null){
					boolean on = vistorSetting.isOn();
					boolean ds = vistorSetting.isDs();
					ParamVapVistorWifiDTO vw = vistorSetting.getVw();
					WifiDeviceSharedNetwork sharednetwork = sharedNetworkFacadeService.getWifiDeviceSharedNetworkService().getById(setting.getId());
					if(sharednetwork == null){
						sharednetwork = new WifiDeviceSharedNetwork();
						sharednetwork.setId(setting.getId());
						if(on){
							sharednetwork.setSharednetwork_type(SharedNetworkType.Uplink.getKey());
						}
							
						SharedNetworkSettingDTO sharedNetworkSettingDTO = new SharedNetworkSettingDTO();
						if(ds)
							sharedNetworkSettingDTO.setDs(ds);
						if(on){
							sharedNetworkSettingDTO.setOn(on);
							ParamSharedNetworkDTO psn = new ParamSharedNetworkDTO();
							psn.setNtype(SharedNetworkType.Uplink.getKey());
							psn.setSsid(vw.getSsid());
							psn.setUsers_rx_rate(vw.getUsers_rx_rate());
							psn.setUsers_tx_rate(vw.getUsers_tx_rate());
							sharedNetworkSettingDTO.setPsn(ParamSharedNetworkDTO.fufillWithDefault(psn));
						}
						sharedNetworkSettingDTO.setC(vistorSetting.getC()==0?System.currentTimeMillis():vistorSetting.getC());
						sharednetwork.putInnerModel(sharedNetworkSettingDTO);
						sharedNetworkFacadeService.getWifiDeviceSharedNetworkService().insert(sharednetwork);
					}else{
						if(on)
							sharednetwork.setSharednetwork_type(SharedNetworkType.Uplink.getKey());
						SharedNetworkSettingDTO sharedNetworkSettingDTO = new SharedNetworkSettingDTO();
						if(ds)
							sharedNetworkSettingDTO.setDs(ds);
						if(on){
							sharedNetworkSettingDTO.setOn(on);
							ParamSharedNetworkDTO psn = new ParamSharedNetworkDTO();
							psn.setNtype(SharedNetworkType.Uplink.getKey());
							psn.setSsid(vw.getSsid());
							psn.setUsers_rx_rate(vw.getUsers_rx_rate());
							psn.setUsers_tx_rate(vw.getUsers_tx_rate());
							sharedNetworkSettingDTO.setPsn(ParamSharedNetworkDTO.fufillWithDefault(psn));
						}
						sharedNetworkSettingDTO.setC(vistorSetting.getC()==0?System.currentTimeMillis():vistorSetting.getC());
						sharednetwork.putInnerModel(sharedNetworkSettingDTO);
						sharedNetworkFacadeService.getWifiDeviceSharedNetworkService().update(sharednetwork);
					}
					wifiDeviceIndexIncrementService.sharedNetworkUpdIncrement(setting.getId(), sharednetwork.getSharednetwork_type());
					System.out.println(String.format("mac[%s] sharednetwork_type[%s]", setting.getId(), sharednetwork.getSharednetwork_type()));
				}
			}
		}
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
