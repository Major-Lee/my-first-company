package com.bhu.vas.di.op.device;

/**
 * 把数据库中记录的设备linkmode配置更新到redis中
 */
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class DeviceLinkModeRedisRepair {
	
	public static void main(String[] args) {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceSettingService  wifiDeviceSettingService = (WifiDeviceSettingService)ctx.getBean("wifiDeviceSettingService");
		DeviceFacadeService  deviceFacadeService = (DeviceFacadeService)ctx.getBean("deviceFacadeService");
		//修改设备的portal模板.
		ModelCriteria mc = new ModelCriteria();
		//.andColumnEqualTo("sharednetwork_type", SharedNetworkType.SafeSecure.getKey())
		mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc.setOrderByClause("id desc");
		mc.setPageNumber(1);
		mc.setPageSize(200);
		EntityIterator<String, WifiDeviceSetting> it = new KeyBasedEntityBatchIterator<String, WifiDeviceSetting>(String.class, WifiDeviceSetting.class, wifiDeviceSettingService.getEntityDao(), mc);
		while(it.hasNext()){
			List<WifiDeviceSetting> list = it.next();
			for(WifiDeviceSetting ws:list){
				WifiDeviceSettingDTO dto = ws.getInnerModel();
				deviceFacadeService.updateDeviceModeStatus(ws.getId(), dto.getLinkmode());
			}
		}
		System.exit(1);
	}
	
}
