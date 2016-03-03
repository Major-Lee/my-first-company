package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.dto.social.SocialHandsetMeetDTO;
import com.bhu.vas.api.rpc.social.model.UserHandset;
import com.bhu.vas.api.rpc.social.model.WifiComment;

import com.bhu.vas.api.vto.WifiActionVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.WifiActionService;

import com.bhu.vas.api.rpc.social.model.pk.UserHandsetPK;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.SocialHandsetMeetHashService;
import com.bhu.vas.business.ds.social.service.UserHandsetService;

import com.bhu.vas.business.ds.social.service.WifiCommentService;
import com.smartwork.msip.cores.helper.JsonHelper;


import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bluesand on 3/2/16.
 */
@Service
public class SocialFacadeRpcService {

    @Resource
    private WifiCommentService wifiCommentService;

    @Resource
    private UserHandsetService userHandsetService;

    public WifiComment comment(long uid, String bssid, String message) {

        WifiComment wifiComment = new WifiComment();
        wifiComment.setUid(uid);
        wifiComment.setMessage(message);
        wifiComment.setBssid(bssid);
        wifiComment.setCreated_at(new Date());

        return wifiCommentService.insert(wifiComment);
    }

    public WifiActionVTO clickPraise(String bssid, String type) {

	if (WifiActionService.getInstance().isNoExist(bssid)) {
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("up", "0");
	    map.put("down", "0");
	    map.put("report", "0");
	    WifiActionService.getInstance().hadd(bssid, map);
	}
	WifiActionService.getInstance().hincrease(bssid, type);
	return WifiActionService.getInstance().counts(bssid);
    }


    public boolean handsetMeet(Long uid, String hd_mac, String hd_macs, String bssid, String ssid, String lat, String lon) {

        if (uid != null || uid >0) {
            UserHandset userHandset = new UserHandset();
            userHandset.setId(new UserHandsetPK(uid, hd_mac));
            userHandset.setCreated_at(new Date());
            userHandsetService.insert(userHandset);
        }

        SocialHandsetMeetDTO dto = new SocialHandsetMeetDTO();
        dto.setBssid(bssid);
        dto.setSsid(ssid);
        dto.setTs(System.currentTimeMillis());
        dto.setLat(lat);
        dto.setLon(lon);

        String[] list = hd_macs.split(",");
        if (list != null && list.length >0) {
            for (String mac : list) {
                //TODO:(bluesand): backend操作
                SocialHandsetMeetHashService.getInstance().handsetMeet(hd_mac, mac, bssid, JsonHelper.getJSONString(dto));
            }
        }
        return false;
    }

}
