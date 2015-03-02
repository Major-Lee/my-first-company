package com.bhu.vas.business.statistics.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.statistics.model.UserStatistics;
import com.bhu.vas.business.statistics.dao.UserStatisticsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
@Transactional("coreTransactionManager")
public class UserStatisticsService extends AbstractCoreService<Integer,UserStatistics, UserStatisticsDao>{
	@Resource
	@Override
	public void setEntityDao(UserStatisticsDao userStatisticsDao) {
		super.setEntityDao(userStatisticsDao);
	}
	
	public static final int Action_Share = 1;
	public static final int Action_SubjectUp = 2;
	public static final int Action_SubjectDown = 3;
	public static final int Action_SubjectAbstractUp = 4;
	public static final int Action_SubjectAbstractDown = 5;
	public static final int Action_Estimate = 6;
	public static final int Action_ShareAndEstimate = 10;
	public void increaseUpdate(int uid, int action){
		increaseUpdate(uid,action,1);
	}
	public void increaseUpdate(int uid, int action,int incr){
		boolean update = true;
		UserStatistics entity = super.getById(uid);
		if(entity == null){
			entity = new UserStatistics();
			entity.setId(uid);
			update = false;
		}
		
		switch(action){
			case Action_Share : entity.setShare(entity.getShare()+incr); break;
			case Action_SubjectUp : entity.setS_up(entity.getS_up()+incr); break;
			case Action_SubjectDown : entity.setS_down(entity.getS_down()+incr); break;
			case Action_SubjectAbstractUp : entity.setA_up(entity.getA_up()+incr); break;
			case Action_SubjectAbstractDown : entity.setA_down(entity.getA_down()+incr); break;
			case Action_Estimate : entity.setEstimate(entity.getEstimate()+incr); break;
			case Action_ShareAndEstimate : 
				entity.setShare(entity.getShare()+1);
				entity.setEstimate(entity.getEstimate()+1);
				break;
			default:
				break;
		}
		
		if(update){
			super.update(entity);
		}else{
			super.insert(entity);
		}
	}
	
	public List<UserStatistics> findOrderByShare(int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.setOrderByClause("share desc");
		mc.setStart(start);
		mc.setSize(size);
		return super.findModelByCommonCriteria(mc);
	}
	
	public int count(){
		return super.countByCommonCriteria(new CommonCriteria());
	}
	
}
