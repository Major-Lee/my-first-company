package com.bhu.vas.business.ds.social;

import com.bhu.vas.api.rpc.social.model.Wifi;
import com.bhu.vas.business.ds.social.service.WifiService;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;
import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by bluesand on 3/7/16.
 */
public class WifiTest extends BaseTest {

    @Resource
    private WifiService wifiService;


    @Test
    public void insesrt(){

        Wifi wifi = new Wifi();
        wifi.setId("123");
        wifi.setLat(123.123);
        wifi.setLon(232.1);
        wifi.setManufacturer("baidu");
        wifi.setMax_rate("123123");
        wifi.setSsid("google");
        wifi.setCreated_at(new Date());

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!");

        wifiService.insert(wifi);
    }
}
