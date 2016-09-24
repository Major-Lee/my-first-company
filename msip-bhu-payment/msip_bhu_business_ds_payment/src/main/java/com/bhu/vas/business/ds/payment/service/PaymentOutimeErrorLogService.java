package com.bhu.vas.business.ds.payment.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.model.PaymentOutimeErrorLog;
import com.bhu.vas.business.ds.payment.dao.PaymentOutimeErrorLogDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractPaymentService;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentOutimeErrorLogService extends AbstractPaymentService<String, PaymentOutimeErrorLog, PaymentOutimeErrorLogDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentOutimeErrorLogDao PaymentOutimeErrorLoggDao) {
		super.setEntityDao(PaymentOutimeErrorLoggDao);
	}
}
