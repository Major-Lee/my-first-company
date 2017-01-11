package com.bhu.vas.business.ds.statistics.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class UserIncomeDao extends AbstractCoreDao<String, UserIncome>{
	public double countIncome(int uid) {
		double income=0;
		try {
			income=super.getSqlSessionSlaverTemplate().selectOne(UserIncome.class.getName()+".countIncome",uid);
		} catch (Exception e) {
			return income;
		}
		return income;
	}
	public double countTotalIncome() {
		double income = 0;
		try {
			income = super.getSqlSessionSlaverTemplate().selectOne(UserIncome.class.getName()+".countTotalIncome");
		} catch (Exception e) {
			return income;
		}
		return income;
	}
	public double countTotalIncomeByDay(Integer uid,String time) {
		Double income = 0.0;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("uid", uid);
		map.put("time", time);
		try {
			income = super.getSqlSessionSlaverTemplate().selectOne(UserIncome.class.getName()+".countTotalIncomeByDay",map);
		    if (null == income || StringUtils.isBlank(String.valueOf(income))) {
		    	return 0.0;
		    }
		} catch (Exception e) {
			return income;
		}	
		return income;
	}
	public double countTotalUserNumByDay(Integer uid,String time) {
		Double userNum = 0.0;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("uid", uid);
		map.put("time", time);
		try {
			userNum = super.getSqlSessionSlaverTemplate().selectOne(UserIncome.class.getName()+".countTotalUserNumByDay",map);
			if (null == userNum || StringUtils.isBlank(String.valueOf(userNum))) {
		    	return 0.0;
		    }
		} catch (Exception e) {
			return userNum;
		}
		return userNum;
	}
	public List<UserIncome> findMonthList(String time) {
		List<UserIncome> userNum = super.getSqlSessionSlaverTemplate().selectList(UserIncome.class.getName()+".findMonthList",time);
		return userNum;
	}
	public Order selectOrdersInfo(String orderid) {
		Order order = null;
		try {
			order=super.getSqlSessionSlaverTemplate().selectOne(Order.class.getName()+".selectOrdersInfo", orderid);
		} catch (Exception e) {
			return order;
		}
		return order;
	}
	public List<UserIncome> findByLimit(Map<String, Object> map) {
		return super.getSqlSessionSlaverTemplate().selectList(UserIncome.class.getName()+".findUserIncomeByLimit",map);
	}
}
