package com.bhu.vas.business.payment;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.payment.model.PaymentOutimeErrorLog;
import com.bhu.vas.business.ds.payment.service.PaymentOutimeErrorLogService;
import com.bhu.vas.business.payment.help.BusinessHelper;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomPicker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentOutimeErrorLogServiceTest extends BaseTest {
	@Resource
	PaymentOutimeErrorLogService paymentOutimeErrorLog;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	@Test
	public void test001BatchCreateOrder() {
		for (int i = 1; i < 2; i++) {
			PaymentOutimeErrorLog order = new PaymentOutimeErrorLog();
			String reckoningId = BusinessHelper.generatePaymentReckoningNoByType("test");
			order.setId(reckoningId);
			order.setOrder_id(RandomPicker.randString(letters, 10));
			order.setOt(222);
			order.setC_type("WapNativeWeixin");
			paymentOutimeErrorLog.insert(order);
		}
	}
	
//	@Test
//	public void test002update() {
//		PaymentReckoning updatePayStatus = paymentReckoningService.getById("PROPCWX1466992205468nkwk");
//		updatePayStatus.setThird_party_code("11");
//		updatePayStatus.setPay_status(1);
//		updatePayStatus.setPaid_at(new Date());
//			updatePayStatus.setRemark("ssss");
// 		paymentReckoningService.update(updatePayStatus);
//	}
}
