package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

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

    public Long hdelHandsetAlias(int uid, String handsetMac) {
        return this.hdel(generateKey(String.valueOf(uid)), handsetMac);
    }

    public String hgetHandsetAlias(int uid, String handsetMac) {
        return this.hget(generateKey(String.valueOf(uid)), handsetMac);
    }

    public boolean hexistsHandsetAlias(int uid, String handsetMac) {
        return this.hexists(generateKey(String.valueOf(uid)), handsetMac);
    }

    private String[][] generateKeyAndFields(int uid, List<String> macs){
        if(macs == null || macs.isEmpty()) return null;
        int size = macs.size();
        String[][] result = new String[2][size];
        String[] keys = new String[size];
        String[] fields = new String[size];
        int cursor = 0;
        for(String mac : macs){
            keys[cursor] = generateKey(String.valueOf(uid));
            fields[cursor] = mac;
            cursor++;
        }
        result[0] = keys;
        result[1] = fields;
        return result;
    }

    public List<String> pipelineHandsetAlias(int uid, List<String> macs) {
        String[][] keyAndFields = generateKeyAndFields(uid, macs);
        List<Object> values = null;
        List<String> result = new ArrayList<String>();
        try{
            values = this.pipelineHGet_diffKeyWithDiffFieldValue(keyAndFields[0],keyAndFields[1]);
            for(Object obj :values){
                if (obj == null) {
                    result.add("");
                } else {
                    result.add(obj.toString());
                }

            }
        }finally{
            if(values != null){
                values.clear();
                values = null;
            }
        }
        return result;
    }
}
