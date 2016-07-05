package com.bhu.vas.business.user;
import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.bhu.vas.business.portrait.ds.hportrait.service.HandsetPortraitService;
import com.smartwork.msip.localunit.BaseTest;

/**
 * 主键索引
 * 其他字段不索引的前提下
 * @author Edmond
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HandsetPortraitServiceTest extends BaseTest{

	@Resource
	private HandsetPortraitService handsetPortraitService;
	@BeforeClass
    public static void setUp() throws Exception {
		System.out.println("111111111");
		Thread.sleep(1000);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	System.out.println("0000000");
    	Thread.sleep(1000);
    }

}
