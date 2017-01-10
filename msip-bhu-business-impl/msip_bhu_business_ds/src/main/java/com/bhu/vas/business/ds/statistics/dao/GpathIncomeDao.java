package com.bhu.vas.business.ds.statistics.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.GpathIncome;
import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class GpathIncomeDao extends AbstractCoreDao<Integer, GpathIncome>{

	public List<GpathIncome> findByLimit(Map<String, Object> map) {
		return super.getSqlSessionSlaverTemplate().selectList(GpathIncome.class.getName()+".findGpathIncomeByLimit",map);
		
	}

}
