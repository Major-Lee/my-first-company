package com.bhu.vas.business.subject.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.UserAbstract;
import com.bhu.vas.business.subject.dao.UserAbstractDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserAbstractService extends AbstractCoreService<Integer, UserAbstract, UserAbstractDao>{
	@Resource
	@Override
	public void setEntityDao(UserAbstractDao userAbstractDao) {
		super.setEntityDao(userAbstractDao);
	}

}
