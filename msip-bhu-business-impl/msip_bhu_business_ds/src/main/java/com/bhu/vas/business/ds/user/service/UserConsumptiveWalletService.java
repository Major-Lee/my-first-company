package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserConsumptiveWallet;
import com.bhu.vas.business.ds.user.dao.UserConsumptiveWalletDao;
import com.smartwork.msip.cores.orm.service.EntityService;

@Service
@Transactional("coreTransactionManager")
public class UserConsumptiveWalletService extends EntityService<Integer,UserConsumptiveWallet, UserConsumptiveWalletDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	//@Resource
	//private SequenceService sequenceService;
	@Resource
	@Override
	public void setEntityDao(UserConsumptiveWalletDao userConsumptiveWalletDao) {
		super.setEntityDao(userConsumptiveWalletDao);
	}
	
}
