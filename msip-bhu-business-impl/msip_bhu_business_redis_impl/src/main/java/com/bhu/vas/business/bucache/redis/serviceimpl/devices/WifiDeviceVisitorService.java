package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;

/**
 * Created by bluesand on 10/26/15.
 */
public class WifiDeviceVisitorService extends AbstractRelationSortedSetCache {


    private static class ServiceHolder{
        private static WifiDeviceVisitorService instance =new WifiDeviceVisitorService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static WifiDeviceVisitorService getInstance() {
        return ServiceHolder.instance;
    }

    private WifiDeviceVisitorService() {}

    private static String generateKey(String wifiId){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.WifiDeviceGuest.Guest);
        sb.append(StringHelper.POINT_CHAR_GAP).append(wifiId);
        return sb.toString();
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
        return WifiDeviceVisitorService.class.getName();
    }


    /**
     * 访客上线
     * @param wifiId
     * @param handsetId
     * @return
     */
    public long addVisitorOnlinePresent(String wifiId, String handsetId){
        return super.zadd(generateKey(wifiId), 0, handsetId);
    }

    public long addAuthOnlinePresent(String wifiId, long socre, String handsetId) {
        return super.zadd(generateKey(wifiId), socre, handsetId);
    }

    public long removePresent(String wifiId, String handsetId) {
        return super.zrem(generateKey(wifiId), handsetId);
    }

    public long countAuthPresent(String wifiId) {
        return super.zcount(generateKey(wifiId), 1, Long.MAX_VALUE);
    }

    public Set<Tuple> fetchAuthOnlinePresent(String wifiId, int start, int size) {
        return super.zrangeByScoreWithScores(generateKey(wifiId),1, Long.MAX_VALUE, start, (start+size-1));
    }



    public static void main(String args[]) {
        System.out.println(WifiDeviceVisitorService.getInstance().addAuthOnlinePresent("84:82:f4:19:01:0c", System.currentTimeMillis(), "6c:e8:73:c2:4b:3c"));

        System.out.println(WifiDeviceVisitorService.getInstance().addAuthOnlinePresent("84:82:f4:19:01:0c", System.currentTimeMillis(), "bc:f5:ac:ac:a4:ce"));

        System.out.println(WifiDeviceVisitorService.getInstance().addVisitorOnlinePresent("84:82:f4:19:01:0c", "b4:0b:44:0d:96:31"));

        System.out.println(WifiDeviceVisitorService.getInstance().removePresent("84:82:f4:19:01:0c", "b4:0b:44:0d:96:31"));

        System.out.println(WifiDeviceVisitorService.getInstance().countAuthPresent("84:82:f4:19:01:0c"));


        Set<Tuple> presents = WifiDeviceVisitorService.getInstance().fetchAuthOnlinePresent("84:82:f4:23:06:68", 0, 5);

        System.out.println(!presents.isEmpty());
        System.out.println(presents.size());
        for(Tuple tuple : presents){
            System.out.println(tuple.getScore());
            System.out.println(tuple.getElement());
        }


//        System.out.println();
//
//        String payload = "<event>\n" +
//                "        <wlan>\n" +
//                "                <ITEM action=\"online\" mac=\"b4:0b:44:0d:96:31\" channel=\"1\" ssid=\"BhuWifi-哈哈\" bssid=\"96:82:f4:23:06:69\" location=\"\" vapname=\"wlan3\" portal=\"local\" authorized=\"true\" phy_rate=\"72M\" rssi=\"-56dBm\" snr=\"51dB\" ethernet=\"false\" />\n" +
//                "        </wlan>\n" +
//                "</event>";
//
//        List<HandsetDeviceDTO> dtos = RPCMessageParseHelper.generateDTOListFromMessage(payload,
//                HandsetDeviceDTO.class);
//
//        System.out.println(dtos.get(0).getPortal());
//        System.out.println(dtos.get(0).getVapname());

    }

}
