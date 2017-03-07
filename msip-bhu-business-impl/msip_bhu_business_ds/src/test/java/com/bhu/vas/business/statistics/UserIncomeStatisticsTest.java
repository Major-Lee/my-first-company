package com.bhu.vas.business.statistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentChannelSatDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentInfoDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentInfoDetailDTO;
import com.bhu.vas.api.helper.BusinessEnumType.PaymentThirdType;
import com.bhu.vas.api.rpc.charging.model.StatisticFincialIncome;
import com.bhu.vas.api.rpc.payment.vto.PaymentChannelStatVTO;
import com.bhu.vas.api.vto.bill.BillDayVTO;
import com.bhu.vas.api.vto.bill.BillVTO;
import com.bhu.vas.business.ds.statistics.service.StatisticFincialIncomeService;
import com.bhu.vas.business.ds.statistics.service.StatisticFincialMonthService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;

/**
 * Created by bluesand on 4/28/15.
 */
public class UserIncomeStatisticsTest extends BaseTest {

	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private StatisticFincialMonthService statisticFincialMonthService;
	@Resource
	private StatisticFincialIncomeService statisticFincialIncomeService;



    @Test
    public void find() {
//        UserBrandStatistics userBrandStatistics = userBrandStatisticsService.getById("2015-05-23");
//
//        System.out.println(userBrandStatistics.getExtension_content());
//
//        System.out.println(userBrandStatistics.getInnerModels());
////        List<UserBrandDTO> userBrandStatisticsDTOs  =
////                JsonHelper.getDTOList(userBrandStatistics.getExtension_content(),UserBrandDTO.class);
//
//        //System.out.println(userBrandStatisticsDTOs);

//    	Date date = new Date();  
//		Calendar calendar = Calendar.getInstance();  
//		calendar.setTime(date);  
//		calendar.add(Calendar.MONTH, 0-1);  
//		date = calendar.getTime();  
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  
//		String time =sdf.format(date);
//		System.out.println(time);
//		
//		//StatisticFincialMonth
//		UserBillVTO userbill = new UserBillVTO();
//		userbill.setStartTime("2016-12");
//		userbill.setEndTtime("2016-12");
//		
//		double totalBeginIncome = 0; //期初
//		double totalEndIncome = 0;; //期末
//		double totalMonthCount = 0;; //交易数
//		double totalMonthIncome = 0;; //交易
//		double totalWithdrawPast = 0;; //往期已提现
//		double totalWithdrawApply = 0;; //本次提现数
//		double totalCash = 0;; //钱包余额
//		double totalBalance = 0;; //误差
//		
//    	List<UserBillMonthVTO> billMonth = new ArrayList<UserBillMonthVTO>();
//    	
//    	
//		List<Object> statisticFincialMonth = statisticFincialMonthService.findModelByMonthId("2016-12","2016-12");
//		if(statisticFincialMonth != null){
//			for (Object object : statisticFincialMonth) {
//				System.out.println("statisticFincialMonth size："+statisticFincialMonth.size());
//				StatisticFincialMonth  monthData  =(StatisticFincialMonth)object;
//				UserBillMonthVTO monthBill = new UserBillMonthVTO();
//				monthBill.setDate(monthData.getMonthid());
//				monthBill.setBeginIncome(monthData.getBegin_income());
//				monthBill.setEndIncome(monthData.getEnd_income());
//				monthBill.setCash(monthData.getCash());
//				monthBill.setMonthCount(monthData.getMonth_count());
//				monthBill.setMonthIncome(monthData.getMonth_income());
//				monthBill.setWithdrawApply(monthData.getWithdraw_apply());
//				monthBill.setWithdrawPast(monthData.getWithdraw_past());
//				//误差= 期末总收益-往期已提金额-本期申请金额-钱包剩余
//				double balance = Double.valueOf(monthData.getEnd_income())-
//						Double.valueOf(monthData.getWithdraw_past())-
//						Double.valueOf(monthData.getWithdraw_apply())-
//						Double.valueOf(monthData.getCash());
//				monthBill.setBalance(balance+"");
//		    	billMonth.add(monthBill);
//		    	
//		    	totalBeginIncome += Double.valueOf(monthData.getBegin_income());
//		    	totalEndIncome += Double.valueOf(monthData.getEnd_income());
//		    	totalMonthCount += Double.valueOf(monthData.getMonth_count());
//		    	totalMonthIncome += Double.valueOf(monthData.getMonth_income());
//		    	totalWithdrawPast += Double.valueOf(monthData.getWithdraw_past());
//		    	totalWithdrawApply += Double.valueOf(monthData.getWithdraw_apply());
//		    	totalCash += Double.valueOf(monthData.getCash());
//		    	totalBalance += balance;
//				
//			}
//		}
//		userbill.setTotalBalance(totalBalance+"");
//		userbill.setTotalEndIncome(totalEndIncome+"");
//		userbill.setTotalBeginIncome(totalBeginIncome+"");
//		userbill.setTotalMonthCount(totalMonthCount+"");
//		userbill.setTotalMonthIncome(totalMonthIncome+"");
//		userbill.setTotalWithdrawApply(totalWithdrawApply+"");
//		userbill.setTotalWithdrawPast(totalWithdrawPast+"");
//		userbill.setTotalCash(totalCash+"");
//		userbill.setMonthBill(billMonth);
//		
//		System.out.println("++user bill result+++"+JsonHelper.getJSONString(userbill));
		
    	String startTime = "2016-12-01";
    	String endTime = "2016-12-02";
		//StatisticFincialIncome
    	Map<String ,Object> bhuIncomeMap = new HashMap<String ,Object>();
		List<Object> statisticFincialIncome = statisticFincialIncomeService.findFincialIncomeByTime(startTime,endTime);
		if(statisticFincialIncome != null){
			for (Object object : statisticFincialIncome) {
				StatisticFincialIncome  dd  =(StatisticFincialIncome)object;
				System.out.println("statisticFincialIncome："+dd.getDayid());
				bhuIncomeMap.put(dd.getDayid(), dd);
			}
		}
		BillVTO bill = new BillVTO();
		bill.setStartTime(startTime);
		bill.setEndTtime(endTime);
		
		//获取该时间段内平台收益
		String payDomain = null;//BusinessRuntimeConfiguration.PaymentJavaApiDomain;
		if(payDomain == null){
			payDomain = "http://upay.bhuwifi.com";
		}
		long paltformIncomeBegin = System.currentTimeMillis(); // 这段代码放在程序执行前
		String param = "startTime="+startTime+"&endTime="+endTime;
		Object response = sendPost(payDomain+"/bhu_pay_api/v1/msip_bhu_payment_rest/channelStat/info", param);
		long paltformIncomeEnd = System.currentTimeMillis(); // 这段代码放在程序执行前
		System.out.println("paltformIncome elapsed" +(paltformIncomeEnd - paltformIncomeBegin));
		ResponsePaymentChannelSatDTO ss = JsonHelper.getDTO(response+"", ResponsePaymentChannelSatDTO.class);
		List<PaymentChannelStatVTO>  paymentChannelList = ss.getResult();
		
		List<BillDayVTO> billDayList = new ArrayList<BillDayVTO>();
		DecimalFormat df  = new DecimalFormat("#########0.00");
		double totalA = 0;
		double totalBHUA =0;
		double totalUserA =0;
		if (paymentChannelList != null) {
			for (PaymentChannelStatVTO paymentChannelStatVTO : paymentChannelList) {
				System.out.println( paymentChannelStatVTO.getTimeD()+"info:"+paymentChannelStatVTO.getInfo());
				String info =paymentChannelStatVTO.getInfo();
				ResponsePaymentInfoDTO paymentInfo = JsonHelper.getDTO(info, ResponsePaymentInfoDTO.class);
				ResponsePaymentInfoDetailDTO hee =  paymentInfo.getHee();
				ResponsePaymentInfoDetailDTO now =  paymentInfo.getNow();
				ResponsePaymentInfoDetailDTO paypal =  paymentInfo.getPaypal();
				ResponsePaymentInfoDetailDTO wifiManage =  paymentInfo.getWifiManage();
				ResponsePaymentInfoDetailDTO wifiHelper =  paymentInfo.getWifiHelper();
				ResponsePaymentInfoDetailDTO weixin =  paymentInfo.getWeixin();
				ResponsePaymentInfoDetailDTO alipay =  paymentInfo.getAlipay();
				String dateT = paymentChannelStatVTO.getTimeD();
				BillDayVTO billDay = new BillDayVTO();
		    	billDay.setDate(dateT);
		    	billDay.setAilpayA(alipay.getAmount()+"");
		    	billDay.setAilpayN(PaymentThirdType.ALIPAY.getName_zh());
		    	billDay.setHeeA(hee.getAmount()+"");
		    	billDay.setHeeN(PaymentThirdType.HEE.getName_zh());
		    	billDay.setPaypalA(paypal.getAmount()+"");
		    	billDay.setPaypalN(PaymentThirdType.PAYPAL.getName_zh());
		    	billDay.setWifiManageN(PaymentThirdType.WIFIMANAGE.getName_zh());
		    	billDay.setWifiManageA(wifiManage.getAmount()+"");
		    	billDay.setWeixinA(weixin.getAmount()+"");
		    	billDay.setWeixinN(PaymentThirdType.WEIXIN.getName_zh());
		    	billDay.setWifiHelperA(wifiHelper.getAmount()+"");
		    	billDay.setWifiHelperN(PaymentThirdType.WIFIHELPER.getName_zh());
		    	billDay.setNowA(now.getAmount()+"");
		    	billDay.setNowN(PaymentThirdType.NOW.getName_zh());
		    	StatisticFincialIncome fincialIncome =(StatisticFincialIncome)bhuIncomeMap.get(dateT);
		    	if(fincialIncome != null){
		    		String dayTotalBHUStr = fincialIncome.getBhu_income();
		    		String dayTotalUserStr = fincialIncome.getUser_income();
		    		String dayTotalStr = fincialIncome.getTotal_income();
		    		billDay.setTotalBHUA(dayTotalBHUStr);
			    	billDay.setTotalUserA(dayTotalUserStr);
			    	billDay.setTotalA(dayTotalStr);
			    	
			    	totalA += Double.valueOf(dayTotalStr);
			    	totalUserA += Double.valueOf(dayTotalUserStr);
			    	totalBHUA += Double.valueOf(dayTotalBHUStr);
		    	}else{
		    		billDay.setTotalBHUA("0");
			    	billDay.setTotalUserA("0");
			    	billDay.setTotalA("0");
		    	}
		    	billDayList.add(billDay);
		    	
			}
		}
		bill.setBillDay(billDayList);
		bill.setAmountC(totalBHUA+"");
		bill.setAmountT(totalA+"");
		bill.setAmountU(totalUserA+"");
		System.out.println("++user bill result+++"+JsonHelper.getJSONString(bill));
//		StatisticFincialIncome statisticFincialIncome = statisticFincialIncomeService.getById(78);
//		System.out.println("statisticFincialIncome："+statisticFincialIncome.getDayid());
		
		
		
//		UserIncome userIncome = userIncomeService.getById("219");
//		System.out.println(userIncome.getIncome());
//		List<UserIncome> userIncomList=userIncomeService.findMonthList(time+"%");
//		System.out.println("userIncomList size:"+userIncomList.size());
//		if(userIncomList.size()<=0){
//			
//			System.out.println(userIncomList.size()+"statring");
//		}else{
//			
//			System.out.println(userIncomList.size());
//		}


    }

	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Accept-Encoding", "utf-8");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setConnectTimeout(400);  
			conn.setReadTimeout(700);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
			}
		}
		return result;
	}
}
