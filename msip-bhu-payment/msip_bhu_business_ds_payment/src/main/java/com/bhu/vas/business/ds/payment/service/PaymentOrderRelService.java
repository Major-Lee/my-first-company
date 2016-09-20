package com.bhu.vas.business.ds.payment.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.model.PaymentOrderRel;
import com.bhu.vas.business.ds.payment.dao.PaymentOrderRelDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractPaymentService;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentOrderRelService extends AbstractPaymentService<String, PaymentOrderRel, PaymentOrderRelDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentOrderRelDao PaymentOrderRelDao) {
		super.setEntityDao(PaymentOrderRelDao);
	}

	// 通过Upay支付订单号修改订单
	// 支持billno,others,pay_status,notify_status,updated_at.数据修改
	/*public void updateByReckoningId(PaymentReckoning data) {
		long id = this.getIdByReckoningId(data.getReckoning_id());
		data.setId(id);
		data.setPaid_at(new Date());
		this.update(data);
		return;
	}*/

}
