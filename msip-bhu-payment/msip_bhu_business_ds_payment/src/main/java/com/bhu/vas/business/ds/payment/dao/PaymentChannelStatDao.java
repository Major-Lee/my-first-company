package com.bhu.vas.business.ds.payment.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.payment.model.PaymentChannelStat;
import com.smartwork.msip.business.abstractmsd.dao.AbstractPaymentDao;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */
@Repository
public class PaymentChannelStatDao extends AbstractPaymentDao<String, PaymentChannelStat>{

	public List<Object> platformIncome(Map<String, Object> map) {
		return super.getSqlSessionSlaverTemplate().selectList(PaymentChannelStat.class.getName()+".platformIncome",map);
		
	}
	
}
