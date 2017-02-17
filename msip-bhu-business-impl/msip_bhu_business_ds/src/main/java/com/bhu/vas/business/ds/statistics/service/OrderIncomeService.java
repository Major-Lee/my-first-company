package com.bhu.vas.business.ds.statistics.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.OrderIncome;
import com.bhu.vas.business.ds.statistics.dao.OrderIncomeDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class OrderIncomeService extends AbstractCoreService<String, OrderIncome, OrderIncomeDao>{
	@Resource
	@Override
	public void setEntityDao(OrderIncomeDao orderIncomeDao) {
		super.setEntityDao(orderIncomeDao);
	}
	public String orderCountIncome() {
		return super.entityDao.orderCountIncome();
	}
}
