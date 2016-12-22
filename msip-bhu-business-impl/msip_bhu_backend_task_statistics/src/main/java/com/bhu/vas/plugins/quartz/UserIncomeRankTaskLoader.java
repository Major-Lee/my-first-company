package com.bhu.vas.plugins.quartz;



import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;



/**
 * 每天两点零九分运行
 * @author dell
 *
 */
public class UserIncomeRankTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(UserIncomeRankTaskLoader.class);

	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	public void execute() {
		logger.info("UserIncomeRankTaskLoader start...");
		userWalletFacadeService.rankingList();
		logger.info("UserIncomeRankTaskLoader end...");
	}
	
	
}
