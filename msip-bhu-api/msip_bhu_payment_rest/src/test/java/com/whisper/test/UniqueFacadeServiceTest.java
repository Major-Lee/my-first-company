package com.whisper.test;

import org.apache.http.examples.client.ClientCustomSSL;
import org.junit.Test;

import com.smartwork.msip.localunit.BaseTest;

public class UniqueFacadeServiceTest extends BaseTest{
	@Test
	public void checkEmailExist(){
		
//		ResponseError error =  ValidateService.validateEmail("brook6@jing.fm");
//		if(error != null){
//			System.out.println(error.getMsg());
//			System.out.println(error.getCodemsg());
//		}else
//			System.out.println("不存在");
		ClientCustomSSL.gethttpRequests();
		
	}
}
