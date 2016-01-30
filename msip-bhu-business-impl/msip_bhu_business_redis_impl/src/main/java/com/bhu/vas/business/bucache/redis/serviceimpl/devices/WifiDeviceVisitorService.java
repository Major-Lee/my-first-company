package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * Created by bluesand on 10/26/15.
 */
public class WifiDeviceVisitorService extends AbstractRelationSortedSetCache {


    private final long MIN_RANGE_VALUE  = 1000000000000L;

    private final long ONLINE_SCORE_VALUE = 0;

    private final long OFFLINE_SCORE_VALUE = 1;


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
     * 访客上线未认证
     * @param wifiId
     * @param handsetId
     * @return
     */
    public long addVisitorOnlinePresent(String wifiId, String handsetId){
        return super.zadd(generateKey(wifiId), ONLINE_SCORE_VALUE, handsetId);
    }

    /**
     * 访客网络终端认证离线
     * @param wifiId
     * @param handsetId
     * @return
     */
    public long addVisitorOfflinePresent(String wifiId, String handsetId) {
        return super.zadd(generateKey(wifiId), OFFLINE_SCORE_VALUE, handsetId);
    }

    /**
     * 访客网络终端认证上线
     * @param wifiId
     * @param socre
     * @param handsetId
     * @return
     */
    public long addAuthOnlinePresent(String wifiId, long socre, String handsetId) {
        return super.zadd(generateKey(wifiId), socre, handsetId);
    }

    /**
     * 访客网络终端认证取消下线
     * @param wifiId
     * @param handsetId
     * @return
     */
    public long removePresent(String wifiId, String handsetId) {
        return super.zrem(generateKey(wifiId), handsetId);
    }


    /**
     * 如果存在列表中,表示当前终端在线或者在已经认证,否则表示终端已经完全下线,不在认证期间.
     * @param wifiId
     * @param handsetId
     * @return
     */
    public boolean isOnlinePresent(String wifiId, String handsetId) {
        return super.zscore(generateKey(wifiId), handsetId) != null;
    }



    public long clearPresent(String wifiId) {
        return super.del(generateKey(wifiId));
    }


    public List<Object> removePresents(String wifiId, String[] handsetIds) {
        return super.pipelineZRem_sameKeyWithDiffMember(generateKey(wifiId), handsetIds);
    }

    public long countAuthPresent(String wifiId) {
        return super.zcount(generateKey(wifiId), MIN_RANGE_VALUE, Long.MAX_VALUE);
    }

    public Set<Tuple> fetchAuthOnlinePresent(String wifiId, int start, int size) {
        return super.zrangeByScoreWithScores(generateKey(wifiId),MIN_RANGE_VALUE, Long.MAX_VALUE, start, (start+size-1));
    }

    public Set<Tuple> fetchOnlinePresent(String wifiId, int start, int size) {
        return super.zrangeByScoreWithScores(generateKey(wifiId), ONLINE_SCORE_VALUE, ONLINE_SCORE_VALUE, start, (start+size-1));
    }

    public Set<Tuple> fetchOfflinePresent(String wifiId, int start, int size) {
        return super.zrangeByScoreWithScores(generateKey(wifiId), OFFLINE_SCORE_VALUE, OFFLINE_SCORE_VALUE, start, (start+size-1));
    }

    public Set<Tuple> fetchAllPresent(String wifiId, int start, int size) {
        return super.zrangeByScoreWithScores(generateKey(wifiId), ONLINE_SCORE_VALUE, Long.MAX_VALUE, start, (start+size-1));
    }


    public List<Object> pipelineAllPresentScores(String wifiId,String[] handsetIds) {
        return super.pipelineZScore_sameKeyWithDiffMember(generateKey(wifiId), handsetIds);
    }




