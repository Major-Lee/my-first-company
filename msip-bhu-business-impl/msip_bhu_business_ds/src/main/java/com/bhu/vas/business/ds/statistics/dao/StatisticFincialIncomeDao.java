package com.bhu.vas.business.ds.statistics.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.StatisticFincialIncome;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class StatisticFincialIncomeDao extends AbstractCoreDao<Integer, StatisticFincialIncome>{

	public List<Object> findModelByFincialIncomeId(Map<String, Object> map) {
		
		return super.getSqlSessionSlaverTemplate().selectList(StatisticFincialIncome.class.getName()+".StatisticFincialIncome",map);
	}
}
