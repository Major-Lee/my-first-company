package com.bhu.vas.business.ds.payment.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.dto.PaymentRecordInfoDTO;
import com.bhu.vas.api.rpc.payment.model.PaymentParameter;
import com.bhu.vas.api.rpc.payment.model.PaymentRecord;
import com.bhu.vas.business.ds.payment.dao.PaymentParameterDao;
import com.bhu.vas.business.ds.payment.dao.PaymentRecordDao;
import com.smartwork.msip.cores.orm.service.EntityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentParameterService extends EntityService<String, PaymentParameter, PaymentParameterDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentParameterDao paymentParameterDao) {
		super.setEntityDao(paymentParameterDao);
	}


	public PaymentParameter findByName(String name) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("name", name);
		List<PaymentParameter> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}
	
}
