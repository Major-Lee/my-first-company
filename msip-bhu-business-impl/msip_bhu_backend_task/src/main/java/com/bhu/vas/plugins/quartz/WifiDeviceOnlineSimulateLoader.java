package com.bhu.vas.plugins.quartz;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentCtxService;
import com.bhu.vas.business.logger.BusinessStatisticsLogger;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
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
	
	//@Resource
	//private WifiDeviceService wifiDeviceService;
	
	public void execute() {
		logger.info("WifiDeviceOnlineSimulateLoader starting...");
		//int total = 0;
		/*try{
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("online", 1);//.andColumnNotEqualTo("orig_model", WifiDeviceHelper.WIFI_URouter_DEVICE_ORIGIN_MODEL);
	    	mc.setPageNumber(1);
	    	mc.setPageSize(250);
			EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDevice> next = it.next();
				for(WifiDevice device:next){
					BusinessStatisticsLogger.doSimulateLoginActionLog(device.getId());
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
		}finally{
		}*/
		
		final AtomicInteger atomic = new AtomicInteger(0);
		//System.out.println("~~~~~~~~~~ gogogo:");
		WifiDevicePresentCtxService.getInstance().iteratorAll(new IteratorNotify<Map<String,String>>(){
			@Override
			public void notifyComming(Map<String, String> onlineMap) {
				//System.out.println("~~~~~~~~~~:"+onlineMap);
				Iterator<Entry<String, String>> iter = onlineMap.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, String> next = iter.next();
					String key = next.getKey();//mac
					//String value = next.getValue();//ctx
					BusinessStatisticsLogger.doSimulateLoginActionLog(key);
					//SessionManager.getInstance().addSession(key, value);
					atomic.incrementAndGet();
					//System.out.println(String.format("Online device[%s] ctx[%s]", key,value));
				}
				//System.out.println(t);
			}
		});
		//System.out.println(String.format("Init Online count[%s] Ok~~~~~~",atomic.get()));
		
		logger.info(String.format("WifiDeviceOnlineSimulateLoader ended, total devices [%s]", atomic.get()));
	}
}
