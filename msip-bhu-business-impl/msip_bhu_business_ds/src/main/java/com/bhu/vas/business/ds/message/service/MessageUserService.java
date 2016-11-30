package com.bhu.vas.business.ds.message.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.message.model.MessageUser;
import com.bhu.vas.business.ds.message.dao.MessageUserDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class MessageUserService extends AbstractCoreService<String,MessageUser, MessageUserDao>{
	
	@Resource
	@Override
	public void setEntityDao(MessageUserDao messageUserDao) {
		super.setEntityDao(messageUserDao);
	}
}
