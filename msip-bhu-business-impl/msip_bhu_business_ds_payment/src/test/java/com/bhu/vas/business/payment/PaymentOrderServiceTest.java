package com.bhu.vas.business.payment;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.payment.model.PaymentOrder;
import com.bhu.vas.business.ds.payment.service.PaymentOrderService;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentOrderServiceTest extends BaseTest {
	@Resource
	PaymentOrderService paymentOrderService;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	@Test
	public void test001BatchCreateOrder() {
		for (int i = 1; i < 2; i++) {
			PaymentOrder order = new PaymentOrder();
			order.setTid(RandomPicker.randString(letters, 10));
			order.setGid(RandomPicker.randString(letters, 10));
			order.setPrice(RandomData.floatNumber(1, 2));
			order.setNum(1);
			order.setPayment_type("Midas");
			order.setOpenid("BHUUSERMAC000000000000");
			order.setSubject("打赏认证");
			order.setExter_invoke_ip("213.42.3.24");
			order.setAppid("1000");
			order.setToken(RandomPicker.randString(letters, 10));
			paymentOrderService.insert(order);
		}
	}

	@Test
	public void test002FindByTid() {
		PaymentOrder one1 = paymentOrderService.findByTid("iswianwjps");
		System.out.println(one1.getToken());

		PaymentOrder one2 = paymentOrderService.findByTid("bucunzai");
		System.out.println(one2);
	}

	@Test
	public void test003FindByGid() {
		PaymentOrder one1 = paymentOrderService.findByGid("dvjookdrra");
		System.out.println(one1.getToken());

		PaymentOrder one2 = paymentOrderService.findByGid("bucunzai");
		System.out.println(one2);
	}

	@Test
	public void test004GetIdByTid() {
		Long one1 = paymentOrderService.getIdByTid("iswianwjps");
		System.out.println(one1);

		Long one2 = paymentOrderService.getIdByTid("bucunzai");
		System.out.println(one2);
	}

	@Test
	public void test005GetTokenByTid() {
		String one1 = paymentOrderService.getTokenByTid("iswianwjps");
		System.out.println(one1);

		String one2 = paymentOrderService.getTokenByTid("bucunzai");
		System.out.println(one2);
	}

	@Test
	public void test006CountOfToday() {
		int c = paymentOrderService.countOfToday();
		System.out.println(c);
	}

	@Test
	public void test007UpdateByTid() {
		PaymentOrder order = new PaymentOrder();
		order.setTid("iswianwjps");
		order.setBillno("ssssssssss");
		order.setOthers("aaaaaaaaaaa");
		order.setPay_status(1);
		order.setNotify_status(1);
		paymentOrderService.updateByTid(order);
	}

	@Test
	public void test008IsPay() {
		boolean one1 = paymentOrderService.isPay("iswianwjps");
		System.out.println(one1);

		boolean one2 = paymentOrderService.isPay("bucunzai");
		System.out.println(one2);
	}

	@Test
	public void test009IsExistByGid() {
		boolean one1 = paymentOrderService.isExistByGid("dvjookdrra");
		System.out.println(one1);

		boolean one2 = paymentOrderService.isExistByGid("bucunzai");
		System.out.println(one2);
	}
}
