package com.bhu.vas.business.ds.statistics.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.StatisticFincialIncome;
import com.bhu.vas.business.ds.statistics.dao.StatisticFincialIncomeDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class StatisticFincialIncomeService extends AbstractCoreService<Integer, StatisticFincialIncome, StatisticFincialIncomeDao>{
	@Resource
	@Override
	public void setEntityDao(StatisticFincialIncomeDao statisticFincialIncomeDao) {
		super.setEntityDao(statisticFincialIncomeDao);
	}
}
