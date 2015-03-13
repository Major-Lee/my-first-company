package com.whisper.test;

import javax.annotation.Resource;

import org.junit.Test;

import com.smartwork.msip.localunit.BaseTest;
import com.whisper.api.user.model.User;
import com.whisper.business.ucenter.service.UserService;

public class RemoteMemTest extends BaseTest{
	@Resource
	UserService userService;
	
	@Test
	public void testCached(){
		User user  = userService.getById(544344);
		
		System.out.println(user.getMobileno());
		
		userService.getById(544344);
		userService.getById(544344);
	}
}
