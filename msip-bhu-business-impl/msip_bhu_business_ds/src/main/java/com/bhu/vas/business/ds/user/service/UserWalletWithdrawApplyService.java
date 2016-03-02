package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.user.dao.UserWalletWithdrawApplyDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserWalletWithdrawApplyService extends AbstractCoreService<Long,UserWalletWithdrawApply, UserWalletWithdrawApplyDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	@Resource
	@Override
	public void setEntityDao(UserWalletWithdrawApplyDao userWalletWithdrawApplyDao) {
		super.setEntityDao(userWalletWithdrawApplyDao);
	}
	
	@Override
    public UserWalletWithdrawApply insert(UserWalletWithdrawApply entity) {
        if(entity.getId() == null)
            SequenceService.getInstance().onCreateSequenceKey(entity, false);
        return super.insert(entity);
    }
}
