package com.bhu.vas.business.ds.statistics.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.bhu.vas.business.ds.statistics.dao.UserIncomeMonthRankDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
@Transactional("coreTransactionManager")
public class UserIncomeMonthRankService extends AbstractCommdityService<String, UserIncomeMonthRank, UserIncomeMonthRankDao>{
	@Resource
	@Override
	public void setEntityDao(UserIncomeMonthRankDao userIncomeMonthRankDao) {
		super.setEntityDao(userIncomeMonthRankDao);
	}

	public void deleteAllRank() {
		super.entityDao.deleteAllRank();
	}
	
	public List<UserIncomeMonthRank> findByLimit(String time,int pn,int ps){
		ModelCriteria mc=new ModelCriteria();
		mc.createCriteria().andColumnLike("created_at", time);
		mc.setOrderByClause("rank");
		mc.setPageSize(ps);
		mc.setPageNumber(pn);
		return super.entityDao.findByLimit(time);
	}
	
	public void updateBytime(String time){
		super.entityDao.updateByTime(time);
	}

	public UserIncomeMonthRank getByUid(int uid,String time) {
		ModelCriteria mc=new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid).andColumnLike("created_at", time);
		List<UserIncomeMonthRank> incomeRanks= this.findModelByModelCriteria(mc);
		if(incomeRanks!=null&&incomeRanks.size()>0){
			return incomeRanks.get(0);
		}else{
			return null;
		}
	}
}
