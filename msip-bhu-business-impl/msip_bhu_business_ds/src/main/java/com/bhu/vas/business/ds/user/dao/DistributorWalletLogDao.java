package com.bhu.vas.business.ds.user.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.distributor.model.DistributorWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.smartwork.msip.business.abstractmsd.dao.AbstractSharedealDao;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */
@Repository
public class DistributorWalletLogDao extends AbstractCoreDao<Long, DistributorWalletLog>{
	public List<Map<String,Object>> bhuAccountIncome(Map<String, Object> map) {
		return super.getSqlSessionSlaverTemplate().selectList(DistributorWalletLog.class.getName()+".bhuAccountIncome",map);
	}
}
