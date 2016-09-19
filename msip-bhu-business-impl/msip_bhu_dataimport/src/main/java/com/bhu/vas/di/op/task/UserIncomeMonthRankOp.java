package com.bhu.vas.di.op.task;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;

public class UserIncomeMonthRankOp {
	public static void main(String[] args) {
		System.out.println(0);
		ClassPathXmlApplicationContext context = null;
		try{
			String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
			context = new ClassPathXmlApplicationContext(CONFIG, UserIncomeMonthRankOp.class);
			System.out.println(1);
			context.start();
			UserWalletFacadeService userWalletFacadeService = context.getBean("userWalletFacadeService",UserWalletFacadeService.class);
			userWalletFacadeService.monthRankingList();
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{
 
		}
		System.exit(1);
	}
}
