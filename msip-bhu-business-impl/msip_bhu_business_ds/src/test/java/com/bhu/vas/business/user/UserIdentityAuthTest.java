package com.bhu.vas.business.user;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.user.model.UserIdentityAuth;
import com.bhu.vas.business.ds.user.service.UserIdentityAuthService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.localunit.BaseTest;

public class UserIdentityAuthTest extends BaseTest{
	
	@Resource
	private UserIdentityAuthService  userIdentityAuthService;
	
//	@Test
//	public void test001(){
//		UserIdentityAuth entity = new UserIdentityAuth();
//		entity.setId("86 15127166171");
//		entity.setHdmac("68:3e:34:48:b7:35");
//		entity.setCreate_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5));
//		userIdentityAuthService.insert(entity);
//	}
}
