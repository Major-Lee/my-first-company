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
	
	/*public List<StatisticFincialMonth> findModelByMonthId(String startMonth,String endMonth ,String orderFiled){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnBetween("monthid", startMonth, endMonth);
		//mc.setOrderByClause(orderFiled);
    	return super.findModelByModelCriteria(mc);
	}*/
	
//	public List<Object> findModelByMonthId(String preDay,String today){
//		Map<String,Object> map=new HashMap<String,Object>();
//		map.put("pre_day", preDay);
//		map.put("today", today);
//		return super.entityDao.findUserIncomeListByTime(map);
//	}
}
