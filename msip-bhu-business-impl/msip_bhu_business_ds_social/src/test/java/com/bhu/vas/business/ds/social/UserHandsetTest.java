package com.bhu.vas.business.ds.social;

import com.bhu.vas.api.rpc.social.model.UserHandset;
import com.bhu.vas.api.rpc.social.model.pk.UserHandsetPK;
import com.bhu.vas.business.ds.social.service.UserHandsetService;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by bluesand on 3/3/16.
 */
public class UserHandsetTest extends BaseTest{

    @Resource
    private UserHandsetService userHandsetService;


    @Test
    public void insert() {
        UserHandsetPK userHandsetPK = new UserHandsetPK();
        userHandsetPK.setUid(1018);
        userHandsetPK.setHd_mac("123123123123123");

        UserHandset userHandset = new UserHandset();
        userHandset.setId(userHandsetPK);
        userHandset.setCreated_at(new Date());
        userHandset.setUpdated_at(new Date());
        userHandset.setNick("baidu");

        userHandsetService.insert(userHandset);

//        userHandsetService.findAll();


    }
}
