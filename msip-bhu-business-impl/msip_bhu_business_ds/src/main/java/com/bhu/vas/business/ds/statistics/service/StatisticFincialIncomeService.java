package com.bhu.vas.business.ds.statistics.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public List<Object> findFincialIncomeByTime(String preDay,String today){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("start_day", preDay);
		map.put("end_day", today);
		return super.entityDao.findModelByFincialIncomeId(map);
	}
}
