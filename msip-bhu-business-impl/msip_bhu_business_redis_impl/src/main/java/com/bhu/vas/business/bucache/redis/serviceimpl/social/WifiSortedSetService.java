package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * Created by bluesand on 3/7/16.
 */
public class WifiSortedSetService extends AbstractRelationSortedSetCache {
    private static class ServiceHolder{
        private static WifiSortedSetService instance =new WifiSortedSetService();
    }

    public static WifiSortedSetService getInstance() {
        return ServiceHolder.instance;
    }

    private WifiSortedSetService() {
    }

    private String generateKey(String bssid){
        StringBuilder sb = new StringBuilder();
        sb.append(BusinessKeyDefine.Social.VISITOR).append(bssid);
        return sb.toString();
    }

    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.SOCIAL);
    }

    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return WifiSortedSetService.class.getName();
    }

    public long addWifiVistor(String bssid, long uid) {
        return this.zadd(generateKey(bssid), System.currentTimeMillis(), String.valueOf(uid));
    }

    public Set<String> getWifiVistors(String bssid) {
        return this.zrevrange(generateKey(bssid), 0, -1);
    }



    public static void main(String[] args) {
        WifiSortedSetService.getInstance().addWifiVistor("1234", 1024);
        WifiSortedSetService.getInstance().addWifiVistor("1234", 1023);
        WifiSortedSetService.getInstance().addWifiVistor("1234", 1026);
        WifiSortedSetService.getInstance().addWifiVistor("1234", 1025);

        System.out.println(WifiSortedSetService.getInstance().getWifiVistors("1234"));
    }

}
