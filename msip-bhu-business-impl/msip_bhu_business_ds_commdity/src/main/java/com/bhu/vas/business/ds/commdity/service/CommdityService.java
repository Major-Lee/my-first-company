package com.bhu.vas.business.ds.commdity.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.business.ds.commdity.dao.CommdityDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;
@Service
@Transactional("commdityTransactionManager")
public class CommdityService extends AbstractCommdityService<Integer, Commdity, CommdityDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(CommdityDao commdityDao) {
		super.setEntityDao(commdityDao);
	}

/*	public int porcByCommdityTest(CommdityPorcDTO dto){
		return super.getEntityDao().porcByCommdityTest(dto);
	}*/
}
