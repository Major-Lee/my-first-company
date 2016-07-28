package com.bhu.vas.business.ds.statistics.dao;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.api.rpc.commdity.model.Order;
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
	public double countTotalIncome() {
		double income = 0;
		try {
			income = super.getSqlSessionMasterTemplate().selectOne(UserIncome.class.getName()+".countTotalIncome");
		} catch (Exception e) {
			return income;
		}
		return income;
	}
	public double countTotalIncomeByDay(String time) {
		Double income = 0.0;
		try {
			income = super.getSqlSessionMasterTemplate().selectOne(UserIncome.class.getName()+".countTotalIncomeByDay",time);
		    if (null == income || StringUtils.isBlank(String.valueOf(income))) {
		    	return 0.0;
		    }
		} catch (Exception e) {
			return income;
		}	
		return income;
	}
	public double countTotalUserNumByDay(String time) {
		Double userNum = 0.0;
		try {
			userNum = super.getSqlSessionMasterTemplate().selectOne(UserIncome.class.getName()+".countTotalUserNumByDay",time);
			if (null == userNum || StringUtils.isBlank(String.valueOf(userNum))) {
		    	return 0.0;
		    }
		} catch (Exception e) {
			return userNum;
		}
		return userNum;
	}
	public Order selectOrdersInfo(String orderid) {
		Order order = null;
		try {
			order=super.getSqlSessionMasterTemplate().selectOne(Order.class.getName()+".selectOrdersInfo", orderid);
		} catch (Exception e) {
			return order;
		}
		return order;
	}
}
