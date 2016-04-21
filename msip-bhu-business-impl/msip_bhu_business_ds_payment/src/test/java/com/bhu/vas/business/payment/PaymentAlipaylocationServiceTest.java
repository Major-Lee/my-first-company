package com.bhu.vas.business.payment;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.payment.model.PaymentAlipaylocation;
import com.bhu.vas.business.ds.payment.service.PaymentAlipaylocationService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomPicker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentAlipaylocationServiceTest extends BaseTest {
	@Resource
	PaymentAlipaylocationService paymentAlipaylocationService;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	@Test
	public void test001BatchCreateOrder() {
		for (int i = 1; i < 2; i++) {
			PaymentAlipaylocation order = new PaymentAlipaylocation();
			order.setTid(RandomPicker.randString(letters, 10));
			order.setLocation(RandomPicker.randString(letters, 10));
			paymentAlipaylocationService.insert(order);
		}
	}

	@Test
	public void test002GetLocationByTid() {
		String one = paymentAlipaylocationService
				.getLocationByTid("gvossvdeiw");
		System.out.println(one);
	}

}
