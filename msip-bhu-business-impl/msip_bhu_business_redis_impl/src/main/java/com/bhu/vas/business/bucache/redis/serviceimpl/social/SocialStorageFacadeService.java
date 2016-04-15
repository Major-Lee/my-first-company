package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import com.bhu.vas.api.dto.social.HandsetMeetDTO;
import com.smartwork.msip.cores.helper.JsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bluesand on 3/11/16.
 */
public class SocialStorageFacadeService  {


    private final static int MAX_HANDSET_MEET_COUNT = 10;


    public static void handsetMeet(String uid, String hd_mac, String bssid, String dto) {

        List<HandsetMeetDTO> meets = getHandsetMeets(uid, hd_mac, bssid);

        if (meets != null) {
            if (meets.size() < MAX_HANDSET_MEET_COUNT && meets.size() >= 0) {
                meets.add(0, JsonHelper.getDTO(dto, HandsetMeetDTO.class));
            } else {
                meets.add(0, JsonHelper.getDTO(dto, HandsetMeetDTO.class));
                meets.remove(MAX_HANDSET_MEET_COUNT);
            }
        } else {
            meets = new ArrayList<HandsetMeetDTO>();
            meets.add(0, JsonHelper.getDTO(dto, HandsetMeetDTO.class));
        }

        SocialHandsetMeetHashService.getInstance().hsetHadsetMeets(uid, hd_mac, bssid, JsonHelper.getJSONString(meets));

//        SocialHandsetMeetHashService.getInstance().hincrbyHadsetMeetTotalWithBssid(uid, hd_mac, bssid);
        SocialHandsetMeetHashService.getInstance().hincrbyHadsetMeetTotal(uid, hd_mac);

//        SocialHandsetMeetHashService.getInstance().hsetLastMeetWithBssid(uid, hd_mac, bssid, dto);
        SocialHandsetMeetHashService.getInstance().hsetLastMeet(uid, hd_mac, dto);

    }


    public static List<HandsetMeetDTO> getHandsetMeets(String uid, String hd_mac, String bssid) {
        String meets = SocialHandsetMeetHashService.getInstance().hgetHandsetMeets(uid, hd_mac, bssid);
        return JsonHelper.getDTOList(meets,HandsetMeetDTO.class);
    }

    public static HandsetMeetDTO getLastHandsetMeetWithBssid(String hd_mac_self, String hd_mac, String bssid) {
        String dto = SocialHandsetMeetHashService.getInstance().hgetLastHandsetMeetWithBssid(hd_mac_self, hd_mac, bssid);
        return JsonHelper.getDTO(dto, HandsetMeetDTO.class);
    }


    public static HandsetMeetDTO getLastHandsetMeet(String uid, String hd_mac) {
        String dto = SocialHandsetMeetHashService.getInstance().hgetLastHandsetMeet(uid, hd_mac);
        return JsonHelper.getDTO(dto, HandsetMeetDTO.class);
    }

}
