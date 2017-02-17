package com.bhu.vas.business.ds.statistics.dao;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.OrderIncome;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class OrderIncomeDao extends AbstractCoreDao<String, OrderIncome>{
	
	public String orderCountIncome() {
		String income="0";
		try {
			income=super.getSqlSessionSlaverTemplate().selectOne(OrderIncome.class.getName()+".orderCountIncome");
		} catch (Exception e) {
			return income;
		}
		return income;
	}
}
