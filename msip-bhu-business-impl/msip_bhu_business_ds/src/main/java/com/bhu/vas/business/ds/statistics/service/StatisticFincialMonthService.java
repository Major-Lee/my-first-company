package com.bhu.vas.business.ds.statistics.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.StatisticFincialMonth;
import com.bhu.vas.business.ds.statistics.dao.StatisticFincialMonthDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class StatisticFincialMonthService extends AbstractCoreService<Integer, StatisticFincialMonth, StatisticFincialMonthDao>{
	@Resource
	@Override
	public void setEntityDao(StatisticFincialMonthDao statisticFincialMonthDao) {
		super.setEntityDao(statisticFincialMonthDao);
	}
}
