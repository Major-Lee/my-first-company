package com.bhu.vas.business.ds.statistics.dao;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class UserIncomeDao extends AbstractCoreDao<String, UserIncome>{
	public double countIncome(int uid) {
		double income=0;
		try {
			income=super.getSqlSessionMasterTemplate().selectOne(UserIncome.class.getName()+".countIncome",uid);
		} catch (Exception e) {
			return income;
		}
		return income;
	}
}
