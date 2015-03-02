package com.wecite.toplines.business.user;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.user.model.UserLoginState;
import com.bhu.vas.business.user.service.UserLoginStateService;
import com.smartwork.msip.localunit.BaseTest;

public class UserLoginStateServiceTest extends BaseTest{
	@Resource
	private UserLoginStateService userLoginStateService;
	
	//@Test
	public void test100174() throws InterruptedException{
		String date = "2013-04-16";
		/*boolean ret = userLoginStateService.markUserLoginState(100111, date);
		System.out.println("ret:"+ret);*/
		date = "2013-05-16";
		UserLoginState model = userLoginStateService.getOrCreateById("100111");
		int count = model.getUserLoginMarkCount(date);
		System.out.println("login count:"+count);
		/*boolean ret = userLoginStateService.markUserLoginState(100112);
		System.out.println("ret:"+ret);*/
		
		UserLoginState state = userLoginStateService.getById("100174");
		String[] ss = state.getLoginMarksForAfter30day("2013-04-28");
		for(String s : ss){
			System.out.print(s);
			System.out.print(",");
		}
	}
	
	@Test
	public void test(){
		UserLoginState model = userLoginStateService.getOrCreateById("200007");
		String[] strs = model.getLoginMarksForAfterdays("2014-09-27", 30);
		for(String str : strs){
			System.out.print(str);
		}
	}
	
}
