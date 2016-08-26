package com.bhu.vas.business.ds.statistics.dao;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.UserIncomeRank;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class UserIncomeRankDao extends AbstractCoreDao<String, UserIncomeRank>{
	public void deleteAllRank() {
		super.getSqlSessionMasterTemplate().delete(UserIncomeRank.class.getName()+".deleteAllRank");
	}
	public void updateByTime(String time) {
		super.getSqlSessionMasterTemplate().delete(UserIncomeRank.class.getName()+".updateByTime",time);
	}
	public List<UserIncomeRank> findByLimit(String time) {
		return super.getSqlSessionMasterTemplate().selectList(UserIncomeRank.class.getName()+".findByLimit",time);
	}
	
	
}
