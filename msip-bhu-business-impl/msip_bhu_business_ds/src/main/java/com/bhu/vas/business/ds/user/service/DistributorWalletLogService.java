package com.bhu.vas.business.ds.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Transactional("coreTransactionManager")
public class DistributorWalletLogService
		extends
		AbstractCoreService<Long, DistributorWalletLog, DistributorWalletLogDao> {

	@Resource
	@Override
	public void setEntityDao(DistributorWalletLogDao distributorWalletLog) {
		super.setEntityDao(distributorWalletLog);
	}
	
	public List<Map<String,Object>> queryPlanInfo(String preDay, String today) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("pre_day", preDay);
		map.put("today", today);
		return super.entityDao.bhuAccountIncome(map);
	}
}