package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import com.bhu.vas.api.dto.social.SocialRemarkDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import redis.clients.jedis.JedisPool;

/**
 * Created by Administrator on 2016/3/17.
 */
public class SocialCareHashService extends AbstractRelationHashCache {

    private  static class ServiceHolder{
        private static SocialCareHashService instance = new SocialCareHashService();
    }

    /**
     * 获得工厂单例
     * @return
     */
    public static SocialCareHashService getInstance() {
        return ServiceHolder.instance;
    }

    private String generateKey(long uid){
        StringBuilder sb = new StringBuilder();
        sb.append(BusinessKeyDefine.Social.REMARKS).append(uid);
        return sb.toString();
    }


    public void care(long uid, String hd_mac, String dto){
        this.hset(generateKey(uid),hd_mac, dto);
    }

    public void unCare(long uid,String hd_mac){
        this.hdel(generateKey(uid),hd_mac);
    }

    public boolean isCared(long uid, String hd_mac){
       return this.hexists(Long.toString(uid),hd_mac);
    }


    public String getCareValue(long uid, String hd_mac) {
        return this.hget(String.valueOf(uid) ,hd_mac);
    }


    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return WifiActionHashService.class.getName();
    }

    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.DEFAULT);
    }
}
