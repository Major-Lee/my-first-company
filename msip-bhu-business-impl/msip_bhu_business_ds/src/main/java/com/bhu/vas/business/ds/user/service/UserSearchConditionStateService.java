package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserSearchConditionState;
import com.bhu.vas.business.ds.user.dao.UserSearchConditionStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserSearchConditionStateService extends AbstractCoreService<Integer,UserSearchConditionState,UserSearchConditionStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserSearchConditionStateDao userSearchConditionStateDao) {
		super.setEntityDao(userSearchConditionStateDao);
	}
	
}
