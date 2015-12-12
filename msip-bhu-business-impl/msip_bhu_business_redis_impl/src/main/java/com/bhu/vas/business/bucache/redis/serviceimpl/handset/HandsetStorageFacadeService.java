package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.HandsetLogDTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
 * 考虑以后设备所属的终端量非常多的情况，类似拆表数据存储实现机制，并且不用通过数据库遍历可以把所有数据提取出来
 * 目前拆成20000个redis hash存储数据 可以支持2千w的数据散列
 * 拆分对象为mac地址，存储数据为Handset信息数据HandsetDeviceDTO
 * @author edmond
 *
 */
public class HandsetStorageFacadeService{
	
	public static Long handsetComming(HandsetDeviceDTO dto){
		//HandsetStatisticsService.getInstance().online(dto.wasOnline());
		return HandsetStorageService.getInstance().handsetComming(dto);
		//String mac = dto.getMac();
		//this.hset(generateKey(mac), mac, JsonHelper.getJSONString(dto));
	}
	
	public static List<Object> handsetsComming(List<HandsetDeviceDTO> dtos){
		//HandsetStatisticsService.getInstance().online(true, dtos.size());
		return HandsetStorageService.getInstance().handsetsComming(dtos);
		//String[][] keyAndFields = generateKeyAndFieldsAndValues(dtos);
		//this.pipelineHSet_diffKeyWithDiffFieldValue(keyAndFields[0], keyAndFields[1], keyAndFields[2]);
	}
	
	public static HandsetDeviceDTO handset(String mac){
		return HandsetStorageService.getInstance().handset(mac);
	}
	
	public static List<HandsetDeviceDTO> handsets(List<String> macs){
		return HandsetStorageService.getInstance().handsets(macs);
	}
	
/*	public void handsetLeave(String mac){
		this.hdel(generateKey(wifiId),wifiId);
	}*/
	
	public static void clearOrResetAll(){
		HandsetStorageService.getInstance().clearOrResetAll();
	}
	
	/**
	 * 读取速度相对慢，可以在生产环境运行
	 * @return
	 */
	public static long countAll(){
		return HandsetStorageService.getInstance().countAll();
	}
	
	public static void iteratorAll(IteratorNotify<Map<String,String>> notify){
		HandsetStorageService.getInstance().iteratorAll(notify);
	}
	
	
	public static void statisticsSet(long online,long total){
		HandsetStatisticsService.getInstance().statisticsSet(online,total);
	}
	
	public static int[] statistics(){
		return HandsetStatisticsService.getInstance().statistics();
	}
	
	
    /**
	 * 设备非法关机，断开长连接后通知所有终端离线，设备端上报的终端流量统计为0.
	 * @param wifiId
	 */
	public static void wifiDeviceIllegalOffline(String dmac, List<HandsetDeviceDTO> handsets) {
        if (handsets != null) {
        	long ts = System.currentTimeMillis();
            for(HandsetDeviceDTO dto:handsets){
                if(dto != null){
                	DeviceHandsetLogService.getInstance().hansetLogComming(false, dmac, dto.getMac(), 0l, ts);//(wifiId, dto.getMac(), "0", System.currentTimeMillis());
                }
            }
        }
	}
	
    /**
     * 终端离线更新记录
     * @param wifiId
     * @param handsetId
     * @param tx_bytes
     * @param logout_at
     */
    public static void wifiDeviceHandsetOffline(String dmac, String hmac, String tx_bytes, long logout_at) {
    	System.out.println(String.format("wifiDeviceHandsetOffline dmac[%s] hmac[%s] logout_at[%s]", dmac,hmac,logout_at));
    	long rb = Long.parseLong(tx_bytes);
    	DeviceHandsetLogService.getInstance().hansetLogComming(false, dmac, hmac, rb, logout_at);
    	if(rb >0){
    		DeviceHandsetExtFieldService.getInstance().increaseTrb(dmac, hmac, rb);
    	}
    }

    
    public static int wifiDeviceHandsetOnline(String dmac, String hmac, long last_login_at){
    	System.out.println(String.format("wifiDeviceHandsetOnline dmac[%s] hmac[%s] last_login_at[%s]", dmac,hmac,last_login_at));
    	return DeviceHandsetLogService.getInstance().hansetLogComming(true, dmac, hmac, 0l, last_login_at);
    }
	
    
    public static List<HandsetLogDTO> wifiDeviceHandsetRecentLogs(String dmac, String hmac,int size){
    	
    	return DeviceHandsetLogService.getInstance().fetchRecentHandsetLogs(dmac, hmac, size);
    }
    
