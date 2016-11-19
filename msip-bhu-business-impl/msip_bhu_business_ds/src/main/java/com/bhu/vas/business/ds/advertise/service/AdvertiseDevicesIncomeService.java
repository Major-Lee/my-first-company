package com.bhu.vas.business.ds.advertise.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.advertise.model.AdvertiseDevicesIncome;
import com.bhu.vas.business.ds.advertise.dao.AdvertiseDevicesIncomeDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class AdvertiseDevicesIncomeService extends AbstractCoreService<Integer, AdvertiseDevicesIncome, AdvertiseDevicesIncomeDao>{

	@Resource
	@Override
	public void setEntityDao(AdvertiseDevicesIncomeDao advertiseDevicesIncomeDao) {
		super.setEntityDao(advertiseDevicesIncomeDao);
	}
}
