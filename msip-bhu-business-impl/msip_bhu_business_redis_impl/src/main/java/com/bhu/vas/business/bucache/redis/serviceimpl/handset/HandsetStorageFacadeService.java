package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.List;
import java.util.Map;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
 * 考虑以后设备所属的终端量非常多的情况，类似拆表数据存储实现机制，并且不用通过数据库遍历可以把所有数据提取出来
 * 目前拆成20000个redis hash存储数据 可以支持2千w的数据散列
 * 拆分对象为mac地址，存储数据为Handset信息数据HandsetDeviceDTO
 * @author edmond
 *
 */
public class HandsetStorageFacadeService{
	
	public static void handsetComming(HandsetDeviceDTO dto){
		HandsetStorageService.getInstance().handsetComming(dto);
		HandsetStatisticsService.getInstance().online(dto.wasOnline());
		//String mac = dto.getMac();
		//this.hset(generateKey(mac), mac, JsonHelper.getJSONString(dto));
	}
	
	public static void handsetsComming(List<HandsetDeviceDTO> dtos){
		HandsetStorageService.getInstance().handsetsComming(dtos);
		HandsetStatisticsService.getInstance().online(true, dtos.size());
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
	
	public static int[] statistics(){
		return HandsetStatisticsService.getInstance().statistics();
	}
	
	public static void main(String[] argc){
		//HandsetStorageService.getInstance().clearOrResetAll();
		HandsetStorageFacadeService.iteratorAll(new IteratorNotify<Map<String,String>>(){
			@Override
			public void notifyComming(Map<String, String> t) {
				System.out.println(t);
			}
		});
	}
}