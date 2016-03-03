package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.rpc.social.model.WifiComment;
import com.bhu.vas.api.vto.WifiActionVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.WifiActionService;
import com.bhu.vas.business.ds.social.service.WifiCommentService;
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
}
