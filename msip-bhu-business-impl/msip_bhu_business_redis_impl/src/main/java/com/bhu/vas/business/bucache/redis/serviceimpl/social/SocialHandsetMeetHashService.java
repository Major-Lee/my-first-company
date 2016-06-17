package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;

import redis.clients.jedis.JedisPool;



/**
 * Created by bluesand on 3/3/16.
 */
public class SocialHandsetMeetHashService extends AbstractRelationHashCache {


    private final static String HANDSET_MEET_TOTAL_COUNT_KEY = "total";
    private final static String HANDSET_MEET_LAST_KEY = "last";
    private final static String HANDSET_MEET = "meet";

    private static class ServiceHolder{
        private static SocialHandsetMeetHashService instance =new SocialHandsetMeetHashService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static SocialHandsetMeetHashService getInstance() {
        return ServiceHolder.instance;
    }

    private SocialHandsetMeetHashService() {

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
        return SocialHandsetMeetHashService.class.getName();
    }


    private static String generateKey(String uidOrhd_mac, String hd_mac){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Social.MEET);
        String key = null;
        
        if (uidOrhd_mac.hashCode() >= hd_mac.hashCode()) {
        	key= uidOrhd_mac  + "_" + hd_mac;
		}else{
			key = hd_mac  + "_" + uidOrhd_mac;
		}
        
        sb.append(StringHelper.POINT_CHAR_GAP).append(key);
        return sb.toString();
    }

    public void hsetHadsetMeets(String uidOrhd_mac, String hd_mac, String bssid, String meets) {
        this.hset(generateKey(uidOrhd_mac, hd_mac), HANDSET_MEET, meets);
    }

//    public void hincrbyHadsetMeetTotalWithBssid(String hd_mac_self, String hd_mac, String bssid) {
//        //bssid场景下的统计
//        this.hincrby(generateKey(hd_mac_self, hd_mac), bssid + "_" + HANDSET_MEET_TOTAL_COUNT_KEY, 1);
//    }

    public void hincrbyHadsetMeetTotal(String uidOrhd_mac, String hd_mac) {
        this.hincrby(generateKey(uidOrhd_mac, hd_mac), HANDSET_MEET_TOTAL_COUNT_KEY, 1);
    }

    public void hsetLastMeetWithBssid(String uidOrhd_mac, String hd_mac, String bssid, String dto) {
        this.hset(generateKey(uidOrhd_mac, hd_mac), bssid + "_" + HANDSET_MEET_LAST_KEY, dto);
    }

    public void hsetLastMeet(String uidOrhd_mac, String hd_mac, String dto) {
        this.hset(generateKey(uidOrhd_mac, hd_mac), HANDSET_MEET_LAST_KEY, dto);
    }


    public String hgetLastHandsetMeetWithBssid(String uidOrhd_mac, String hd_mac, String bssid) {
        return this.hget(generateKey(uidOrhd_mac, hd_mac), bssid);
    }

    public String hgetLastHandsetMeet(String uidOrhd_mac, String hd_mac) {
        return this.hget(generateKey(uidOrhd_mac, hd_mac), HANDSET_MEET_LAST_KEY);
    }

    public String hgetHandsetMeets(String uidOrhd_mac, String hd_mac, String bssid) {
        return this.hget(generateKey(uidOrhd_mac, hd_mac), HANDSET_MEET);
    }

    public static void main(String[] args) {
        System.out.println(generateKey("100027", "84:82:f4:19:00:c0"));
        System.out.println(generateKey("b8:88:e3:f3:37:8f", "f0:25:b7:63:86:5b"));
        
        
        

//        HandsetMeetDTO dto = new HandsetMeetDTO();
//        dto.setBssid("google");
//        dto.setLat("10.0");
//        dto.setLat("11.1");
//        dto.setSsid("gooogle-wifi");
//        dto.setTs(System.currentTimeMillis());
//
//        SocialHandsetMeetHashService.getInstance().hsetHadsetMeets("100027", "82:83:90:12:32:35", "google", JsonHelper.getJSONString(dto));
//
//        String dd = SocialHandsetMeetHashService.getInstance().hget(generateKey("100027", "82:83:90:12:32:34"), HANDSET_MEET_LAST_KEY);
//
//        System.out.println("===" + dd);
//
//        System.out.println(JsonHelper.getDTO(dd, HandsetMeetDTO.class));
//
////        dto = SocialHandsetMeetHashService.getInstance().getLasthandsetMeet("84:82:f4:28:7a:ec", "82:83:90:12:32:34");
//
//        System.out.println();
    }
}
