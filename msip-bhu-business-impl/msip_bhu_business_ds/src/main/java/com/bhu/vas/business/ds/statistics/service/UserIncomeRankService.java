package com.bhu.vas.business.ds.statistics.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.UserIncomeRank;
import com.bhu.vas.business.ds.statistics.dao.UserIncomeRankDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

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
	
	public List<UserIncomeRank> findByLimit(String time,int pn,int ps){
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

	public UserIncomeRank getByUid(int uid,String time) {
		ModelCriteria mc=new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid).andColumnLike("created_at", time);
		List<UserIncomeRank> incomeRanks= this.findModelByModelCriteria(mc);
		if(incomeRanks!=null&&incomeRanks.size()>0){
			return incomeRanks.get(0);
		}else{
			return null;
		}
	}
}
