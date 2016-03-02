package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWithdrawApply;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.user.dao.UserWithdrawApplyDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class UserWithdrawApplyService extends AbstractCoreService<Long,UserWithdrawApply, UserWithdrawApplyDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	//@Resource
	//private SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(UserWithdrawApplyDao userWithdrawApplyDao) {
		super.setEntityDao(userWithdrawApplyDao);
	}
	
	@Override
    public UserWithdrawApply insert(UserWithdrawApply entity) {
        if(entity.getId() == null)
            SequenceService.getInstance().onCreateSequenceKey(entity, false);
        return super.insert(entity);
    }
}
