package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserConfigsState;
import com.bhu.vas.business.ds.user.dao.UserConfigsStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserConfigsStateService extends AbstractCoreService<Integer, UserConfigsState, UserConfigsStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserConfigsStateDao userConfigsStateDao) {
		super.setEntityDao(userConfigsStateDao);
	}
	
}
