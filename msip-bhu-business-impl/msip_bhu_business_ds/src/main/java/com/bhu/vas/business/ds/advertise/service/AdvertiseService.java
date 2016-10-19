package com.bhu.vas.business.ds.advertise.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.ds.advertise.dao.AdvertiseDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
@Service
@Transactional("coreTransactionManager")
public class AdvertiseService extends AbstractCoreService<Integer, Advertise, AdvertiseDao>{
	@Resource
	@Override
	public void setEntityDao(AdvertiseDao advertiseDao) {
		super.setEntityDao(advertiseDao);
	}
}
