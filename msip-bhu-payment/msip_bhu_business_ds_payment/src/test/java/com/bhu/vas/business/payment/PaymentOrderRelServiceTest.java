package com.bhu.vas.business.payment;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.payment.model.PaymentOrderRel;
import com.bhu.vas.business.ds.payment.service.PaymentOrderRelService;
import com.bhu.vas.business.payment.help.BusinessHelper;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomPicker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentOrderRelServiceTest extends BaseTest {
	@Resource
	PaymentOrderRelService paymentOrderRelService;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	@Test
	public void test001BatchCreateOrder() {
		for (int i = 1; i < 2; i++) {
			PaymentOrderRel order = new PaymentOrderRel();
			String reckoningId = BusinessHelper.generatePaymentReckoningNoByType("test");
			order.setId("test1473152942076cznl");
			order.setOrder_id(RandomPicker.randString(letters, 10));
			order.setAmount(Integer.parseInt(BusinessHelper.getMoney(0.1+"")));
			order.setPayment_type("WapWeixin");
			order.setSubject("打赏");
			order.setAppid("1000");
			order.setPay_status(1);
			order.setChannel_no(RandomPicker.randString(letters, 10));
			order.setChannel_type("Midas");
			order.setThird_party_code(RandomPicker.randString(letters, 10));
			paymentOrderRelService.insert(order);
		}
	}
	
//	@Test
//	public void test002update() {
//		PaymentReckoning updatePayStatus = paymentReckoningService.getById("PROPCWX1466992205468nkwk");
//		updatePayStatus.setThird_party_code("11");
//		updatePayStatus.setPay_status(1);
//		updatePayStatus.setPaid_at(new Date());
//			updatePayStatus.setRemark("ssss");
//			paymentOrderRelService.update(updatePayStatus);
//	}

}
