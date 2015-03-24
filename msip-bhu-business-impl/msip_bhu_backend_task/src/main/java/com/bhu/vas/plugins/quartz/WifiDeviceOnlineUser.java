package com.bhu.vas.plugins.quartz;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class WifiDeviceOnlineUser {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceOnlineUser.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	public void execute() {
		logger.info("WifiDeviceOnlineUser starting...");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()
				.andColumnEqualTo("locked", false)
				.andColumnEqualTo("validated", true);
		//mc.setOrderByClause(" created_at ");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
				,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<WifiDevice> entitys = it.next();
			for(WifiDevice device:entitys){
				String wifi_mac = device.getId();
				long count = WifiDeviceHandsetPresentSortedSetService.getInstance().presentNotOfflineSize(wifi_mac);
			}
			/*boolean bulk_result = userIndexService.createIndexsByEntitys(entitys);
			if(bulk_result){
				bulk_success++;
			}else{
				bulk_fail++;
			}
			index_count = index_count + entitys.size();*/
		}
		logger.info("WifiDeviceOnlineUser ended");
	}
}
