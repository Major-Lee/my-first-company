package com.bhu.vas.business.ds.payment.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.model.PaymentCallbackSum;
import com.bhu.vas.business.ds.payment.dao.PaymentCallbackSumDao;
import com.bhu.vas.business.payment.help.BusinessHelper;
import com.smartwork.msip.business.abstractmsd.service.AbstractPaymentService;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentCallbackSumService extends AbstractPaymentService<String, PaymentCallbackSum, PaymentCallbackSumDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentCallbackSumDao PaymentCallbackSumDao) {
		super.setEntityDao(PaymentCallbackSumDao);
	}

	public void updateAddScores(int subtotal,String reckonNo) {
		PaymentCallbackSum one = this.getById(reckonNo);
		if (one == null) {
			one = new PaymentCallbackSum();
			one.setId(reckonNo);
			one.setSubtotal(subtotal);
			one.setChanged_at(new Date());
			this.insert(one);
		}else{
			one.setSubtotal(subtotal+one.getSubtotal());
			one.setUpdated_at(new Date());
			this.update(one);
		}
	}
	
	public static void main(String[] args) {
		String reckonId = "INNERAPWX1472099514472jshy";
		String curStr = "";
		if(reckonId.startsWith("PRO")){
			curStr = reckonId.substring(3, 7);
		}else if(reckonId.startsWith("TEST")){
			curStr = reckonId.substring(4, 8);
		}else if(reckonId.startsWith("INNER")){
			curStr = reckonId.substring(5, 9);
		}
		System.out.println(curStr);
	}
}
