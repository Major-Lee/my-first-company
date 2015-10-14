package com.bhu.vas.business.ds.task.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.business.ds.task.dao.VasModuleCmdDefinedDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class VasModuleCmdDefinedService extends AbstractCoreService<VasModuleCmdPK,VasModuleCmdDefined, VasModuleCmdDefinedDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(VasModuleCmdDefinedDao vasModuleCmdDefinedDao) {
		super.setEntityDao(vasModuleCmdDefinedDao);
	}
}
