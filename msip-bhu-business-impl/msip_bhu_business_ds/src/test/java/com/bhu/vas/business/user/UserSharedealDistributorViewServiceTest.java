package com.bhu.vas.business.user;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.user.model.UserSharedealDistributorView;
import com.bhu.vas.business.ds.user.service.UserSharedealDistributorViewService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;

/**
 * Created by bluesand on 4/29/15.
 */
public class UserSharedealDistributorViewServiceTest extends BaseTest{
	
	@Resource
	private UserSharedealDistributorViewService userSharedealDistributorViewService;
	
	@Test
	public void test001BatchCreateUserOAuth(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", 1);
		List<UserSharedealDistributorView> results = userSharedealDistributorViewService.findModelByModelCriteria(mc);
		for(UserSharedealDistributorView view : results){
			System.out.println(view);
		}
	}	
}
