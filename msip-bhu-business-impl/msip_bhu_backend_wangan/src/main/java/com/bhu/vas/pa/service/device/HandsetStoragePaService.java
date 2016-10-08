package com.bhu.vas.pa.service.device;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.pa.dto.PaHandsetOnlineAction;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 把公安数据存储到redis
 * @author Yetao
 *
 */
public class HandsetStoragePaService extends AbstractRelationStringCache {
	private static class ServiceHolder{ 
		private static HandsetStoragePaService instance = new HandsetStoragePaService(); 
	}
	
	//暫時假定2千萬设备，保證每個hashkey中存儲的不超過1000條數據，遵循redis 對於hash結構在不超過1000條數據的情況下壓縮及性能最優
	public static final int hasPrimeValue = 20000;
	
	
	private static final int expire_time = 3600 * 24;
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static HandsetStoragePaService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private HandsetStoragePaService(){
	}
	
	private static String generateKey(String mac, String hmac){//String dmac,String hmac){
		StringBuilder sb = new StringBuilder("PA.");
		sb.append(mac);
		sb.append(hmac);
		return sb.toString();
	}
	
	public  void saveHandset(String mac, String hmac, String objstr){
		this.set(generateKey(mac, hmac), objstr);
	}
    
	public void handsetsComming(List<PaHandsetOnlineAction> dtos){
		String[][] keyAndFields = generateKeyAndFieldsAndValues(dtos);
		if(keyAndFields != null)
			this.pipelineHSet_diffKeyWithDiffFieldValue(keyAndFields[0], keyAndFields[1]);
	}
	
	public List<PaHandsetOnlineAction> gets(String mac, List<String> hmacs){
		List<String> keys = new ArrayList<String>();
		for (String hmac : hmacs) {
			if (StringUtils.isEmpty(hmac)) {
				continue;
			}
			keys.add(generateKey(mac, hmac));
		}
		
		if (CollectionUtils.isEmpty(keys)) {
			return null;
		}
		
		List<PaHandsetOnlineAction> result = new ArrayList<>();
		for (String key : keys) {
			String value = get(key);
			if (StringUtils.isEmpty(value)) {
				continue;
			}
			result.add(JsonHelper.getDTO(value, PaHandsetOnlineAction.class));
		}
		return result;
	}
	
	private void pipelineHSet_diffKeyWithDiffFieldValue(String[] keys, String[] values) {
		for (int i = 0; i < keys.length; i++) {
			this.set(keys[i], values[i]);
		}
	}
	
	private String[][] generateKeyAndFieldsAndValues(List<PaHandsetOnlineAction> dtos){
		if(dtos.isEmpty()) return null;
		int size = dtos.size();
		String[][] result = new String[2][size];
		String[] keys = new String[size];
		String[] values = new String[size];
		int cursor = 0;
		for(PaHandsetOnlineAction dto : dtos){
			keys[cursor] = generateKey(dto.getMac(), dto.getHmac());
			values[cursor] = JsonHelper.getJSONString(dto);
			cursor++;
		}
		result[0] = keys;
		result[1] = values;
		return result;
	}
	
	
	public String getHandset(String mac, String hmac){
		String str = this.get(generateKey(mac, hmac));
		this.expire(generateKey(mac, hmac), expire_time);
		return str;
	}
	
	public void removeHandset(String mac, String hmac){
		this.del(generateKey(mac, hmac));
	}
    	
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return HandsetStoragePaService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PAHANDSET);
	}
	
	public static void main(String[] argv){
		/*System.out.println(HandsetStorageService.generateKey("6c:72:e7:70:fd:76"));
		System.out.println(HandsetStorageService.generateKey("f0:25:b7:07:d3:0e"));
		System.out.println(HandsetStorageService.generateKey("18:00:2d:91:57:8b"));
		System.out.println(HandsetStorageService.generateKey("f0:25:b7:93:d9:e9"));
		System.out.println(HandsetStorageService.generateKey("60:f8:1d:a2:b2:a7"));
		System.out.println(HandsetStorageService.getInstance().handset("18:00:2d:91:57:8b").getDhcp_name());*/
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
		
		
		List<String> macs = new ArrayList<String>();
		macs.add("b1");
		macs.add("b2");
		macs.add("b3");
		macs.add("b4");
		macs.add("b5");
        
		List<String> hmacs = new ArrayList<String>();
		hmacs.add("111");
		hmacs.add("222");
		hmacs.add("333");
		hmacs.add("444");
		hmacs.add("555");
		
		List<PaHandsetOnlineAction> now = new ArrayList<PaHandsetOnlineAction>();
        
		int cur = 0;
		for(String mac:macs){
			for (int j = 0; j<=2; j++) {
				PaHandsetOnlineAction dto = new PaHandsetOnlineAction();
				dto.setMac(mac);
				dto.setHmac(hmacs.get(j));
				now.add(dto);
			}
			cur++;
		}
		HandsetStoragePaService.getInstance().handsetsComming(now);
		String key = generateKey("aa", "111");
		System.out.println(key + "------");
		String str = HandsetStoragePaService.getInstance().get(key);
		System.out.println(str);
		
		for (String mac : macs) {
			System.out.println(JsonHelper.getJSONString(HandsetStoragePaService.getInstance().gets(mac, hmacs)));
		}
		
	}
}
