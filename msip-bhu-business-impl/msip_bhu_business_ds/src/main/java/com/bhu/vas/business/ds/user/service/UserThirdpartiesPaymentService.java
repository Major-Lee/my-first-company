/*package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserThirdpartiesPayment;
import com.bhu.vas.business.ds.user.dao.UserThirdpartiesPaymentDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserThirdpartiesPaymentService extends AbstractCoreService<Integer,UserThirdpartiesPayment, UserThirdpartiesPaymentDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	@Resource
	@Override
	public void setEntityDao(UserThirdpartiesPaymentDao userThirdpartiesPaymentDao) {
		super.setEntityDao(userThirdpartiesPaymentDao);
	}
}
*/