    public static List<HandsetLogDTO> wifiDeviceHandsetAllLogs(String dmac, String hmac){
    	return DeviceHandsetLogService.getInstance().fetchHandsetAllLogs(dmac, hmac);
    }
    
    public static long wifiDeviceHandsetTrbFetched(String dmac, String hmac){
    	long trb = DeviceHandsetExtFieldService.getInstance().trb(dmac, hmac);
    	System.out.println(String.format("wifiDeviceHandsetTrbFetched dmac[%s] hmac[%s] trb[%s]", dmac,hmac,trb));
    	return trb;//.fetchRecentHandsetLogs(dmac, hmac, size);
    }
    
    public static long wifiDeviceHandsetLogsClear(String dmac, String hmac){
    	DeviceHandsetExtFieldService.getInstance().trbclear(dmac, hmac);
    	return DeviceHandsetLogService.getInstance().handsetLogsClear(dmac, hmac);
    }
    
	public static void main(String[] argc){
		//HandsetStorageService.getInstance().clearOrResetAll();
		/*HandsetStorageFacadeService.iteratorAll(new IteratorNotify<Map<String,String>>(){
			@Override
			public void notifyComming(Map<String, String> t) {
				System.out.println(t);
			}
		});*/
		/*long countAll = HandsetStorageFacadeService.countAll();
		System.out.println(countAll);
		HandsetDeviceDTO handset = HandsetStorageFacadeService.handset("88:32:9b:32:41:10");
		System.out.println(handset.getMac());
		System.out.println(handset.getDhcp_name());
		
		int[] statistics = HandsetStorageFacadeService.statistics();
		
		System.out.println(statistics[0]);
		System.out.println(statistics[1]);*/
		
		
		
		/*List<HandsetLogDTO> recentLogs = HandsetStorageFacadeService.wifiDeviceHandsetRecentLogs("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e", 100);
		for(HandsetLogDTO dto :recentLogs){
			System.out.println(JsonHelper.getJSONString(dto));
		}
		
		System.out.println(new Date(1449836882759l));
		System.out.println(new Date(1449837003638l));
		System.out.println(new Date(1449834460661l));
		System.out.println(new Date(1449835127772l));*/
		HandsetStorageFacadeService.wifiDeviceHandsetLogsClear("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e");
		HandsetStorageFacadeService.wifiDeviceHandsetOnline("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e", DateTimeHelper.getDateDaysAgo(10).getTime());
		HandsetStorageFacadeService.wifiDeviceHandsetOffline("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e","403999333", DateTimeHelper.getDateDaysAgo(7).getTime());
		
		
		
		Date start = DateTimeHelper.getDateDaysAgo(3);
		HandsetStorageFacadeService.wifiDeviceHandsetOnline("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e", start.getTime());
		HandsetStorageFacadeService.wifiDeviceHandsetOffline("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e","1235566", start.getTime()+3*60*60*1000);
		
		HandsetStorageFacadeService.wifiDeviceHandsetOnline("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e", start.getTime()+4*60*60*1000);
		HandsetStorageFacadeService.wifiDeviceHandsetOffline("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e","1231166", start.getTime()+5*60*60*1000);
		
		HandsetStorageFacadeService.wifiDeviceHandsetOnline("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e", System.currentTimeMillis());
		HandsetStorageFacadeService.wifiDeviceHandsetOffline("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e","7999333", DateTimeHelper.getDateDaysAfter(1).getTime());
		List<HandsetLogDTO> allLogs = wifiDeviceHandsetAllLogs("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e");
		for(HandsetLogDTO dto :allLogs){
			System.out.println(JsonHelper.getJSONString(dto));
		}
		System.out.println("-----------------------");
		List<HandsetLogDTO> recentLogs = HandsetStorageFacadeService.wifiDeviceHandsetRecentLogs("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e", 5);
		for(HandsetLogDTO dto :recentLogs){
			System.out.println(JsonHelper.getJSONString(dto));
		}
		
		/*
		System.out.println(new Date(1449836882759l));
		System.out.println(new Date(1449837003638l));
		System.out.println(new Date(1449834460661l));
		System.out.println(new Date(1449835127772l));*/
		
		/*List<HandsetLogDTO> recentLogs = HandsetStorageFacadeService.wifiDeviceHandsetRecentLogs("84:82:f4:23:06:68", "3c:d0:f8:e9:b3:2e", 100);
		for(HandsetLogDTO dto :recentLogs){
			System.out.println(JsonHelper.getJSONString(dto));
		}
		
		Map<String, List<WifiHandsetDeviceItemDetailMDTO>> buildHdDetailMap = buildHdDetailMap(recentLogs);
		System.out.println(buildHdDetailMap);*/
	}
	
	
	
