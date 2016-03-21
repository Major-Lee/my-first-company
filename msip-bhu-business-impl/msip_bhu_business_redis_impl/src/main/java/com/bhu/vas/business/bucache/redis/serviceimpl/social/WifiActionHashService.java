package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import java.util.HashMap;
import java.util.Map;

import com.bhu.vas.api.dto.social.WifiActionDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

import redis.clients.jedis.JedisPool;

public class WifiActionHashService extends AbstractRelationHashCache {

    private final static String COUNT_DEFAULT = "0";

    public enum followType {
        up, report;
    }

    private static class ServiceHolder {
        private static WifiActionHashService instance = new WifiActionHashService();
    }

    /**
     * 获得工厂单例
     *
     * @return
     */
    public static WifiActionHashService getInstance() {
        return ServiceHolder.instance;
    }

    private WifiActionHashService() {
    }

    private String generateKey(String bssid) {
        StringBuilder sb = new StringBuilder();
        sb.append(BusinessKeyDefine.Social.ACTION).append(bssid);
        return sb.toString();
    }

    public boolean isNoExist(String bssid) {
        if ((this.hgetall(generateKey(bssid)).isEmpty())) {
            return true;
        }
        return false;
    }

    public WifiActionDTO counts(String bssid) {
        Map<String, String> map = this.hgetall(generateKey(bssid));
        WifiActionDTO dto = new WifiActionDTO();
        if (map.isEmpty()) {
            init(bssid);
        }
        dto.setUp(map.get(followType.up.name()));
        dto.setReport(map.get(followType.report.name()));
        return dto;
    }

    public void init(String bssid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(followType.up.name(), COUNT_DEFAULT);
        map.put(followType.report.name(), COUNT_DEFAULT);
        this.hmset(generateKey(bssid), map);
    }

    public void hincrease(String bssid, String type) {
        this.hincrby(generateKey(bssid), type, 1);
    }

    public void hdecrease(String bssid, String type) {
        this.hincrby(generateKey(bssid), type, -1);
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