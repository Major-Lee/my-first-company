package com.bhu.vas.business.ds.payment.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.dto.PaymentErrorCountDTO;
import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.business.ds.payment.dao.PaymentReckoningDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractTagService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentReckoningService extends
		AbstractTagService<String, PaymentReckoning, PaymentReckoningDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentReckoningDao PaymentReckoningDao) {
		super.setEntityDao(PaymentReckoningDao);
	}


	// 通过商品中心支付订单号查找订单
	public PaymentReckoning findByOrderId(String gid) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("order_id", gid);
		List<PaymentReckoning> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}
	public PaymentErrorCountDTO paymentRecordInfo(){
		PaymentErrorCountDTO recordInfoDTO = new PaymentErrorCountDTO();
		int executeRet = this.executeProcedure(recordInfoDTO);
		if(executeRet != 0)
			System.out.println(executeRet);
		
		return recordInfoDTO;
	}
}
