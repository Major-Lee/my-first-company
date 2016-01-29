package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserTest;
import com.bhu.vas.business.ds.user.dao.UserTestDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserTestServiceDemo extends AbstractCoreService<Integer, UserTest, UserTestDao> {

	@Resource
	@Override
	public void setEntityDao(UserTestDao userTestDao) {
		// TODO Auto-generated method stub
		super.setEntityDao(userTestDao);
	}
}
