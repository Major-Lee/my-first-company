package com.bhu.vas.business.ds.statistics.dao;

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
		double income=0;
		try {
			income=super.getSqlSessionMasterTemplate().selectOne(UserIncome.class.getName()+".countTotalIncome");
		} catch (Exception e) {
			return income;
		}
		return income;
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
