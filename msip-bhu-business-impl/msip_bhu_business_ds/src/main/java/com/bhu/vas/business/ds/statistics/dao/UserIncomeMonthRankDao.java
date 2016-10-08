package com.bhu.vas.business.ds.statistics.dao;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.bhu.vas.api.rpc.charging.model.UserIncomeRank;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class UserIncomeMonthRankDao extends AbstractCoreDao<Integer, UserIncomeMonthRank>{
	public void deleteAllRank() {
		super.getSqlSessionMasterTemplate().delete(UserIncomeMonthRank.class.getName()+".deleteAllRank");
	}
	public void updateByTime(String time) {
		super.getSqlSessionMasterTemplate().delete(UserIncomeMonthRank.class.getName()+".updateByTime",time);
	}
	public List<UserIncomeMonthRank> findByLimit(Map<String,Object> map) {
		return super.getSqlSessionMasterTemplate().selectList(UserIncomeMonthRank.class.getName()+".findByLimit",map);
	}
	public List<UserIncomeMonthRank> getByUid(Map<String,Object> map) {
		return super.getSqlSessionMasterTemplate().selectList(UserIncomeRank.class.getName()+".getByUid",map);
	}
}
