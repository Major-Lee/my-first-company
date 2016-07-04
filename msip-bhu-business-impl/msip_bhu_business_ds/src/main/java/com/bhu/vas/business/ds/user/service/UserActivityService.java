package com.bhu.vas.business.ds.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserActivity;
import com.bhu.vas.business.ds.user.dao.UserActivityDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserActivityService extends AbstractCoreService<Integer,UserActivity, UserActivityDao>{

}
