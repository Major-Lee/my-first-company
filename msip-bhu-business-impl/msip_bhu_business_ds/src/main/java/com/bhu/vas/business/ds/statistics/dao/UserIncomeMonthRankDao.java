package com.bhu.vas.business.ds.statistics.dao;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class UserIncomeMonthRankDao extends AbstractCoreDao<String, UserIncomeMonthRank>{
	public void deleteAllRank() {
		super.getSqlSessionMasterTemplate().delete(UserIncomeMonthRank.class.getName()+".deleteAllRank");
	}
	public void updateByTime(String time) {
		super.getSqlSessionMasterTemplate().delete(UserIncomeMonthRank.class.getName()+".updateByTime",time);
	}
	public List<UserIncomeMonthRank> findByLimit(String time) {
		return super.getSqlSessionMasterTemplate().selectList(UserIncomeMonthRank.class.getName()+".findByLimit",time);
	}
}
