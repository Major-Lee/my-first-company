package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
 * 考虑以后设备所属的终端量非常多的情况，类似拆表数据存储实现机制，并且不用通过数据库遍历可以把所有数据提取出来
 * 目前拆成20000个redis hash存储数据 可以支持2千w的数据散列
 * 拆分对象为mac地址，存储数据为Handset信息数据HandsetDeviceDTO
 * @author edmond
 *
 */
public class HandsetStorageService extends AbstractRelationHashCache{
	private static class ServiceHolder{ 
		private static HandsetStorageService instance =new HandsetStorageService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static HandsetStorageService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private HandsetStorageService(){
	}
	//暫時假定2千萬设备，保證每個hashkey中存儲的不超過1000條數據，遵循redis 對於hash結構在不超過1000條數據的情況下壓縮及性能最優
	public static final int hasPrimeValue = 20000;
	
	private static String generateKey(String mac){
		int hashvalue = HashAlgorithmsHelper.additiveHash(mac, hasPrimeValue);
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.HandsetPresent.PresentPrefixKey);
		sb.append(String.format("%05d", hashvalue));
		return sb.toString();
	}
	
	private static String generateKeyByHashValue(int hashvalue){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.HandsetPresent.PresentPrefixKey);
		sb.append(String.format("%05d", hashvalue));
		return sb.toString();
	}
	
	public Long handsetComming(HandsetDeviceDTO dto){
		String mac = dto.getMac();
		return this.hset(generateKey(mac), mac, JsonHelper.getJSONString(dto));
	}
	
	public List<Object> handsetsComming(List<HandsetDeviceDTO> dtos){
		String[][] keyAndFields = generateKeyAndFieldsAndValues(dtos);
		return this.pipelineHSet_diffKeyWithDiffFieldValue(keyAndFields[0], keyAndFields[1], keyAndFields[2]);
	}
	
	public HandsetDeviceDTO handset(String mac){
		String value = this.hget(generateKey(mac),mac);
		if(StringUtils.isEmpty(value)){
			return null;//
		}
		return JsonHelper.getDTO(value,HandsetDeviceDTO.class);
	}
	
	public List<HandsetDeviceDTO> handsets(List<String> macs){
		String[][] keyAndFields = generateKeyAndFields(macs);
		List<Object> values = null;
		List<HandsetDeviceDTO> result = new ArrayList<HandsetDeviceDTO>();
		try{
			values = this.pipelineHGet_diffKeyWithDiffFieldValue(keyAndFields[0],keyAndFields[1]);
			for(Object obj :values){
				if(obj != null)
					result.add(JsonHelper.getDTO(String.class.cast(obj), HandsetDeviceDTO.class));
				else{
					result.add(null);
				}
			}
		}finally{
			if(values != null){
				values.clear();
				values = null;
			}
		}
		return result;
	}
	
