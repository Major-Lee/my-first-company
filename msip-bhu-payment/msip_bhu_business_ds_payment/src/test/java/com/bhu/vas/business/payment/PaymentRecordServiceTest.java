package com.bhu.vas.business.payment;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.payment.model.PaymentRecord;
import com.bhu.vas.business.ds.payment.service.PaymentRecordService;
import com.bhu.vas.business.payment.help.BusinessHelper;
import com.smartwork.msip.localunit.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentRecordServiceTest extends BaseTest {
	@Resource
	PaymentRecordService paymentRecordService;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	@Test
	public void test001BatchCreateOrder() {
		for (int i = 1; i < 2; i++) {
			PaymentRecord record = new PaymentRecord();
			String tId = "O"+BusinessHelper.getMillis();
			record.setAmount(22);
			record.setId("45678");
			record.setCount(11);
			record.setDx_count(233333333);
			record.setVideo_count(233333333);
			record.setInfo("test");
			record.setUpdated_at(new Date());
			paymentRecordService.insert(record);
		}
	}

//	@Test
//	public void test002FindByTid() {
//		PaymentOrder one1 = paymentOrderService.findByTid("iswianwjps");
//		System.out.println(one1.getToken());
//
//		PaymentOrder one2 = paymentOrderService.findByTid("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test003FindByGid() {
//		PaymentOrder one1 = paymentOrderService.findByGid("dvjookdrra");
//		System.out.println(one1.getToken());
//
//		PaymentOrder one2 = paymentOrderService.findByGid("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test004GetIdByTid() {
//		Long one1 = paymentOrderService.getIdByTid("iswianwjps");
//		System.out.println(one1);
//
//		Long one2 = paymentOrderService.getIdByTid("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test005GetTokenByTid() {
//		String one1 = paymentOrderService.getTokenByTid("iswianwjps");
//		System.out.println(one1);
//
//		String one2 = paymentOrderService.getTokenByTid("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test006CountOfToday() {
//		int c = paymentOrderService.countOfToday();
//		System.out.println(c);
//	}
//
//	@Test
//	public void test007UpdateByTid() {
//		PaymentOrder order = new PaymentOrder();
//		order.setTid("iswianwjps");
//		order.setBillno("ssssssssss");
//		order.setOthers("aaaaaaaaaaa");
//		order.setPay_status(1);
//		order.setNotify_status(1);
//		paymentOrderService.updateByTid(order);
//	}
//
//	@Test
//	public void test008IsPay() {
//		boolean one1 = paymentOrderService.isPay("iswianwjps");
//		System.out.println(one1);
//
//		boolean one2 = paymentOrderService.isPay("bucunzai");
//		System.out.println(one2);
//	}
//
//	@Test
//	public void test009IsExistByGid() {
//		boolean one1 = paymentOrderService.isExistByGid("dvjookdrra");
//		System.out.println(one1);
//
//		boolean one2 = paymentOrderService.isExistByGid("bucunzai");
//		System.out.println(one2);
//	}
//	@Test
//	public void test011DoSharedealDailyUserDailySummary(){
//		String cdate = DateTimeHelper.formatDate(new Date(), DateTimeHelper.FormatPattern5);
//		 paymentRecordService.paymentRecordInfo();
//    	System.out.println("dddd:");
//   	}
}
