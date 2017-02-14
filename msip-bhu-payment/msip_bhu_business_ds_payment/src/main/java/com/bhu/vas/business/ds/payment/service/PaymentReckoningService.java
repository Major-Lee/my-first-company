package com.bhu.vas.business.ds.payment.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.dto.PaymentErrorCountDTO;
import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.business.ds.payment.dao.PaymentReckoningDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractPaymentService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentReckoningService extends AbstractPaymentService<String, PaymentReckoning, PaymentReckoningDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentReckoningDao PaymentReckoningDao) {
		super.setEntityDao(PaymentReckoningDao);
	}

	// 通过Upay支付订单号查找订单
	public PaymentReckoning findByReckoningId(String tid) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("reckoning_id", tid);
		List<PaymentReckoning> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}

	// 通过商品中心支付订单号查找订单
	public PaymentReckoning findByOrderId(String gid) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("order_id", gid);
		List<PaymentReckoning> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}
	
	// 通过第三方支付订单号查找订单
	public PaymentReckoning findByThreeOrderId(String gid) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("remark", gid);
		List<PaymentReckoning> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}
	// 统计当前订单的错单情况
	public List<PaymentReckoning> findErrorCountByType(String startTime,String endTime,String channelType) {
		ModelCriteria mc = new ModelCriteria();
		//mc.createCriteria().andColumnEqualTo("appid", 1000);
		//mc.createCriteria().andColumnGreaterThanOrEqualTo("created_at ", startTime);
		//mc.createCriteria().andColumnLessThan("created_at ", endTime);
		mc.createCriteria().andColumnEqualTo("payment_type ", "WapWeixin");
		mc.createCriteria().andColumnEqualTo("channel_type", channelType);
		//mc.createCriteria().andColumnGreaterThanOrEqualTo("UNIX_TIMESTAMP(paid_at) - UNIX_TIMESTAMP(created_at)", 120);
		return this.findModelByModelCriteria(mc);
	}
	
	public PaymentErrorCountDTO paymentRecordInfo(){
		PaymentErrorCountDTO recordInfoDTO = new PaymentErrorCountDTO();
		int executeRet = this.executeProcedure(recordInfoDTO);
		if(executeRet != 0)
			System.out.println(executeRet);
		
		return recordInfoDTO;
	}

	// 通过Upay支付订单号获取订单ID
	/*public Long getIdByReckoningId(String tid) {
		PaymentReckoning one = this.findByReckoningId(tid);
		return one == null ? null : one.getId();
	}*/

	// 通过Upay支付订单号获取订单Token
	public String getTokenByReckoningId(String tid) {
		PaymentReckoning one = this.findByReckoningId(tid);
		return one == null ? null : one.getToken();
	}

	// 获取今日支付订单总数
	public int countOfToday() {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnGreaterThan("created_at",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		return this.countByModelCriteria(mc);
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

	// 通过Upay支付订单号查询订单是否支付，订单不存在也表示未支付
	public boolean isPay(String tid) {
		PaymentReckoning one = this.findByReckoningId(tid);
		if (one == null) {
			return false;
		}
		return one.getPay_status() == 1 ? true : false;
	}

	// 判断商品中心支付订单号是否已经存在
	public boolean isExistByOrderId(String gid) {
		PaymentReckoning one = this.findByOrderId(gid);
		return one == null ? false : true;
	}
	
	public List<PaymentReckoning> listToday(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnGreaterThan("created_at",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		return this.findModelByModelCriteria(mc);
	}
	
	public int countOfAll() {
		ModelCriteria mc = new ModelCriteria();
		return this.countByModelCriteria(mc);
	}
	
	public int countOfUser(){
		ModelCriteria mc = new ModelCriteria();
		
		return this.countByModelCriteria(mc);
	}
	
}
