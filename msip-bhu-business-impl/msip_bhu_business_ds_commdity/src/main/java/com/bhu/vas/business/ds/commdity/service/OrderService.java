package com.bhu.vas.business.ds.commdity.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.ds.commdity.dao.OrderDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("commdityTransactionManager")
public class OrderService extends AbstractCoreService<Integer,Order, OrderDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(OrderDao orderDao) {
		super.setEntityDao(orderDao);
	}
}
