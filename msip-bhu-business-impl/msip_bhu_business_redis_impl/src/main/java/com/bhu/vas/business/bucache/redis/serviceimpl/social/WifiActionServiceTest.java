package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import org.junit.Test;

import com.smartwork.msip.localunit.BaseTest;

public class WifiActionServiceTest extends BaseTest{
    
    @Test
    public void test01() {
	WifiActionService.getInstance().actions("84:82:f4:28:7a:ec");
//	for(String key : map.keySet()) {
//	    System.out.println("key:"+key+" and value:"+map.get(key));
//	}
    }
}
