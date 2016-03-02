package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.business.ds.user.dao.UserWalletDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserWalletService extends AbstractCoreService<Integer,UserWallet, UserWalletDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	//@Resource
	//private SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(UserWalletDao userWalletDao) {
		super.setEntityDao(userWalletDao);
	}
	
}
