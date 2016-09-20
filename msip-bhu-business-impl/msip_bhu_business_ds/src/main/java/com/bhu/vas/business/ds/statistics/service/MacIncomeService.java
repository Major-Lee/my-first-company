package com.bhu.vas.business.ds.statistics.service;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.MacIncome;
import com.bhu.vas.business.ds.statistics.dao.MacIncomeDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
@Transactional("coreTransactionManager")
public class MacIncomeService extends AbstractCommdityService<Integer, MacIncome, MacIncomeDao>{
	@Resource
	@Override
	public void setEntityDao(MacIncomeDao macIncomeDao) {
		super.setEntityDao(macIncomeDao);
	}
	public List<MacIncome> findListByMac(String mac,String time){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()
		.andColumnEqualTo("mac", mac)
		.andColumnEqualTo("time", time);
		mc.setOrderByClause("income");
		return super.findModelByModelCriteria(mc);
	}
}
