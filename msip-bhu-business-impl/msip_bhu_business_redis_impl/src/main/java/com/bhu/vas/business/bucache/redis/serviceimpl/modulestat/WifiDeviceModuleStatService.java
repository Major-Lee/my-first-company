package com.bhu.vas.business.bucache.redis.serviceimpl.modulestat;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import redis.clients.jedis.JedisPool;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bluesand on 12/1/15.
 */
public class WifiDeviceModuleStatService extends AbstractRelationHashCache {
    private static class ServiceHolder{
        private static WifiDeviceModuleStatService instance =new WifiDeviceModuleStatService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static WifiDeviceModuleStatService getInstance() {
        return ServiceHolder.instance;
    }

    private WifiDeviceModuleStatService(){}



    public Set<String> hgetModuleStat(String key) {
        return this.hkeys(generateStyleKey(key));
    }

    public List<Object> hgetModuleStats(String key) {

        Set<String> sets = this.hkeys(generateStyleKey(key));

        String[] keys = new String[sets.size()];
        String[] fields = new String[sets.size()];

        int cursor = 0;
        for (String field : sets) {
            keys[cursor] = generateStyleKey(key);
            fields[cursor] = field;
        }

        return this.pipelineHGet_diffKeyWithDiffFieldValue(keys, fields);
    }


    public Map<String, Long> hgetModuleStatsWithKey(String key) {

        Map<String, Long> map = new LinkedHashMap<>();

        Set<String> sets = this.hkeys(generateStyleKey(key));

        String[] keys = new String[sets.size()];
        String[] fields = new String[sets.size()];

        int cursor = 0;
        for (String field : sets) {
            keys[cursor] = generateStyleKey(key);
            fields[cursor] = field;
            cursor ++;
        }

        List<Object> rets =  this.pipelineHGet_diffKeyWithDiffFieldValue(keys, fields);

        cursor = 0;
        for (String field: fields) {
            map.put(field, Long.parseLong(rets.get(cursor).toString()));
            cursor ++;
        }

        return map;

    }






    public List<Object> addDayModuleStats(Map<String, Map<String,Long>> maps, int size) {
        String[][] keyAndFields = generateKeyAndFieldsAndValues(maps, size);

        long[] values = new long[keyAndFields[2].length];
        int cursour = 0;
        for (String value : keyAndFields[2]) {
            values[cursour] = Long.parseLong(value);
            cursour ++;
        }

        return this.pipelineHIncr_diffKeyWithDiffFieldValue(keyAndFields[0], keyAndFields[1], values);
    }


    private String[][] generateKeyAndFieldsAndValues(Map<String, Map<String,Long>> maps, int size){
        if(maps.isEmpty())
            return null;

        String[][] result = new String[3][size];
        String[] keys = new String[size];
        String[] fields = new String[size];
        String[] values = new String[size];

        int cursor = 0;
        for (String styleKey : maps.keySet()) {
            Map<String, Long> modules = maps.get(styleKey);
            for (String filedKey : modules.keySet()) {
                keys[cursor] = generateStyleKey(styleKey);
                fields[cursor] = generateModuleKey(filedKey);
                values[cursor] = modules.get(filedKey).toString();
                cursor++;
            }
        }

        result[0] = keys;
        result[1] = fields;
        result[2] = values;
        return result;
    }


    private String generateStyleKey(String key) {
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.ModuleStat.StylePrefix);
        sb.append(StringHelper.POINT_STRING_GAP).append(key);
        return sb.toString();
    }

    private String generateModuleKey(String key) {
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.ModuleStat.ModulePrefix);
        sb.append(StringHelper.POINT_STRING_GAP).append(key);
        return sb.toString();
    }



    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return WifiDeviceModuleStatService.class.getName();
    }
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
    }


    public static void main(String[] args) {
        System.out.println(WifiDeviceModuleStatService.getInstance().pipelineHSet_sameKeyWithDiffFieldValue("TTTT", new String[]{"TT1,TT2"}, new String[]{"1", "2"}));

        //WifiDeviceModuleStatService.getInstance().hset("TTTT", "T", "1234");
    }


}
