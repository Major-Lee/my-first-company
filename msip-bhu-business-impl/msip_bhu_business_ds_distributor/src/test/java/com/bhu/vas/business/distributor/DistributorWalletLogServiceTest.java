package com.bhu.vas.business.distributor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.business.ds.distributor.service.DistributorWalletLogService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.localunit.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DistributorWalletLogServiceTest extends BaseTest {
	@Resource
	DistributorWalletLogService distributorWalletLogService;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	@Test
	public void test007UpdateByTid() {
//		DistributorWalletLog order =distributorWalletLogService.getById(1290l);
//		System.out.println(order.getUid());
//		System.out.println(order.getOrderid());
		String startTime = "2017-02-01";
		String endTime = "2017-02-14";
		if(StringUtils.isBlank(startTime)){
			Date dayOfMonth = DateTimeHelper.getFirstDateOfCurrentMonth();
			SimpleDateFormat sdf =DateTimeHelper.longDateFormat;
			startTime = sdf.format(dayOfMonth);
		}
		if(StringUtils.isBlank(endTime)){
			endTime = DateTimeHelper.getDateTime(DateTimeHelper.DefalutFormatPattern);
		}
		List<Map<String,Object>> bhuIncomeList = distributorWalletLogService.queryPlanInfo("2017-01-01",endTime);
		if(bhuIncomeList != null){
			for (int i = 0; i < bhuIncomeList.size(); i++) {
				Map<String,Object> paltformInfoVTO = bhuIncomeList.get(i);
				String income = paltformInfoVTO.get("income")+"";
				String time = paltformInfoVTO.get("time")+"";
				System.out.println("paltformAmount = " + income + ", time = " + time);  
			}
		}
//		//获取该时间段内公司收益
//		Map<String,Object> bhuIncome = distributorWalletLogService.queryPlanInfo(startTime,endTime);
//		if(bhuIncome != null){
//			for (int i = 0; i < bhuIncome.size(); i++) {
//				String bhuAmount = bhuIncome.get("bhuAmount")+"";
//				String time =  bhuIncome.get("time")+"";
//				System.out.println("bhuAmount = " + bhuAmount + ", time = " + time);  
//			}
//		}
	}

}
