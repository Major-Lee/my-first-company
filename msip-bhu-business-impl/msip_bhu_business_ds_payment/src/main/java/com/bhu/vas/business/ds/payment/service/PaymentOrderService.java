package com.bhu.vas.business.ds.payment.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.model.PaymentOrder;
import com.bhu.vas.business.ds.payment.dao.PaymentOrderDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractTagService;

/**
 * 
 * @author xiaowei
 *		by 16/04/12
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentOrderService extends AbstractTagService<Long, PaymentOrder, PaymentOrderDao> {

    @Resource
    @Override
    public void setEntityDao(PaymentOrderDao paymentOrderDao) {
        super.setEntityDao(paymentOrderDao);
    }
    
}