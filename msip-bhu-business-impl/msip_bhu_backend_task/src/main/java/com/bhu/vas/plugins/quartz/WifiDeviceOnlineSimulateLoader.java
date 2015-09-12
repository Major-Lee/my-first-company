package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.BusinessMarkerService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 每天零点开始执行
 * 对所有零点在线设备进行模拟登录指令日志，保证代理商统计日志的完整性
 * 写入到单独的log日志
 * 	  下发设备状态查询
 * @author Edmond Lee
 *
 */
public class WifiDeviceOnlineSimulateLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceOnlineSimulateLoader.class);
	
	public int bulk_success = 0;
	public int bulk_fail = 0;
	public int index_count = 0;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	public void execute() {
		logger.info("WifiDeviceOnlineSimulateLoader starting...");
		int total = 0;
		try{
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("online", 1);//.andColumnNotEqualTo("orig_model", WifiDeviceHelper.WIFI_URouter_DEVICE_ORIGIN_MODEL);
	    	mc.setPageNumber(1);
	    	mc.setPageSize(250);
			EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDevice> next = it.next();
				for(WifiDevice device:next){
					
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
		}finally{
		}
		logger.info(String.format("WifiDeviceOnlineActionLoader ended, total devices [%s]", total));
	}
}
