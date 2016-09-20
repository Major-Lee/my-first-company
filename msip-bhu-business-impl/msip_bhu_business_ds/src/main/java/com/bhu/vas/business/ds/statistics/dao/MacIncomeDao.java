package com.bhu.vas.business.ds.statistics.dao;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.MacIncome;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class MacIncomeDao extends AbstractCoreDao<Integer, MacIncome>{
	public double countIncome(String mac) {
		double income=0;
		try {
			income=super.getSqlSessionMasterTemplate().selectOne(MacIncome.class.getName()+".countIncome",mac);
		} catch (Exception e) {
			return income;
		}
		return income;
	}
}
