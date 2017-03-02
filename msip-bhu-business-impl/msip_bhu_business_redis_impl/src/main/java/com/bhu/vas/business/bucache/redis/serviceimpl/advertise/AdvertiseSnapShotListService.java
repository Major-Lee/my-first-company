//package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;
//
//import java.util.List;
//
//import redis.clients.jedis.JedisPool;
//
//import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
//import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
//import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
//import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;
//
//public class AdvertiseSnapShotListService extends AbstractRelationListCache{
//
//    private static class ServiceHolder{
//        private static AdvertiseSnapShotListService instance =new AdvertiseSnapShotListService();
//    }
//    
//    /**
//     * 获取工厂单例
//     * @return
//     */
//    public static AdvertiseSnapShotListService getInstance() {
//        return ServiceHolder.instance;
//    }
//	
//    private static String generateKey(String adid){
//        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseSnapShot);
//        sb.append(adid);
//        return sb.toString();
//    }
//    
//	public void generateSnapShot(String adid,List<String> macs){
//		for(String mac : macs){
//			this.lpush(generateKey(adid), mac);
//		}
//	}
//    
//	public List<String> fetchAdvertiseSnapShot(String adid){
//		return this.lrange(generateKey(adid), 0, -1);
//	}
//	
//	public void destorySnapShot(String adid){
//		this.del(generateKey(adid));
//	}
//	
//	@Override
//	public String getName() {
//		return AdvertiseSnapShotListService.class.getName();
//	}
//
//	@Override
//	public String getRedisKey() {
//		return null;
//	}
//
//	@Override
//	public JedisPool getRedisPool() {
//		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);	
//	}
//}
