package com.bhu.vas.business.payment;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.payment.help.BusinessHelper;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomPicker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentReckoningServiceTest extends BaseTest {
	@Resource
	PaymentReckoningService paymentReckoningService;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	@Test
	public void test001BatchCreateOrder() {
		for (int i = 1; i < 2; i++) {
			PaymentReckoning order = new PaymentReckoning();
			String reckoningId = BusinessHelper.generatePaymentReckoningNoByType("test");
			order.setId(reckoningId);
			order.setOrder_id(RandomPicker.randString(letters, 10));
			order.setAmount(Integer.parseInt(BusinessHelper.getMoney(0.1+"")));
			order.setPayment_type("PcAlipay");
			order.setOpenid("BHUUSERMAC000000000000");
			order.setSubject("打赏");
			order.setExter_invoke_ip("213.42.3.24");
			order.setAppid("1000");
			order.setToken(RandomPicker.randString(letters, 10));
			paymentReckoningService.insert(order);
		}
	}

//	@Test
//	public void test002FindByTid() {
//		PaymentReckoning one1 = paymentReckoningService.findByTid("iswianwjps");
//		System.out.println(one1.getToken());
//
//		PaymentReckoning one2 = paymentReckoningService.findByTid("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test003FindByGid() {
////		PaymentReckoning one1 = paymentReckoningService.findByOrderId("nfupyategh");
////		System.out.println(one1.getToken());
////
////		PaymentReckoning one2 = paymentReckoningService.findByOrderId("bucunzai");
////		System.out.println(one2);
//	}
//
/*	@Test
	public void test004GetIdByTid() {
		PaymentReckoning  aa = paymentReckoningService.findByReckoningId("PCAL1463036536532zvfm");
		String orderId = aa.getOrder_id();
		PaymentReckoning updatePayStatus = new PaymentReckoning();
    	updatePayStatus.setOrder_id(orderId);
		updatePayStatus.setReckoning_id(out_trade_no);
		updatePayStatus.setThird_party_code(thirdPartCode);
		updatePayStatus.setPay_status(1);
 		paymentReckoningService.updateByReckoningId(updatePayStatus);
 		
 		logger.info("订单："+orderId+";支付流水号："+out_trade_no+"支付状态已修改完成.");
 		
 		//通知订单
 		PaymentReckoning payNotice =  paymentReckoningService.findByReckoningId(out_trade_no);
 		ResponsePaymentCompletedNotifyDTO rpcn_dto = new ResponsePaymentCompletedNotifyDTO();
 		rpcn_dto.setSuccess(true);
 		rpcn_dto.setOrderid(payNotice.getOrder_id());
 		rpcn_dto.setPayment_type(payNotice.getPayment_type());
 		String fmtDate = BusinessHelper.formatDate(payNotice.getPaid_at(), "yyyy-MM-dd HH:mm:ss");
 		rpcn_dto.setPaymented_ds(fmtDate);
 		String notify_message = JsonHelper.getJSONString(rpcn_dto);
 		System.out.println(notify_message);
 		CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(notify_message);
 		
 		logger.info("订单："+orderId+";支付流水号："+out_trade_no+"已写入Redis.");
 		
 		//修改订单的通知状态
 		PaymentReckoning updatePayNoticeStatus = new PaymentReckoning();
 		updatePayNoticeStatus.setReckoning_id(out_trade_no);
 		updatePayNoticeStatus.setNotify_status(1);
 		updatePayNoticeStatus.setNotify_at(new Date());
 		paymentReckoningService.updateByReckoningId(updatePayNoticeStatus);
 		
		System.out.println(aa.getOrder_id());
		System.out.println(one2);
	}*/

//	@Test
//	public void test005GetTokenByTid() {
////		String one1 = paymentReckoningService.getTokenByReckoningId("test1462867245269dint");
////		System.out.println(one1);
////
////		String one2 = paymentReckoningService.getTokenByReckoningId("bucunzai");
////		System.out.println(one2);
//	}
//
//	@Test
//	public void test006CountOfToday() {
//		int c = paymentReckoningService.countOfToday();
//		System.out.println(c);
//	}

//	@Test
//	public void test007UpdateByTid() {
//		PaymentReckoning order = new PaymentReckoning();
//		order.setReckoning_id("test1462868226517kokb");;
//		order.setThird_party_code("ssssssssss");
//		order.setPay_status(1);
//		order.setNotify_status(1);
//		order.setNotify_at(new Date());
//		paymentReckoningService.updateByReckoningId(order);
//	}

//	@Test
//	public void test008IsPay() {
//		boolean one1 = paymentReckoningService.isPay("test1462868226517kokb");
//		System.out.println(one1);
//
//		boolean one2 = paymentReckoningService.isPay("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test009IsExistByGid() {
//		boolean one1 = paymentReckoningService.isExistByOrderId("vzjkbivlqn");
//		System.out.println(one1);
//
//		boolean one2 = paymentReckoningService.isExistByOrderId("bucunzai");
//		System.out.println(one2);
//	}
}
