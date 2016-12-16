package com.bhu.vas.business.ds.commdity.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.commdity.dao.OrderDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
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
			SequenceService.getInstance().onCreateSequenceKey(entity, false);
		}
		return super.insert(entity);
	}
	public int countByType(int type,int data,String date){
		ModelCriteria mc=new ModelCriteria();
		//type 0:type 1:channel
		if(type!=0){
			mc.createCriteria().andColumnEqualTo("channel", data).andColumnLike("created_at", date).andColumnEqualTo("status", 10);
		}else{
			mc.createCriteria().andColumnEqualTo("type", data).andColumnLike("created_at", date).andColumnEqualTo("status", 10);
		}
		int n=super.countByCommonCriteria(mc);
		return n;
	}
	
	public void dropCache(String orderid){
		this.getEntityCache().remove(orderid);
	}
}
