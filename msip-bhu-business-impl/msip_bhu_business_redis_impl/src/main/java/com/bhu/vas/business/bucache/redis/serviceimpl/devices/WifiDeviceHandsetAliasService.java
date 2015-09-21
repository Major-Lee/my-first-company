package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;
import redis.clients.jedis.JedisPool;

/**
 * 获取用户设备终端别名
 */
public class WifiDeviceHandsetAliasService extends AbstractRelationHashCache {

    private static class ServiceHolder{
        private static WifiDeviceHandsetAliasService instance =new WifiDeviceHandsetAliasService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static WifiDeviceHandsetAliasService getInstance() {
        return ServiceHolder.instance;
    }

    private WifiDeviceHandsetAliasService() {

    }

    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
    }

    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return WifiDeviceHandsetAliasService.class.getName();
    }

    private static String generateKey(String wifiId){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.UserWifiDeviceHandset.Nick);
        sb.append(StringHelper.POINT_CHAR_GAP).append(wifiId);
        return sb.toString();
    }

    public Long hsetHandsetAlias(int uid, String handsetMac, String alias) {
        return this.hset(generateKey(String.valueOf(uid)), handsetMac, alias);
    }

    public Long hdelHandsetAlias(int uid, String handsetMac, String alias) {
        return this.hdel(generateKey(String.valueOf(uid)), handsetMac, alias);
    }

    public String hgetHandsetAlias(int uid, String handsetMac) {
        return this.hget(generateKey(String.valueOf(uid)), handsetMac);
    }
}
