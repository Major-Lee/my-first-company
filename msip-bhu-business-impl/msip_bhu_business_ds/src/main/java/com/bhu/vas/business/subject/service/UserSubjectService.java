package com.bhu.vas.business.subject.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.UserSubject;
import com.bhu.vas.business.subject.dao.UserSubjectDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserSubjectService extends AbstractCoreService<Integer,UserSubject, UserSubjectDao>{
	@Resource
	@Override
	public void setEntityDao(UserSubjectDao userSubjectDao) {
		super.setEntityDao(userSubjectDao);
	}
	
}
