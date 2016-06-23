package com.bhu.vas.business.ds.statistics.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.business.ds.statistics.dao.UserIncomeDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;

@Service
@Transactional("coreTransactionManager")
public class UserIncomeService extends AbstractCommdityService<String, UserIncome, UserIncomeDao>{
	@Resource
	@Override
	public void setEntityDao(UserIncomeDao userIncomeDao) {
		super.setEntityDao(userIncomeDao);
	}

}
