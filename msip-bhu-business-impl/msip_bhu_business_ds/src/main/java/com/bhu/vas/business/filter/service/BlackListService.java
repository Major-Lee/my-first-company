package com.bhu.vas.business.filter.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.filter.model.BlackList;
import com.bhu.vas.business.filter.dao.BlackListDao;
import com.bhu.vas.business.sequence.service.SequenceService;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
@Service
@Transactional("coreTransactionManager")
public class BlackListService extends AbstractCoreService<String,BlackList, BlackListDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	private SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(BlackListDao blackListDao) {
		super.setEntityDao(blackListDao);
	}
}
