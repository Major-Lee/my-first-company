package com.bhu.vas.business.ds.statistics.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public List<MacIncome> findByLimit(String time,int start,int limit) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("time", time);
		map.put("limit", limit);
		map.put("start",start);
		return super.entityDao.findByLimit(map);
	}
}