    public static void main(String args[]) {


        System.out.println(WifiDeviceVisitorService.getInstance().fetchOnlinePresent("84:82:f4:28:8f:ac",0, 10));
        System.out.println(WifiDeviceVisitorService.getInstance().zrangeByScore(generateKey("84:82:f4:28:8f:ac"), 1, 1));
        System.out.println(WifiDeviceVisitorService.getInstance().zrangeByScoreWithScores(generateKey("84:82:f4:28:8f:ac"), 1, 1, 0, 5));

//        System.out.println(WifiDeviceVisitorService.getInstance().addVisitorOnlinePresent("84:82:f4:19:01:0c", "b4:0b:44:0d:96:31"));
//        System.out.println(WifiDeviceVisitorService.getInstance().fetchOnlinePresent("84:82:f4:19:01:0c",0, 10));
//        System.out.println(WifiDeviceVisitorService.getInstance().addAuthOnlinePresent("84:82:f4:19:01:0c", System.currentTimeMillis(), "bc:f5:ac:ac:a4:ce"));
//        System.out.println(WifiDeviceVisitorService.getInstance().fetchAuthOnlinePresent("84:82:f4:19:01:0c",0, 10));
//        System.out.println(WifiDeviceVisitorService.getInstance().addVisitorOfflinePresent("84:82:f4:19:01:0c", "b4:0b:44:0d:96:31"));
//        System.out.println(WifiDeviceVisitorService.getInstance().fetchOfflinePresent("84:82:f4:19:01:0c",0, 10));
//        System.out.println(WifiDeviceVisitorService.getInstance().removePresent("84:82:f4:19:01:0c", "b4:0b:44:0d:96:31"));


//        System.out.println(System.currentTimeMillis());
//
//        System.out.println(WifiDeviceVisitorService.getInstance().addAuthOnlinePresent("84:82:f4:19:01:0c", System.currentTimeMillis(), "6c:e8:73:c2:4b:3c"));
//
//        System.out.println(WifiDeviceVisitorService.getInstance().addAuthOnlinePresent("84:82:f4:19:01:0c", System.currentTimeMillis(), "bc:f5:ac:ac:a4:ce"));
//
//        System.out.println(WifiDeviceVisitorService.getInstance().addVisitorOnlinePresent("84:82:f4:19:01:0c", "b4:0b:44:0d:96:31"));
//
//        System.out.println(WifiDeviceVisitorService.getInstance().removePresent("84:82:f4:19:01:0c", "b4:0b:44:0d:96:31"));
//
//        System.out.println(WifiDeviceVisitorService.getInstance().countAuthPresent("84:82:f4:19:01:0c"));
//
//
//        Set<Tuple> presents = WifiDeviceVisitorService.getInstance().fetchAuthOnlinePresent("84:82:f4:23:06:68", 0, 5);
//
//        System.out.println(!presents.isEmpty());
//        System.out.println(presents.size());
//        for(Tuple tuple : presents){
//            System.out.println(tuple.getScore());
//            System.out.println(tuple.getElement());
//        }
//
//
//        System.out.println();
//
//        String payload = "<event>\n" +
//                "        <wlan>\n" +
//                "                <ITEM action=\"online\" mac=\"b4:0b:44:0d:96:31\" channel=\"1\" ssid=\"BhuWifi-啊\" bssid=\"96:82:f4:23:06:69\" location=\"\" vapname=\"wlan3\" portal=\"local\" authorized=\"true\" phy_rate=\"72M\" rssi=\"-56dBm\" snr=\"51dB\" ethernet=\"false\" />\n" +
//                "        </wlan>\n" +
//                "</event>";
//
//
//
//        List<HandsetDeviceDTO> dtos = RPCMessageParseHelper.generateDTOListFromMessage(payload,
//                HandsetDeviceDTO.class);
//
//        System.out.println(dtos.get(0).getPortal());
//        System.out.println(dtos.get(0).getVapname());
//        System.out.println(dtos.get(0).getAuthorized());
//
//
//
//        String text2 = "<return>\n" +
//                "        <ITEM result=\"ok\" config_sequence=\"247\" />\n" +
//                "</return>";
//        for(int i=0;i<100;i++){
//            WifiDeviceSettingDTO dto = RPCMessageParseHelper.generateDTOFromQueryDeviceSetting(
//                    text2);
//            System.out.println(dto.getSequence());
//        }

    }

}
