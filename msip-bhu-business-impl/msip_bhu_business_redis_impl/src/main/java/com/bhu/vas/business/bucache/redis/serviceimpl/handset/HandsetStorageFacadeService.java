package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.List;
import java.util.Map;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.HandsetLogDTO;
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
    	long rb = Long.parseLong(tx_bytes);
    	DeviceHandsetLogService.getInstance().hansetLogComming(false, dmac, hmac, rb, logout_at);
    	if(rb >0){
    		DeviceHandsetExtFieldService.getInstance().increaseTrb(dmac, hmac, rb);
    	}
    }

    
    public static int wifiDeviceHandsetOnline(String dmac, String hmac, long last_login_at){
    	return DeviceHandsetLogService.getInstance().hansetLogComming(true, dmac, hmac, 0l, last_login_at);
    }
	
    public static List<HandsetLogDTO> wifiDeviceHandsetRecentLogs(String dmac, String hmac,int size){
    	return DeviceHandsetLogService.getInstance().fetchRecentHandsetLogs(dmac, hmac, size);
    }
    
    public static long wifiDeviceHandsetTrbFetched(String dmac, String hmac){
    	return DeviceHandsetExtFieldService.getInstance().trb(dmac, hmac);//.fetchRecentHandsetLogs(dmac, hmac, size);
    }
    
	public static void main(String[] argc){
		//HandsetStorageService.getInstance().clearOrResetAll();
		/*HandsetStorageFacadeService.iteratorAll(new IteratorNotify<Map<String,String>>(){
			@Override
			public void notifyComming(Map<String, String> t) {
				System.out.println(t);
			}
		});*/
		long countAll = HandsetStorageFacadeService.countAll();
		System.out.println(countAll);
		HandsetDeviceDTO handset = HandsetStorageFacadeService.handset("88:32:9b:32:41:10");
		System.out.println(handset.getMac());
		System.out.println(handset.getDhcp_name());
		
		int[] statistics = HandsetStorageFacadeService.statistics();
		
		System.out.println(statistics[0]);
		System.out.println(statistics[1]);
	}
}