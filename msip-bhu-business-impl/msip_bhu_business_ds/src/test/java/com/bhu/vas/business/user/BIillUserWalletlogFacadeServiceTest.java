package com.bhu.vas.business.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentChannelSatDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentInfoDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentInfoDetailDTO;
import com.bhu.vas.api.helper.BusinessEnumType.PaymentThirdType;
import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.api.rpc.payment.vto.PaymentChannelStatVTO;
import com.bhu.vas.api.vto.bill.BillDayVTO;
import com.bhu.vas.api.vto.bill.BillVTO;
import com.bhu.vas.business.ds.statistics.service.UserIncomeMonthRankService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeRankService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.DistributorWalletLogService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;

public class BIillUserWalletlogFacadeServiceTest extends BaseTest{
	@Resource
	private DistributorWalletLogService distributorWalletLogService;
	@Resource
	UserWalletLogService userWalletLogService;
	@Resource
	UserIncomeService userIncomeService;
	@Resource
	UserWalletFacadeService userWalletFacadeService;
	@Resource
	UserIncomeRankService userIncomeRankService;
	@Resource
	UserIncomeMonthRankService userIncomeMonthRankService;
	
	@Test
	public void test() {
		String startTime ="";
		String endTime ="";
		if(StringUtils.isBlank(startTime)){
			Date dayOfMonth = DateTimeHelper.getFirstDateOfCurrentMonth();
			SimpleDateFormat sdf =DateTimeHelper.longDateFormat;
			startTime = sdf.format(dayOfMonth);
		}
		if(StringUtils.isBlank(endTime)){
			endTime = DateTimeHelper.getDateTime(DateTimeHelper.DefalutFormatPattern);
		}
		
		//获取该时间段内公司收益
		Map<String,String> bhuIncomeMap = new HashMap<String,String>();
		List<Map<String,Object>> bhuIncomeList = distributorWalletLogService.queryPlanInfo(startTime,endTime);
		if(bhuIncomeList != null){
			for (int i = 0; i < bhuIncomeList.size(); i++) {
				Map<String,Object> paltformInfoVTO = bhuIncomeList.get(i);
				String income = paltformInfoVTO.get("income")+"";
				String time = paltformInfoVTO.get("time")+"";
				bhuIncomeMap.put(time, income);
				System.out.println("bhuIncome = " + income + ", time = " + time);  
			}
		}
		
		//获取该时间段内用户收益
		Map<String,String> userIncomeMap = new HashMap<String,String>();
		List<Object> userIncomeList = userWalletLogService.userAccountIncome(startTime,endTime);
		if(userIncomeList != null){
			for (Object object : userIncomeList) {
				UserIncome userIncome = (UserIncome) object;
				userIncomeMap.put(userIncome.getTime(), userIncome.getIncome());
				System.out.println("userIncome = " + userIncome.getIncome() + ", time = " + userIncome.getTime());
			}
		}
		
		//获取该时间段内平台收益
		String param = "startTime="+startTime+"&endTime="+endTime;
		Object response = sendPost("http://upay.bhuwifi.com/bhu_pay_api/v1/msip_bhu_payment_rest/channelStat/info", param);
		ResponsePaymentChannelSatDTO ss = JsonHelper.getDTO(response+"", ResponsePaymentChannelSatDTO.class);
		List<PaymentChannelStatVTO>  paymentChannelList = ss.getResult();
			
		BillVTO bill = new BillVTO();
		bill.setStartTime(startTime);
		bill.setEndTtime(endTime);
		
		List<BillDayVTO> billDayList = new ArrayList<BillDayVTO>();
		long totalA = 0l;
		long totalBHUA =0l;
		long totalUserA =0l;
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
		    	String dayTotalBHUA = "0";
		    	String dayTotalUserA = "0";
		    	String dayTotalA = "0";
		    	String userIcomeStr = userIncomeMap.get(dateT);
		    	if(StringUtils.isNotBlank(userIcomeStr)){
		    		dayTotalUserA = userIcomeStr;
		    	}
		    	
		    	String bhuIcomeStr = bhuIncomeMap.get(dateT);
		    	if(StringUtils.isNotBlank(bhuIcomeStr)){
		    		dayTotalBHUA = bhuIcomeStr;
		    	}
		    	dayTotalA = Long.parseLong(bhuIcomeStr)+Long.parseLong(userIcomeStr)+"";
		    	bhuIncomeMap.get(dateT);
		    	billDay.setTotalBHUA(dayTotalBHUA);
		    	billDay.setTotalUserA(dayTotalUserA);
		    	billDay.setTotalA(dayTotalA);
		    	billDayList.add(billDay);
		    	
		    	totalBHUA +=Long.parseLong(bhuIcomeStr);
		    	totalA += Long.parseLong(dayTotalA);
		    	totalUserA += Long.parseLong(userIcomeStr);
		    	bill.setBillDay(billDayList);
			}
		}
		bill.setAmountC(totalBHUA+"");
		bill.setAmountT(totalA+"");
		bill.setAmountU(totalUserA+"");
		System.out.println("++user bill result+++"+JsonHelper.getJSONString(bill));
		
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
			conn.setConnectTimeout(9000);  
			conn.setReadTimeout(10000);
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
