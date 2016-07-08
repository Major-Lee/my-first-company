package com.bhu.vas.di.op.task;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;

public class UserIncomeRankBuilderOp {
//	public static void main(String[] args) {
//		System.out.println(0);
//		ClassPathXmlApplicationContext context = null;
//		try{
//			String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
//			context = new ClassPathXmlApplicationContext(CONFIG, UserIncomeRankBuilderOp.class);
//			System.out.println(1);
//			context.start();
//			UserWalletFacadeService userWalletFacadeService = context.getBean("userWalletFacadeService",UserWalletFacadeService.class);
//			userWalletFacadeService.rankingList();
//		}catch(Exception ex){
//			ex.printStackTrace(System.out);
//		}finally{
// 
//		}
//		System.exit(1);
//	}
	public static void main(String[] args) {
		System.out.println(0);
		//ClassPathXmlApplicationContext context = null;
		try{
			ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
			System.out.println(1);
			//context.start();
			UserWalletFacadeService userWalletFacadeService = (UserWalletFacadeService) ctx.getBean("userWalletFacadeService");
			userWalletFacadeService.getUserIncomeService().findListByTime("2016-07-06");
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{
			
		}
		System.exit(1);
	}
}
