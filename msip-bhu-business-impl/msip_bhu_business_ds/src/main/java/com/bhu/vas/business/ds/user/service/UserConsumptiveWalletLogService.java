package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserConsumptiveWalletLog;
import com.bhu.vas.business.ds.user.dao.UserConsumptiveWalletLogDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserConsumptiveWalletLogService extends AbstractCoreService<Long,UserConsumptiveWalletLog, UserConsumptiveWalletLogDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	//@Resource
	//private SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(UserConsumptiveWalletLogDao userConsumptiveWalletLogDao) {
		super.setEntityDao(userConsumptiveWalletLogDao);
	}
}
