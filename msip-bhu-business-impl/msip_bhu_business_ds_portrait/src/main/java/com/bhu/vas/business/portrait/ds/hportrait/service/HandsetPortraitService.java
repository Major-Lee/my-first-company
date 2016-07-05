package com.bhu.vas.business.portrait.ds.hportrait.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.portrait.model.HandsetPortrait;
import com.bhu.vas.business.portrait.ds.hportrait.dao.HandsetPortraitDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractPortraitService;

@Service
@Transactional("portraitTransactionManager")
public class HandsetPortraitService extends AbstractPortraitService<String,HandsetPortrait, HandsetPortraitDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(HandsetPortraitDao handsetPortraitDao) {
		super.setEntityDao(handsetPortraitDao);
	}
	
}
