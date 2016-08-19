package com.bhu.vas.business.user;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.smartwork.msip.localunit.BaseTest;

public class UserWalletlogFacadeServiceTest extends BaseTest{
	@Resource
	UserWalletLogService userWalletLogService;
	@Test
	public void test() {
		String start_time = "2015-08-01 00:00:00";
		String end_time = "2016-08-11 23:59:59";
		Integer uid = 100153;
		String mac = "";
		String umac = "44:00:10:80:1f:6c";
		Map<String, Object> sum= userWalletLogService.getEntityDao().fetchCashSumAndCountByUid(uid, start_time, end_time, null,umac,10);
		Iterator<Map.Entry<String, Object>> it = sum.entrySet().iterator();
		while (it.hasNext()) {
			   Map.Entry<String, Object> entry = it.next();
			   System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
		}
	}
	
}
