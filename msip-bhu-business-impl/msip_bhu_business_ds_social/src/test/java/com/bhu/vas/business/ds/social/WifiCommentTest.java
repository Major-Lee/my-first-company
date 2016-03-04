package com.bhu.vas.business.ds.social;

import com.bhu.vas.api.rpc.social.model.WifiComment;
import com.bhu.vas.business.ds.social.service.WifiCommentService;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by bluesand on 3/2/16.
 */
public class WifiCommentTest extends BaseTest {

    @Resource
    private WifiCommentService wifiCommentService;


    @Test
    public void insert() {
        WifiComment wifiComment = new WifiComment();

        wifiComment.setBssid("12:23:34:56:78:90");
        wifiComment.setCreated_at(new Date());
        wifiComment.setMessage("woooooo");
        wifiComment.setUid(1234567);
        wifiComment.setUpdated_at(new Date());
       wifiComment.setHd_mac("sssss");

        wifiCommentService.insert(wifiComment);


    }
}
