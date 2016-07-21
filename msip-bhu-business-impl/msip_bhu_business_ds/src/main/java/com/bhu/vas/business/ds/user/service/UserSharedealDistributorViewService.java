package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserSharedealDistributorView;
import com.bhu.vas.business.ds.user.dao.UserSharedealDistributorViewDao;
import com.smartwork.msip.cores.orm.service.EntityService;

@Service
@Transactional("coreTransactionManager")
public class UserSharedealDistributorViewService extends EntityService<Integer, UserSharedealDistributorView, UserSharedealDistributorViewDao>{//AbstractCoreService<String,UserWalletWithdrawApply, UserWalletWithdrawApplyDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	@Resource
	@Override
	public void setEntityDao(UserSharedealDistributorViewDao userSharedealDistributorViewDao) {
		super.setEntityDao(userSharedealDistributorViewDao);
	}

}
