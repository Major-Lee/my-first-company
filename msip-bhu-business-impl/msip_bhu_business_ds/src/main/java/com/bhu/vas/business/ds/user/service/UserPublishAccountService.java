package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserPublishAccount;
import com.bhu.vas.business.ds.user.dao.UserPublishAccountDao;
import com.smartwork.msip.cores.orm.service.EntityService;

@Service
@Transactional("coreTransactionManager")
public class UserPublishAccountService extends EntityService<Integer,UserPublishAccount, UserPublishAccountDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	//@Resource
	//private SequenceService sequenceService;
	@Resource
	@Override
	public void setEntityDao(UserPublishAccountDao userPublishAccountDao) {
		super.setEntityDao(userPublishAccountDao);
	}
	
}
