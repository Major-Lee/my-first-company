package com.bhu.vas.business.ds.advertise.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.advertise.model.AdvertiseSharedealDetail;
import com.bhu.vas.business.ds.advertise.dao.AdvertiseSharedealDetailDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
@Service
@Transactional("coreTransactionManager")
public class AdvertiseSharedealDetailService extends AbstractCoreService<Long, AdvertiseSharedealDetail, AdvertiseSharedealDetailDao>{
	@Resource
	@Override
	public void setEntityDao(AdvertiseSharedealDetailDao advertiseSharedealDao) {
		super.setEntityDao(advertiseSharedealDao);
	}
}