/*	public void handsetLeave(String mac){
		this.hdel(generateKey(wifiId),wifiId);
	}*/
	
	public void clearOrResetAll(){
		/*for(int i=0;i<hasPrimeValue;i++){
			this.expire(generateKeyByHashValue(i),0);
		}*/
		int pipe_size = 1000;
		String[] keys_array = new String[pipe_size];
		for(int i=0;i<hasPrimeValue;i++){
			int index = i%pipe_size;
			keys_array[index] = generateKeyByHashValue(i);
			if(index == 999){
				this.expire_pipeline(ArrayHelper.toList(keys_array), 0);//.pipelineHLen_diffKey(keys_array);
			}
			/*String key = generateKeyByHashValue(i);
			//this.pipelineHLen_diffKey(keys).pipelineHLen_diffKey
			long sub_total = this.hlen(key);//.hgetall(key);
			total += sub_total;*/
		}
	}
	
	/**
	 * 读取速度相对慢，可以在生产环境运行
	 * @return
	 */
	public long countAll(){
		int pipe_size = 1000;
		String[] keys_array = new String[pipe_size];
		long total = 0;
		for(int i=0;i<hasPrimeValue;i++){
			int index = i%pipe_size;
			keys_array[index] = generateKeyByHashValue(i);
			if(index == 999){
				List<Object> result = this.pipelineHLen_diffKey(keys_array);
				for(Object obj:result){
					total += Long.class.cast(obj);
				}
			}
			/*String key = generateKeyByHashValue(i);
			//this.pipelineHLen_diffKey(keys).pipelineHLen_diffKey
			long sub_total = this.hlen(key);//.hgetall(key);
			total += sub_total;*/
		}
		return total;
	}
	
	
	public void iteratorAll(IteratorNotify<Map<String,String>> notify){
		for(int i=0;i<hasPrimeValue;i++){
			String key = generateKeyByHashValue(i);
			Map<String, String> hashKeyAll = this.hgetall(key);
			//System.out.println(key);
			if(!hashKeyAll.isEmpty())
				notify.notifyComming(hashKeyAll);
		}
	}
	
	private String[][] generateKeyAndFields(List<String> macs){
		if(macs == null || macs.isEmpty()) return null;
		int size = macs.size();
		String[][] result = new String[2][size];
		String[] keys = new String[size];
		String[] fields = new String[size];
		int cursor = 0;
		for(String mac : macs){
			keys[cursor] = generateKey(mac);
			fields[cursor] = mac;
			cursor++;
		}
		result[0] = keys;
		result[1] = fields;
		return result;
	}
	
	
	private String[][] generateKeyAndFieldsAndValues(List<HandsetDeviceDTO> dtos){
		if(dtos.isEmpty()) return null;
		int size = dtos.size();
		String[][] result = new String[3][size];
		String[] keys = new String[size];
		String[] fields = new String[size];
		String[] values = new String[size];
		int cursor = 0;
		for(HandsetDeviceDTO dto : dtos){
			keys[cursor] = generateKey(dto.getMac());
			fields[cursor] = dto.getMac();
			values[cursor] = JsonHelper.getJSONString(dto);
			cursor++;
		}
		result[0] = keys;
		result[1] = fields;
		result[2] = values;
		return result;
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return HandsetStorageService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.HANDSETPRESENT);
	}
	
	public static void main(String[] argv){


		System.out.println(HandsetStorageService.generateKey("f0:25:b7:07:d3:0e"));
		System.out.println(HandsetStorageService.generateKey("18:00:2d:91:57:8b"));
		System.out.println(HandsetStorageService.generateKey("f0:25:b7:93:d9:e9"));
		System.out.println(HandsetStorageService.generateKey("60:f8:1d:a2:b2:a7"));



		System.out.println(HandsetStorageService.getInstance().handset("18:00:2d:91:57:8b").getDhcp_name());


//		List<String> macs = new ArrayList<String>();
//		macs.add("aa");
//		macs.add("ab");
//		macs.add("ac");
//		macs.add("ad");
//		macs.add("ae");
//		List<HandsetDeviceDTO> handsets = HandsetStorageService.getInstance().handsets(macs);
//		System.out.println(handsets.size());
//
//		List<HandsetDeviceDTO> now = new ArrayList<HandsetDeviceDTO>();
//
//		for(String mac:macs){
//			HandsetDeviceDTO dto = new HandsetDeviceDTO();
//			dto.setMac(mac);
//			now.add(dto);
//		}
//
//		HandsetStorageService.getInstance().handsetsComming(now);
//
//		handsets = HandsetStorageService.getInstance().handsets(macs);
//		System.out.println(handsets.size());
//
//		HandsetDeviceDTO dto1 = new HandsetDeviceDTO();
//		dto1.setMac("gogog");
//
//		HandsetStorageService.getInstance().handsetComming(dto1);
//
//		HandsetDeviceDTO handset = HandsetStorageService.getInstance().handset("gogog");
//		System.out.println(handset.getMac());
//		//HandsetStorageService.getInstance().clearOrResetAll();
//
//		System.out.println(HandsetStorageService.getInstance().countAll());
//		//WifiDevicePresentCtxService.getInstance().addPresents(argg, "ctx002");
//		//String[][] result = WifiDevicePresentCtxService.getInstance().generateKeyAndFields(argg);
//		//System.out.println(result.length);
//
//		/*WifiDevicePresentCtxService.getInstance().iteratorAll(new IteratorNotify<Map<String,String>>(){
//			@Override
//			public void notifyComming(Map<String, String> t) {
//				System.out.println(t);
//			}
//		});*/
//		//WifiDevicePresentCtxService.getInstance().clearOrResetAll();
	}
}
