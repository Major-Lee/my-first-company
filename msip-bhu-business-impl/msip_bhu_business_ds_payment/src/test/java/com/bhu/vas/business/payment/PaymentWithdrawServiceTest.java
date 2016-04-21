package com.bhu.vas.business.payment;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.payment.model.PaymentWithdraw;
import com.bhu.vas.api.rpc.payment.model.PaymentWithdraw;
import com.bhu.vas.business.ds.payment.service.PaymentWithdrawService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentWithdrawServiceTest extends BaseTest {
	@Resource
	PaymentWithdrawService paymentWithdrawService;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	@Test
	public void test001BatchCreateWithdraw() {
		for (int i = 1; i < 2; i++) {
			PaymentWithdraw Withdraw = new PaymentWithdraw();
			Withdraw.setTid(RandomPicker.randString(letters, 10));
			Withdraw.setWid(RandomPicker.randString(letters, 10));
			Withdraw.setWithdraw_type("alipay");
			Withdraw.setUser_id(RandomPicker.randString(letters, 10));
			Withdraw.setUser_name(RandomPicker.randString(letters, 10));
			Withdraw.setPrice(RandomData.floatNumber(1, 2));
			Withdraw.setSubject("打赏认证");
			Withdraw.setExter_invoke_ip("213.42.3.24");
			Withdraw.setAppid("1000");
			paymentWithdrawService.insert(Withdraw);
		}
	}

	@Test
	public void test002FindByTid() {
		PaymentWithdraw one1 = paymentWithdrawService.findByTid("wilazjzytq");
		System.out.println(one1.getUser_id());

		PaymentWithdraw one2 = paymentWithdrawService.findByTid("bucunzai");
		System.out.println(one2);
	}

	@Test
	public void test003FindByWid() {
		PaymentWithdraw one1 = paymentWithdrawService.findByWid("dguxtjbyaq");
		System.out.println(one1.getUser_id());

		PaymentWithdraw one2 = paymentWithdrawService.findByWid("bucunzai");
		System.out.println(one2);
	}

	@Test
	public void test004GetIdByTid() {
		Long one1 = paymentWithdrawService.getIdByTid("wilazjzytq");
		System.out.println(one1);

		Long one2 = paymentWithdrawService.getIdByTid("bucunzai");
		System.out.println(one2);
	}

	@Test
	public void test006CountOfToday() {
		int c = paymentWithdrawService.countOfTodayAlipay();
		System.out.println(c);
	}

	@Test
	public void test007UpdateByTid() {
		PaymentWithdraw order = new PaymentWithdraw();
		order.setTid("wilazjzytq");
		order.setBillno("ssssssssss");
		order.setFail_cause("aaaaaaaaaaa");
		order.setWithdraw_status(1);
		order.setNotify_status(1);
		paymentWithdrawService.updateByTid(order);
	}

	@Test
	public void test008IsWithdraw() {
		boolean one1 = paymentWithdrawService.isWithdraw("wilazjzytq");
		System.out.println(one1);

		boolean one2 = paymentWithdrawService.isWithdraw("bucunzai");
		System.out.println(one2);
	}

	@Test
	public void test009IsExistByWid() {
		boolean one1 = paymentWithdrawService.isExistByWid("dguxtjbyaq");
		System.out.println(one1);

		boolean one2 = paymentWithdrawService.isExistByWid("bucunzai");
		System.out.println(one2);
	}

}
