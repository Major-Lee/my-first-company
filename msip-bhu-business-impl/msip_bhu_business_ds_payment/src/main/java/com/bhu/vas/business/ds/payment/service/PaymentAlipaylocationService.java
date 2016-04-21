package com.bhu.vas.business.ds.payment.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.model.PaymentAlipaylocation;
import com.bhu.vas.api.rpc.payment.model.PaymentWithdraw;
import com.bhu.vas.business.ds.payment.dao.PaymentAlipaylocationDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractTagService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentAlipaylocationService
		extends
		AbstractTagService<Long, PaymentAlipaylocation, PaymentAlipaylocationDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentAlipaylocationDao paymentAlipaylocationDao) {
		super.setEntityDao(paymentAlipaylocationDao);
	}

	// 通过Upay订单号获取跳转地址，用于支付宝回调跳转
	public String getLocationByTid(String tid) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("tid", tid).andSimpleCaulse("1=1");
		List<PaymentAlipaylocation> list = this.findModelByModelCriteria(mc);
		if (list.isEmpty()) {
			return null;
		}
		PaymentAlipaylocation one = list.get(0);
		return one.getLocation();
	}

}