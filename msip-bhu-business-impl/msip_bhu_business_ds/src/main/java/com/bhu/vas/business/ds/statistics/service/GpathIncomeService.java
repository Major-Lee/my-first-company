package com.bhu.vas.business.ds.statistics.service;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.GpathIncome;
import com.bhu.vas.business.ds.statistics.dao.GpathIncomeDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
@Transactional("coreTransactionManager")
public class GpathIncomeService extends AbstractCommdityService<Integer, GpathIncome, GpathIncomeDao>{
	@Resource
	@Override
	public void setEntityDao(GpathIncomeDao gpathIncomeDao) {
		super.setEntityDao(gpathIncomeDao);
	}
	public List<GpathIncome> findListByGpath(String gpath,String time){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()
		.andColumnEqualTo("gpath", gpath)
		.andColumnEqualTo("time", time);
		mc.setOrderByClause("income");
		return super.findModelByModelCriteria(mc);
	}
}
