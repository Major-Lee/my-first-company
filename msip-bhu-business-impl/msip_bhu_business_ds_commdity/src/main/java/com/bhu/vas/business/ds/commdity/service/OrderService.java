package com.bhu.vas.business.ds.commdity.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.ds.commdity.dao.OrderDao;
import com.bhu.vas.business.ds.commdity.helper.OrderHelper;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;
@Service
@Transactional("commdityTransactionManager")
public class OrderService extends AbstractCommdityService<String, Order, OrderDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(OrderDao orderDao) {
		super.setEntityDao(orderDao);
	}
	
	@Override
	public Order insert(Order entity) {
		//如果订单id为空 会默认生成订单id 并且扩展占位为零
		if(entity.getId() == null){
			entity.setId(OrderHelper.generateOrderId(entity.getAppid()));
		}
		return super.insert(entity);
	}
}
