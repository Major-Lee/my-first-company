package com.bhu.vas.business.ds.statistics.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.MacIncome;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class MacIncomeDao extends AbstractCoreDao<Integer, MacIncome>{
	public double countIncome(String mac) {
		double income=0;
		try {
			income=super.getSqlSessionSlaverTemplate().selectOne(MacIncome.class.getName()+".countIncome",mac);
		} catch (Exception e) {
			return income;
		}
		return income;
	}

	public List<MacIncome> findByLimit(Map<String, Object> map) {
		return super.getSqlSessionSlaverTemplate().selectList(MacIncome.class.getName()+".findMacIncomeByLimit",map);
	}
}
