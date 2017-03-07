package com.bhu.vas.business.ds.statistics.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.StatisticFincialMonth;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class StatisticFincialMonthDao extends AbstractCoreDao<Integer, StatisticFincialMonth>{

	public List<Object> findModelByMonthId(Map<String, Object> map) {
		return super.getSqlSessionSlaverTemplate().selectList(StatisticFincialMonth.class.getName()+".StatisticFincialMonth",map);
		
	}
}
