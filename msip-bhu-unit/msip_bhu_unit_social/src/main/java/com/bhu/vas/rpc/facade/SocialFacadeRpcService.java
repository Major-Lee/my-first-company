package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.rpc.social.model.WifiComment;
import com.bhu.vas.business.ds.social.service.WifiCommentService;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.Date;

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
    
    public void clickPraise(long uid,String bssid, String type) {
	
    }
    
}
