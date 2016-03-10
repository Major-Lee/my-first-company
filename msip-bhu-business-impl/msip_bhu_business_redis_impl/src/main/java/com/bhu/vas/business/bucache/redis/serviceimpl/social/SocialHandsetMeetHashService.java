package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import com.bhu.vas.api.dto.social.HandsetMeetDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * Created by bluesand on 3/3/16.
 */
public class SocialHandsetMeetHashService extends AbstractRelationHashCache {


    private final static String HANDSET_MEET_TOTAL_COUNT_KEY = "total";
    private final static String HANDSET_MEET_LAST_KEY = "last";

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
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.DEFAULT);
    }

    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return SocialHandsetMeetHashService.class.getName();
    }


    private static String generateKey(String hd_mac_self, String hd_mac){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Social.MEET);
        String key = null;
        if (hd_mac_self.hashCode() >= hd_mac.hashCode()) {
            key = hd_mac_self  + "_" + hd_mac;
        } else {
            key = hd_mac + "_" + hd_mac_self;
        }
        sb.append(StringHelper.POINT_CHAR_GAP).append(key);
        return sb.toString();
    }

    public void handsetMeet(String hd_mac_self, String hd_mac, String bssid, String dto) {
        this.hset(generateKey(hd_mac_self, hd_mac), bssid, dto);
        this.hincrby(generateKey(hd_mac_self, hd_mac), HANDSET_MEET_TOTAL_COUNT_KEY, 1);
        this.hset(generateKey(hd_mac_self, hd_mac), HANDSET_MEET_LAST_KEY, dto);
    }

    public List<HandsetMeetDTO> getHandsetMeetList(String hd_mac_self, String hd_mac, String bssid) {
        String meetList = this.hget(generateKey(hd_mac_self, hd_mac), bssid);
        return JsonHelper.getDTOList(meetList,HandsetMeetDTO.class);
    }

    public HandsetMeetDTO getLasthandsetMeet(String hd_mac_self, String hd_mac) {
        return JsonHelper.getDTO(this.hget(generateKey(hd_mac_self, hd_mac), HANDSET_MEET_LAST_KEY), HandsetMeetDTO.class);
    }

    public static void main(String[] args) {
        System.out.println(generateKey("84:82:f4:19:01:0c", "84:82:f4:19:00:c0"));
        System.out.println(generateKey("84:82:f4:19:00:c0", "84:82:f4:19:01:0c"));

        HandsetMeetDTO dto = new HandsetMeetDTO();
        dto.setBssid("google");
        dto.setLat("10.0");
        dto.setLat("11.1");
        dto.setSsid("gooogle-wifi");
        dto.setTs(System.currentTimeMillis());

        SocialHandsetMeetHashService.getInstance().handsetMeet("82:83:90:12:32:34", "82:83:90:12:32:35", "google", JsonHelper.getJSONString(dto));

        dto = SocialHandsetMeetHashService.getInstance().getLasthandsetMeet("84:82:f4:28:7a:ec", "82:83:90:12:32:34");

        System.out.println();
    }
}
