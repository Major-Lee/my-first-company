package com.bhu.vas.rpc.service.device;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;

import redis.clients.jedis.JedisPool;

/**
 * 把公安数据存储到redis
 * @author Yetao
 *
 */
public class HandsetStoragePaService extends AbstractRelationStringCache {
	private static class ServiceHolder{ 
		private static HandsetStoragePaService instance =new HandsetStoragePaService(); 
	}
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
		StringBuilder sb = new StringBuilder("TPA.");
		sb.append(mac);
		sb.append(hmac);
		return sb.toString();
	}
	
	public  void saveAuthOnline(String mac, String hmac, String objstr){
		this.set(generateKey(mac, hmac), objstr);
	}

	public String getAuthOnline(String mac, String hmac){
		String str = this.get(generateKey(mac, hmac));
		this.expire(generateKey(mac, hmac), expire_time);
		return str;
	}
	
	public void removeAuthOnline(String mac, String hmac){
		this.del(generateKey(mac, hmac));
	}

/*	public void handsetLeave(String mac){
		this.hdel(generateKey(wifiId),wifiId);
	}*/
	
	
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
	}
}
