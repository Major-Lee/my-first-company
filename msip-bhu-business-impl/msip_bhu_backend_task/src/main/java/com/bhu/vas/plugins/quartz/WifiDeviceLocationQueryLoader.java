package com.bhu.vas.plugins.quartz;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.daemon.helper.DaemonHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class WifiDeviceLocationQueryLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceLocationQueryLoader.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;
	public void execute() {
		logger.info("WifiDeviceLocationQueryLoader starting...");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", 1).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
    	mc.setPageNumber(1);
    	mc.setPageSize(200);
    	int count = 0;
		EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
				,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<WifiDevice> entitys = it.next();
			for(WifiDevice device:entitys){
				if(StringUtils.isEmpty(device.getLat()) || StringUtils.isEmpty(device.getLon())){
					//if('1.2.12','1.2.11','1.2.10'){
					if(device.getOrig_swver().indexOf("1.2.12") >=0 
							|| device.getOrig_swver().indexOf("1.2.11")>=0
							|| device.getOrig_swver().indexOf("1.2.10")>=0){
						DaemonHelper.locationStep1Query(device.getId(), daemonRpcService);
						count++;
					}
				}
			}
		}
		logger.info("WifiDeviceLocationQueryLoader end:"+count);
	}
}
