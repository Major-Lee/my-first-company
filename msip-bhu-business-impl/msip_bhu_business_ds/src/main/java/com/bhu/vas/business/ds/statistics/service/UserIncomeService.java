package com.bhu.vas.business.ds.statistics.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.bhu.vas.business.ds.statistics.dao.UserIncomeDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
@Transactional("coreTransactionManager")
public class UserIncomeService extends AbstractCoreService<String, UserIncome, UserIncomeDao>{
	@Resource
	@Override
	public void setEntityDao(UserIncomeDao userIncomeDao) {
		super.setEntityDao(userIncomeDao);
	}
	public List<UserIncome> findListByTime(String time){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()
		.andColumnLike("time", time+"%");
		mc.setOrderByClause("income*1");
		return super.findModelByModelCriteria(mc);
	}
	public List<UserIncome> findListByUid(int uid,String time){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()
		.andColumnEqualTo("uid", uid)
		.andColumnEqualTo("time", time);
		mc.setOrderByClause("income");
		return super.findModelByModelCriteria(mc);
	}
	public List<UserIncome> findMonthList(String time){
		return super.entityDao.findMonthList(time);
	}
	
	public List<UserIncome> findByLimit(String time,int start,int limit){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("time", time);
		map.put("limit", limit);
		map.put("start",start);
		return super.entityDao.findByLimit(map);
	}
}
