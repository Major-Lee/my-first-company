package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;

import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import redis.clients.jedis.JedisPool;

import java.util.Set;

public class SocialFollowSortedSetService
        extends AbstractRelationSortedSetCache {

    private final static Long FOLLOW_SUM_MAX = 200L;

    private static class ServiceHolder {
        private static SocialFollowSortedSetService instance = new SocialFollowSortedSetService();
    }

    /**
     * 获得工厂单例
     *
     * @return
     */
    public static SocialFollowSortedSetService getInstance() {
        return ServiceHolder.instance;
    }

    public SocialFollowSortedSetService() {
    }

    @Override
    public String getName() {
        return SocialFollowSortedSetService.class.getName();
    }

    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.DEFAULT);
    }

    private String generateKey(long uid) {
        StringBuilder sb = new StringBuilder();
        sb.append(BusinessKeyDefine.Social.RELATION).append(uid);
        return sb.toString();
    }

    public boolean isFollowMax(long uid) {
        if (this.zcard(generateKey(uid)) > FOLLOW_SUM_MAX) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isFollowed(long uid, String hd_mac) {
        Long zrank = this.zrank(generateKey(uid), hd_mac);
        return zrank == null;
    }

    public Set<String> fetchFollowList(long uid, int pageNo, int pageSize) {
        Set<String> set = this.zrange(generateKey(uid), (pageNo - 1) * pageSize, pageSize * pageNo - 1);
        return set;
    }

    public void follow(long uid, String hd_mac) {
            this.zadd(generateKey(uid), System.currentTimeMillis(), hd_mac);
    }

    public void unFollow(long uid, String hd_mac) {
        this.zrem(generateKey(uid), hd_mac);
    }
}