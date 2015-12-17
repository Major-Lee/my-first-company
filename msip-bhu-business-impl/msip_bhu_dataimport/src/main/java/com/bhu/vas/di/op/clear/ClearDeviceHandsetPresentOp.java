package com.bhu.vas.di.op.clear;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 清除终端相关数据
 * @author edmond
 *
 */
public class ClearDeviceHandsetPresentOp {
//	public static List<String> device_macs = new ArrayList<String>();
//	
//	static{
//		device_macs.add("62:68:75:f1:10:80");
//	}
	
	
	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		//10天的毫秒数
		long reachThenCleanClear = 10*24*60*60*1000; 
		long reachThenPartClear = 5*24*60*60*1000;
		long deviceHandsetPresentSizeLimit = 1000;
		long current = System.currentTimeMillis();
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		Map<String,Long> result  = new HashMap<String,Long>();
		ModelCriteria mc = new ModelCriteria();
    	mc.setPageNumber(1);
    	mc.setPageSize(200);
		EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
				,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<String> devices = it.nextKeys();
			for(String mac:devices){
				long presentsize = WifiDeviceHandsetPresentSortedSetService.getInstance().presentSize(mac);
				result.put(mac, presentsize);
				List<String> removedHMacs = new ArrayList<String>();
				Map<String, String> allHandsets4Device = HandsetStorageFacadeService.allHandsets4Device(mac);
				if(!allHandsets4Device.isEmpty()){
					Iterator<Entry<String, String>> iter = allHandsets4Device.entrySet().iterator();
					while(iter.hasNext()){
						Entry<String, String> next = iter.next();
						String hmac = next.getKey();
						String value = next.getValue();
						if(StringUtils.isNotEmpty(value)){
							long action_ts = Long.parseLong(value.substring(1));
							long gap = current - action_ts;
							if(gap >= reachThenCleanClear){
								//1.清除对应的所有DeviceLog及流量统计 和清除DeviceHandset
								HandsetStorageFacadeService.wifiDeviceHandsetClear(mac,hmac);
								System.out.println(String.format("CleanClear mac[%s] hmac[%s] gap[%s]", mac,hmac,DateTimeHelper.getTimeDiff(gap)));
							}else if(gap < reachThenCleanClear && gap >=reachThenPartClear){
								//1.清除对应的部分DeviceLog
								HandsetStorageFacadeService.wifiDeviceHandsetPartClear(mac,hmac);
								System.out.println(String.format("PartClear mac[%s] hmac[%s] gap[%s]", mac,hmac,DateTimeHelper.getTimeDiff(gap)));
							}else{
								System.out.println(String.format("NoAction mac[%s] hmac[%s] gap[%s]", mac,hmac,DateTimeHelper.getTimeDiff(gap)));
								continue;
							}
							if(presentsize>deviceHandsetPresentSizeLimit){
								removedHMacs.add(hmac);
							}
						}
					}
					if(!removedHMacs.isEmpty()){
						WifiDeviceHandsetPresentSortedSetService.getInstance().removePresents(mac, removedHMacs);
						removedHMacs.clear();
					}
				}
			}
		}
		
		/*Map<String, Long> sorted = SortMapHelper.sortMapByValue(result);
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, Long>> iter = sorted.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Long> next = iter.next();
			sb.append(String.format("mac[%s] size[%s]\n", next.getKey(),next.getValue()));
		}
		
		FileHelper.generateFile("/BHUData/data/parser/present.txt", new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));*/
	}
	
/*	public static void doClearRedis(String mac){
		int start = 0;
		int size = 100;
		int count = 0;
		//遍历获取recent探测数据
		do{
			Set<String> recent_set = TerminalRecentSortedSetService.getInstance().fetchTerminalRecent(mac, start, size);
			if(recent_set == null || recent_set.isEmpty()){
				count = 0;
			}else{
				count = recent_set.size();
			}
			String[] recent_array = recent_set.toArray(new String[]{});
			//删除最后一次探测上线时间数据
			TerminalLastTimeStringService.getInstance().dels(mac, recent_array);
			//删除终端探测细节数据
			TerminalDetailRecentSortedSetService.getInstance().dels(mac, recent_array);
			//删除终端探测隔壁老王数据
			TerminalHotSortedSetService.getInstance().del(mac);
			//删除社区类型数据
			TerminalDeviceTypeCountHashService.getInstance().del(mac);
			
			start = start + size;
		}while(count == size);
		
		//删除recent探测数据
		TerminalRecentSortedSetService.getInstance().del(mac);
	}*/
}