	/**
	 * 如果一段日志跨天了，需要拆分出来
	 * @param recentLogs
	 * @return Map<String,List<WifiHandsetDeviceItemDetailMDTO>> date,list
	 *//*
	private static Map<String,List<WifiHandsetDeviceItemDetailMDTO>> buildHdDetailMap(List<HandsetLogDTO> recentLogs){
		Map<String,List<WifiHandsetDeviceItemDetailMDTO>> result = new HashMap<>();
		for(HandsetLogDTO log:recentLogs){
			boolean completed = true;
			long o = log.getO();
			long f = log.getF();
			long trb = log.getTrb();
			if(f == 0){
				f = System.currentTimeMillis();
				completed = false;
			}
			Date login = new Date(o);
			Date logout = new Date(f);
			if(DateUtils.isSameDay(login, logout)){
				String date  = DateTimeHelper.formatDate(login, DateTimeHelper.FormatPattern5);
				add2Result(result,date,new WifiHandsetDeviceItemDetailMDTO(login.getTime(),completed?logout.getTime():0l,trb));
			}else{//跨天
				long days = DateTimeHelper.getTwoDateDifferentDay(logout,login, DateTimeHelper.FormatPattern5);
				for(int i=0;i<=days;i++){
					if(i == 0){
						String date  = DateTimeHelper.formatDate(login, DateTimeHelper.FormatPattern5);
						Date end = DateTimeHelper.getCertainDateEnd(login);
						add2Result(result,date,new WifiHandsetDeviceItemDetailMDTO(login.getTime(),end.getTime(),0l));
					}else if(i==days){
						Date current = DateTimeHelper.getDateDaysAfter(login, i);
						String date  = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern5);
						Date start 	= DateTimeHelper.getCertainDateStart(current);
						add2Result(result,date,new WifiHandsetDeviceItemDetailMDTO(start.getTime(),completed?logout.getTime():0l,trb));
					}else{
						Date current = DateTimeHelper.getDateDaysAfter(login, i);
						String date  = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern5);
						Date start 	= DateTimeHelper.getCertainDateStart(current);
						Date end 	= DateTimeHelper.getCertainDateEnd(current);
						add2Result(result,date,new WifiHandsetDeviceItemDetailMDTO(start.getTime(),end.getTime(),0l));
					}
				}
			}
		}
		return SortMapHelper.sortMapByKey(result);
	}

	*//**
	 * 每次往list里面增加记录的时候都index=0
	 * @param result
	 * @param date
	 * @param dto
	 *//*
	private static void add2Result(Map<String,List<WifiHandsetDeviceItemDetailMDTO>> result,String date,WifiHandsetDeviceItemDetailMDTO dto){
		List<WifiHandsetDeviceItemDetailMDTO> list = result.get(date);
		if(list == null){
			list = new ArrayList<>();
			result.put(date, list);
		}
		list.add(0,dto);
	}*/
}