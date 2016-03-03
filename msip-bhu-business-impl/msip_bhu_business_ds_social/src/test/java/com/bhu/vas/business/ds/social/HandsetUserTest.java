package com.bhu.vas.business.ds.social;

import com.bhu.vas.api.rpc.social.model.HandsetUser;
import com.bhu.vas.api.rpc.social.model.UserHandset;
import com.bhu.vas.api.rpc.social.model.pk.UserHandsetPK;
import com.bhu.vas.business.ds.social.service.HandsetUserService;
import com.bhu.vas.business.ds.social.service.UserHandsetService;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by bluesand on 3/3/16.
 */
public class HandsetUserTest extends BaseTest {

    @Resource
    private HandsetUserService handsetUserService;


    @Test
    public void insert() {

        HandsetUser handsetUser = new HandsetUser();
        handsetUser.setId("!@#!@#123");
        handsetUser.setUid(123);
        handsetUser.setNick("123123");
        handsetUser.setCreated_at(new Date());
        handsetUserService.insert(handsetUser);


    }
}
