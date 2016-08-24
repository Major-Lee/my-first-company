package com.bhu.vas.business.ds.statistics.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.UserIncomeRank;
import com.bhu.vas.business.ds.statistics.dao.UserIncomeRankDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;

@Service
@Transactional("coreTransactionManager")
public class UserIncomeRankService extends AbstractCommdityService<String, UserIncomeRank, UserIncomeRankDao>{
	@Resource
	@Override
	public void setEntityDao(UserIncomeRankDao userIncomeRankDao) {
		super.setEntityDao(userIncomeRankDao);
	}

	public void deleteAllRank() {
		super.entityDao.deleteAllRank();
	}
	
	public List<UserIncomeRank> findByLimit(String time){
		return super.entityDao.findByLimit(time);
	}
}
