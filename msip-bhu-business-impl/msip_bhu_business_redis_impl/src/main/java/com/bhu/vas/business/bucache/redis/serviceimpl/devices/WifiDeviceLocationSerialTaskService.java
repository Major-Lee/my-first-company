package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.redis.SerialTaskDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 考虑以后设备量非常多的情况，类似拆表数据存储实现机制，并且不用通过数据库遍历可以把所有数据提取出来
 * 目前拆成2000个redis hash存储数据
 * 拆分对象为mac地址，存储数据为SerialTaskDTO
 * @author edmond
 *
 */
public class WifiDeviceLocationSerialTaskService extends AbstractRelationHashCache{
	
	private static class WifiDevicePresentCtxServiceHolder{ 
		private static WifiDeviceLocationSerialTaskService instance =new WifiDeviceLocationSerialTaskService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceLocationSerialTaskService getInstance() { 
		return WifiDevicePresentCtxServiceHolder.instance; 
	}
	
	private WifiDeviceLocationSerialTaskService(){
	}
	//暫時假定1百萬设备，但是周期内查询地理位置的数据不会很多，10个分片应该够了，而且这些数据丢失掉也无所谓
	public static final int hasPrimeValue = 10;
	
	private static String generateKey(String mac){
		int hashvalue = HashAlgorithmsHelper.additiveHash(mac, hasPrimeValue);
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDeviceSerialTaskPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(String.format("%04d", hashvalue));
		return sb.toString();
	}
	
	private static String generateKeyByHashValue(int hashvalue){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDeviceSerialTaskPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(String.format("%04d", hashvalue));
		return sb.toString();
	}
	
	public void addSerialTask(String wifiId, SerialTaskDTO serialTask){
		this.hset(generateKey(wifiId), wifiId, JsonHelper.getJSONString(serialTask));
	}
	
	public SerialTaskDTO getSerialTask(String wifiId){
		String result = this.hget(generateKey(wifiId),wifiId);
		if(StringUtils.isNotEmpty(result)){
			return JsonHelper.getDTO(result, SerialTaskDTO.class);
		}
		return null;
	}
	public void removeSerialTask(String wifiId){
		this.hdel(generateKey(wifiId),wifiId);
	}
	
	public void clearOrResetAll(){
		for(int i=0;i<hasPrimeValue;i++){
			this.expire(generateKeyByHashValue(i),0);
		}
	}
	
	public void iteratorAll(IteratorNotify<Map<String,SerialTaskDTO>> notify){
		for(int i=0;i<hasPrimeValue;i++){
			String key = generateKeyByHashValue(i);
			Map<String, String> hashKeyAll = this.hgetall(key);
			if(!hashKeyAll.isEmpty()){
				Map<String, SerialTaskDTO> result = new HashMap<String,SerialTaskDTO>();
				Set<Entry<String, String>> entrySet = hashKeyAll.entrySet();
				for(Entry<String, String> element:entrySet){
					if(StringUtils.isNotEmpty(element.getValue())){
						result.put(element.getKey(),JsonHelper.getDTO(element.getValue(), SerialTaskDTO.class));
					}
				}
				notify.notifyComming(result);
			}
		}
	}
	
	/*public List<Object> getPresents(List<String> wifiIds){
		String[][] keyAndFields = generateKeyAndFields(wifiIds);
		return this.pipelineHGet_diffKeyWithDiffFieldValue(keyAndFields[0],keyAndFields[1]);
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
	
	
	private String[][] generateKeyAndFieldsAndValues(List<String> macs,String ctx){
		if(macs == null || macs.isEmpty()) return null;
		int size = macs.size();
		String[][] result = new String[3][size];
		String[] keys = new String[size];
		String[] fields = new String[size];
		String[] values = new String[size];
		int cursor = 0;
		for(String mac : macs){
			keys[cursor] = generateKey(mac);
			fields[cursor] = mac;
			values[cursor] = ctx;
			cursor++;
		}
		result[0] = keys;
		result[1] = fields;
		result[2] = values;
		return result;
	}*/
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return WifiDeviceLocationSerialTaskService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
	
	public static void main(String[] argv){
		
		SerialTaskDTO serialTask = WifiDeviceLocationSerialTaskService.getInstance().getSerialTask("62:68:75:02:ff:05");
		System.out.println(serialTask);
		/*List<String> argg = new ArrayList<String>();
		argg.add("aa");
		argg.add("ab");
		argg.add("ac");
		argg.add("ad");
		argg.add("ae");*/
		
		//WifiDevicePresentCtxService.getInstance().addPresents(argg, "ctx002");
		//System.out.println(WifiDeviceLocationSerialTaskService.getInstance().getPresents(argg));
		//String[][] result = WifiDevicePresentCtxService.getInstance().generateKeyAndFields(argg);
		//System.out.println(result.length);
		
		/*WifiDevicePresentCtxService.getInstance().iteratorAll(new IteratorNotify<Map<String,String>>(){
			@Override
			public void notifyComming(Map<String, String> t) {
				System.out.println(t);
			}
		});*/
		//WifiDevicePresentCtxService.getInstance().clearOrResetAll();
	}
}
