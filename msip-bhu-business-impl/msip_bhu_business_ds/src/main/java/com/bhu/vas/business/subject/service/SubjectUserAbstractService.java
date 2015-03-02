package com.bhu.vas.business.subject.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.SubjectUserAbstract;
import com.bhu.vas.business.subject.dao.SubjectUserAbstractDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
@Transactional("coreTransactionManager")
public class SubjectUserAbstractService extends AbstractCoreService<Integer,SubjectUserAbstract, SubjectUserAbstractDao>{
	@Resource
	@Override
	public void setEntityDao(SubjectUserAbstractDao subjectUserAbstractDao) {
		super.setEntityDao(subjectUserAbstractDao);
	}

	public int countBySubjectId(int subject_id){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("subjectid", subject_id);
		return super.countByCommonCriteria(mc);
	}
	
	public List<SubjectUserAbstract> findModelBySubjectId(int subject_id){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("subjectid", subject_id);
		return super.findModelByCommonCriteria(mc);
	}
}
