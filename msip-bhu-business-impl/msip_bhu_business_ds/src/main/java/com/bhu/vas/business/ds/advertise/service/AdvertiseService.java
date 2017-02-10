package com.bhu.vas.business.ds.advertise.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.advertise.dao.AdvertiseDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
@Service
@Transactional("coreTransactionManager")
public class AdvertiseService extends AbstractCoreService<String, Advertise, AdvertiseDao>{
	@Resource
	@Override
	public void setEntityDao(AdvertiseDao advertiseDao) {
		super.setEntityDao(advertiseDao);
	}
	
//	@Override
//	public Advertise insert(Advertise entity) {
//		//如果订单id为空 会默认生成订单id 并且扩展占位为零
//		if(entity.getId() == null){
//			SequenceService.getInstance().onCreateSequenceKey(entity, false);
//		}
//		return super.insert(entity);
//	}
}
