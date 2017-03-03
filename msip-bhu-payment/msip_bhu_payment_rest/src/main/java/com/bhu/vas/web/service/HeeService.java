package com.bhu.vas.web.service;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.rpc.payment.model.PaymentAlipaylocation;
import com.bhu.vas.api.rpc.payment.model.PaymentOutimeErrorLog;
import com.bhu.vas.api.vto.payment.PaymentTypeVTO;
import com.bhu.vas.business.ds.payment.service.PaymentAlipaylocationService;
import com.bhu.vas.business.ds.payment.service.PaymentOutimeErrorLogService;
import com.bhu.vas.business.ds.payment.service.PaymentParameterService;
import com.bhu.vas.business.helper.BusinessHelper;
import com.bhu.vas.business.helper.PaymentChannelCode;
import com.bhu.vas.business.qqmail.SendMailHelper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.heepay.api.Heepay;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;

/**
 * Weixin支付
 * @author Pengyu
 *
 */
@Service
public class HeeService{
	private final Logger logger = LoggerFactory.getLogger(HeeService.class);

    private int heeWarningCount;
	private String heeWarningTime = "";
	private String heeErrorTime = "";
	
	@Autowired
	PayLogicService payLogicService;
	@Autowired
    PayHttpService payHttpService;
	@Resource
	PaymentAlipaylocationService paymentAlipaylocationService;
	@Resource
	PaymentOutimeErrorLogService paymentOutimeErrorLogService;
	@Resource
	PaymentParameterService paymentParameterService;


    
    /**
     * 处理微信扫码支付请求
     * @param request
     * @param response
     * @param totalPrice
     * @param goodsNo
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
	public PaymentTypeVTO doHee(HttpServletResponse response, String type,String total_fee, String out_trade_no,String ip,String locationUrl,String usermac,String paymentName,String appid) {
    	PaymentTypeVTO result = new PaymentTypeVTO();
    	if(ip == "" || ip == null){
    		ip = "213.42.3.24";
    	}
    	//服务器异步通知页面路径
		String notify_url = PayHttpService.HEE_NOTIFY_URL;
		//打赏页面跳转同步通知页面路径
		String return_url = PayHttpService.HEE_RETURN_URL;
    	String total_fee_fen = BusinessHelper.getMoney(total_fee);
//    	int temp = Integer.parseInt(total_fee_fen);
//    	if(temp < 50){
//    		total_fee = "0.50";
//    		total_fee_fen = BusinessHelper.getMoney(total_fee);
//    	}
    	//判断是否是充值业务
		if(CommdityApplication.BHU_PREPAID_BUSINESS.getKey().equals(Integer.parseInt(appid))){
			return_url = PayHttpService.HEE_PREPAID_RETURN_URL;
		}
    	String reckoningId = payLogicService.createPaymentId(out_trade_no,"Hee",total_fee_fen,ip,PaymentChannelCode.BHU_WAP_WEIXIN.i18n(),usermac,paymentName,appid,"");
    	if (!StringUtils.isBlank(locationUrl)) {
    		logger.info(String.format("get heepay locationUrl [%s] ",locationUrl));
    		PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
    		orderLocation.setTid(reckoningId);
    		orderLocation.setLocation(locationUrl);
    		long insert_hee_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
    		paymentAlipaylocationService.insert(orderLocation);
    		long insert_hee_end = System.currentTimeMillis() - insert_hee_begin; // 这段代码放在程序执行后
    		logger.info(out_trade_no+"汇元url入库耗时：" + insert_hee_end + "毫秒");
    		logger.info(String.format("apply heepay set reckoningId [%s] location url [%s] insert finished.",reckoningId,locationUrl));
    	}
    	long requset_hee_url_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
    	String url = Heepay.order(reckoningId, total_fee, ip,notify_url,return_url);
    	long requset_hee_url_end = System.currentTimeMillis() - requset_hee_url_begin; // 这段代码放在程序执行后
		logger.info(out_trade_no+"请求汇元获取url耗时：" + requset_hee_url_end + "毫秒");
		int nowOutTimeLevel = payHttpService.getOt();
		try{
						
			if(requset_hee_url_end > nowOutTimeLevel){
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "汇元",requset_hee_url_end+"");
				if(requset_hee_url_end > 7000){
					heeWarningCount++;
				}
				if(requset_hee_url_end > 30000){
					String sendMsg = payLogicService.updatePaymentParam(3,smsg,heeErrorTime);
					if(!StringUtils.isBlank(sendMsg)){
						heeErrorTime =sendMsg;
					}
				}	
				PaymentOutimeErrorLog  paymentNowpayErrorLog = new PaymentOutimeErrorLog();
				paymentNowpayErrorLog.setId(reckoningId);
				paymentNowpayErrorLog.setOrder_id(out_trade_no);
				paymentNowpayErrorLog.setOt(new Long(requset_hee_url_end).intValue());
				paymentNowpayErrorLog.setC_type("Hee");
				paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
			}
			
			switch (heeWarningCount) {
			case 3:
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "汇元",requset_hee_url_end+"");
				String resp = "ture";
				String acc = PayHttpService.Internal_level2_warning_man;
				String curDate = (BusinessHelper.getTimestamp()+"").substring(0, 10);
				if(!heeWarningTime.equals(curDate)){
					heeWarningTime = curDate;
					SendMailHelper.doSendMail(2,smsg);
					resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
				}
				logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,resp));
				heeWarningCount = 0;
				break;
			default:
				break;
			}
		}catch(Exception e){
			logger.info(String.format("apply midas catch exception [%s]",e.getMessage()+e.getCause()));
		}
		if("error".equalsIgnoreCase(url)){
    		result.setType("FAIL");
        	result.setUrl("支付请求失败");
        	return result;
    	}else{
    		url = url.replace("¬", "&not");
        	result.setType("http");
        	result.setUrl(url);
        	return result;
    	}
    }
   	
   	
}