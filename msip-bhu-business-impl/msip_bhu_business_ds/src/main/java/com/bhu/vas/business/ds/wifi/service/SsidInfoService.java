package com.bhu.vas.business.ds.wifi.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.wifi.model.SsidInfo;
import com.bhu.vas.business.ds.wifi.dao.SsidInfoDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
@Service
@Transactional("coreTransactionManager")
public class SsidInfoService extends AbstractCoreService<String, SsidInfo, SsidInfoDao>{
	@Resource
	@Override
	public void setEntityDao(SsidInfoDao ssidInfoDao) {
		super.setEntityDao(ssidInfoDao);
	}
}
