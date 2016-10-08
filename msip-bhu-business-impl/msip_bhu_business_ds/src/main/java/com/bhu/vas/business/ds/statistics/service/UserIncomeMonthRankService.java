package com.bhu.vas.business.ds.statistics.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.bhu.vas.api.rpc.charging.model.UserIncomeRank;
import com.bhu.vas.business.ds.statistics.dao.UserIncomeMonthRankDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
@Transactional("coreTransactionManager")
public class UserIncomeMonthRankService extends AbstractCoreService<Integer, UserIncomeMonthRank, UserIncomeMonthRankDao>{
	@Resource
	@Override
	public void setEntityDao(UserIncomeMonthRankDao userIncomeMonthRankDao) {
		super.setEntityDao(userIncomeMonthRankDao);
	}

	public void deleteAllRank() {
		super.entityDao.deleteAllRank();
	}
	
	public List<UserIncomeMonthRank> findByLimit(String time,int start,int limit){
//		ModelCriteria mc=new ModelCriteria();
//		mc.createCriteria().andSimpleCaulse("1=1").andColumnLike("created_at", time);
//		mc.setOrderByClause("rank");
//		mc.setPageSize(ps);
//		mc.setPageNumber(pn);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("time", time);
		map.put("limit", limit);
		map.put("start",start);
		return super.entityDao.findByLimit(map);
	}
	
	public void updateBytime(String time){
		super.entityDao.updateByTime(time);
	}

	public UserIncomeMonthRank getByUid(int uid,String time) {
//		ModelCriteria mc=new ModelCriteria();
//		mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("uid", uid).andColumnLike("created_at", time);
//		List<UserIncomeMonthRank> incomeRanks= this.findModelByModelCriteria(mc);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("time", time);
		map.put("uid", uid);
		List<UserIncomeMonthRank> incomeRanks= super.entityDao.getByUid(map);
		if(incomeRanks!=null&&incomeRanks.size()>0){
			return incomeRanks.get(0);
		}else{
			return null;
		}
	}
}
