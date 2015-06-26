package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
 * 考虑以后设备所属的终端量非常多的情况，类似拆表数据存储实现机制，并且不用通过数据库遍历可以把所有数据提取出来
 * 目前拆成2000个redis hash存储数据
 * 拆分对象为mac地址，存储数据为ctx
 * @author edmond
 *
 */
public class HandsetPresentService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static HandsetPresentService instance =new HandsetPresentService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static HandsetPresentService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private HandsetPresentService(){
	}
	//暫時假定2千萬设备，保證每個hashkey中存儲的不超過1000條數據，遵循redis 對於hash結構在不超過1000條數據的情況下壓縮及性能最優
	public static final int hasPrimeValue = 20000;
	
	private static String generateKey(String mac){
		int hashvalue = HashAlgorithmsHelper.additiveHash(mac, hasPrimeValue);
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDevicePresentCtxPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(String.format("%05d", hashvalue));
		return sb.toString();
	}
	
	private static String generateKeyByHashValue(int hashvalue){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDevicePresentCtxPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(String.format("%05d", hashvalue));
		return sb.toString();
	}
	
	public void addPresent(String wifiId, String ctx){
		this.hset(generateKey(wifiId), wifiId, ctx);
	}
	
	public void addPresents(List<String> wifiIds, String ctx){
		String[][] keyAndFields = generateKeyAndFieldsAndValues(wifiIds,ctx);
		this.pipelineHSet_diffKeyWithDiffFieldValue(keyAndFields[0], keyAndFields[1], keyAndFields[2]);
	}
	
	public String getPresent(String wifiId){
		return this.hget(generateKey(wifiId),wifiId);
	}
	
	public List<Object> getPresents(List<String> wifiIds){
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
	}
	public void removePresent(String wifiId){
		this.hdel(generateKey(wifiId),wifiId);
	}
	
	
	public void clearOrResetAll(){
		for(int i=0;i<hasPrimeValue;i++){
			this.expire(generateKeyByHashValue(i),0);
		}
	}
	
	public void iteratorAll(IteratorNotify<Map<String,String>> notify){
		for(int i=0;i<hasPrimeValue;i++){
			String key = generateKeyByHashValue(i);
			Map<String, String> hashKeyAll = this.hgetall(key);
			if(!hashKeyAll.isEmpty())
				notify.notifyComming(hashKeyAll);
		}
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return HandsetPresentService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.HANDSETPRESENT);
	}
	
	public static void main(String[] argv){
		List<String> argg = new ArrayList<String>();
		argg.add("aa");
		argg.add("ab");
		argg.add("ac");
		argg.add("ad");
		argg.add("ae");
		
		//WifiDevicePresentCtxService.getInstance().addPresents(argg, "ctx002");
		System.out.println(HandsetPresentService.getInstance().getPresents(argg));
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
