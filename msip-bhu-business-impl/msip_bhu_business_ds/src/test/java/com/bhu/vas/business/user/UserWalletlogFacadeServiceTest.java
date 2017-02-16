package com.bhu.vas.business.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.business.ds.statistics.service.UserIncomeMonthRankService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeRankService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.smartwork.msip.localunit.BaseTest;

public class UserWalletlogFacadeServiceTest extends BaseTest{
	@Resource
	UserWalletLogService userWalletLogService;
	@Resource
	UserIncomeService userIncomeService;
	@Resource
	UserWalletFacadeService userWalletFacadeService;
	@Resource
	UserIncomeRankService userIncomeRankService;
	@Resource
	UserIncomeMonthRankService userIncomeMonthRankService;
	@Test
	public void test() {
		List<Object> userIncomeList = userWalletLogService.userAccountIncome("2017-01-01 00:00:00","2017-02-14 00:00:00");
		if(userIncomeList != null){
			for (Object object : userIncomeList) {
				UserIncome userIncome = (UserIncome) object;
    			System.out.println(userIncome.getTime());
    			System.out.println(userIncome.getIncome());
	    	}
		}
		
	}
	
}
