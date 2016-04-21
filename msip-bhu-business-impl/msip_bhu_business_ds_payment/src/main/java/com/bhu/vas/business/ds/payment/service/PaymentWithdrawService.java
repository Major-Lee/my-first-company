package com.bhu.vas.business.ds.payment.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.model.PaymentOrder;
import com.bhu.vas.api.rpc.payment.model.PaymentWithdraw;
import com.bhu.vas.business.ds.payment.dao.PaymentWithdrawDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractTagService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentWithdrawService extends
		AbstractTagService<Long, PaymentWithdraw, PaymentWithdrawDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentWithdrawDao paymentWithdrawDao) {
		super.setEntityDao(paymentWithdrawDao);
	}

	// 通过Upay提现订单号查找订单
	public PaymentWithdraw findByTid(String tid) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("tid", tid).andSimpleCaulse("1=1");
		List<PaymentWithdraw> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}

	// 通过商品中心提现订单号查找订单
	public PaymentWithdraw findByWid(String wid) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("wid", wid).andSimpleCaulse("1=1");
		List<PaymentWithdraw> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}

	// 通过Upay提现订单号获取订单ID
	public Long getIdByTid(String tid) {
		PaymentWithdraw one = this.findByTid(tid);
		return one == null ? null : one.getId();
	}

	// 获取今日支付宝提现订单总数
	public int countOfTodayAlipay() {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()
				.andColumnGreaterThan("created_at",
						new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.andColumnEqualTo("withdraw_type", "alipay");
		return this.countByModelCriteria(mc);
	}

	// 通过Upay提现订单号修改订单
	// 支持billno,withdraw_status,notify_status,fail_cause,updated.at.修改
	public void updateByTid(PaymentWithdraw data) {
		long id = this.getIdByTid(data.getTid());
		data.setId(id);
		data.setUpdated_at(new Date());
		this.update(data);
		return;
	}

	// 通过Upay提现订单号查询是否提现成功，没有订单也表示提现未成功
	public boolean isWithdraw(String tid) {
		PaymentWithdraw one = this.findByTid(tid);
		if (one == null) {
			return false;
		}
		return one.getWithdraw_status() == 1 ? true : false;
	}

	// 判断商品中心提现订单号是否已经存在
	public boolean isExistByWid(String wid) {
		PaymentWithdraw one = this.findByWid(wid);
		return one == null ? false : true;
	}

}