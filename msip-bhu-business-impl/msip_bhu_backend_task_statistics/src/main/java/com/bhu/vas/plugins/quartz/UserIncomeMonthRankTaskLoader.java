package com.bhu.vas.plugins.quartz;



import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;


/**
 * 每天3，4，5，6点运行
 * @author dell
 *
 */
public class UserIncomeMonthRankTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(UserIncomeMonthRankTaskLoader.class);
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	public void execute() {
		logger.info("UserIncomeMonthRankTaskLoader start....");
		
		userWalletFacadeService.monthRankingList();
				
		logger.info("UserIncomeMonthRankTaskLoader end....");
	}
	
}
