package com.bhu.vas.business.payment;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.payment.model.PaymentWithdraw;
import com.bhu.vas.business.ds.payment.service.PaymentWithdrawService;
import com.smartwork.msip.localunit.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentWithdrawServiceTest extends BaseTest {
	@Resource
	PaymentWithdrawService paymentWithdrawService;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

//	@Test
//	public void test001BatchCreateWithdraw() {
//		for (int i = 1; i < 2; i++) {
//			PaymentWithdraw Withdraw = new PaymentWithdraw();
//			Withdraw.setId(RandomPicker.randString(letters, 10));
//			Withdraw.setOrderId(RandomPicker.randString(letters, 10));
//			Withdraw.setWithdrawType("alipay");
//			Withdraw.setUserId(RandomPicker.randString(letters, 10));
//			Withdraw.setUserName(RandomPicker.randString(letters, 10));
//			Withdraw.setAmount(10);
//			Withdraw.setSubject("必虎提现");
//			Withdraw.setExterInvokeIp("213.42.3.24");
//			Withdraw.setAppid("1000");
//			paymentWithdrawService.insert(Withdraw);
//		}
//	}

//	@Test
//	public void test002FindByTid() {
//		PaymentWithdraw one1 = paymentWithdrawService.findByTid("wilazjzytq");
//		System.out.println(one1.getUserId());
//
//		PaymentWithdraw one2 = paymentWithdrawService.findByTid("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test003FindByWid() {
//		PaymentWithdraw one1 = paymentWithdrawService.findByWid("dguxtjbyaq");
//		System.out.println(one1.getUserId());
//
//		PaymentWithdraw one2 = paymentWithdrawService.findByWid("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test004GetIdByTid() {
//		PaymentWithdraw one1 = paymentWithdrawService.getById("wilazjzytq");
//		System.out.println(one1.getId());
//
//		PaymentWithdraw one2 = paymentWithdrawService.getById("bucunzai");
//		System.out.println(one2.getId());
//	}

//	@Test
//	public void test006CountOfToday() {
//		int c = paymentWithdrawService.countOfTodayAlipay();
//		System.out.println(c);
//	}

	@Test
	public void test007UpdateByTid() {
		PaymentWithdraw order = new PaymentWithdraw();
		order.setId("WDWX1463456447185reqb");
		order.setThirdPartCode("ssssssssss");
		order.setFailCause("aaaaaaaaaaa");
		order.setWithdrawStatus(1);
		order.setNotifyStatus(1);
		order.setWithdrawAt(new Date());
		order.setNotifiedAt(new Date());
		paymentWithdrawService.update(order);
	}

//	@Test
//	public void test008IsWithdraw() {
//		boolean one1 = paymentWithdrawService.isWithdraw("wilazjzytq");
//		System.out.println(one1);
//
//		boolean one2 = paymentWithdrawService.isWithdraw("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test009IsExistByWid() {
//		boolean one1 = paymentWithdrawService.isExistByWid("dguxtjbyaq");
//		System.out.println(one1);
//
//		boolean one2 = paymentWithdrawService.isExistByWid("bucunzai");
//		System.out.println(one2);
//	}

}
