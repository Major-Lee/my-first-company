package com.bhu.vas.business.ds.distributor.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.distributor.model.DistributorWalletLog;
import com.bhu.vas.business.ds.distributor.dao.DistributorWalletLogDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractSharedealService;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("sharedealTransactionManager")
public class DistributorWalletLogService
		extends
		AbstractSharedealService<Long, DistributorWalletLog, DistributorWalletLogDao> {

	@Resource
	@Override
	public void setEntityDao(DistributorWalletLogDao distributorWalletLog) {
		super.setEntityDao(distributorWalletLog);
	}

}