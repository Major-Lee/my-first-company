package com.bhu.vas.business.subject.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.SubjectTagState;
import com.bhu.vas.business.subject.dao.SubjectTagStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class SubjectTagStateService extends AbstractCoreService<Integer,SubjectTagState, SubjectTagStateDao>{
	@Resource
	@Override
	public void setEntityDao(SubjectTagStateDao subjectTagStateDao) {
		super.setEntityDao(subjectTagStateDao);
	}
	
}
