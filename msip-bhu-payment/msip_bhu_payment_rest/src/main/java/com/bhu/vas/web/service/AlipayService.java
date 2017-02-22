package com.bhu.vas.web.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipaySubmit;
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
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;

/**
 * Weixin支付
 * @author Pengyu
 *
 */
@Service
public class AlipayService{
	private final Logger logger = LoggerFactory.getLogger(AlipayService.class);
    
	private int alipayWarningCount;
	private String alipayWarningTime = "";
	private String alipayErrorTime = "";
	
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
	/**
	 *  支付宝支付请求接口
     * @param response
     * @param request
	 * @param totalPrice 支付金额
	 * @param out_trade_no 订单号
	 * @param locationUrl 支付完成后返回页面地址
	 * @param ip 用户Ip
	 * @return
	 */
    public PaymentTypeVTO doAlipay(HttpServletResponse response,HttpServletRequest request,
    		String totalPrice,
    		String out_trade_no,
    		String locationUrl,
    		String ip,
    		String type,
    		String usermac,
    		String paymentName,
    		String appid,
    		String ot){
    	response.setCharacterEncoding("utf-8");
    	PaymentTypeVTO result = new PaymentTypeVTO();
		//服务器异步通知页面路径
		String notify_url = PayHttpService.ALIPAY_NOTIFY_URL;
		//打赏页面跳转同步通知页面路径
		String return_url = PayHttpService.ALIPAY_RETURN_URL;
		//订单名称
		String subject = paymentName;//;new String("打赏".getBytes("ISO-8859-1"), "utf-8");
		//付款金额
		String total_fee = totalPrice;
		//订单描述
		String body = "BHU service";
		Map<String, String> sParaTemp = new HashMap<String, String>();
		String reckoningId = null;
		String total_fee_fen = BusinessHelper.getMoney(total_fee);
		if(CommdityApplication.BHU_PREPAID_BUSINESS.getKey().equals(Integer.parseInt(appid))){
			return_url = PayHttpService.ALIPAY_PREPAID_RETURN_URL;
		}
		PaymentChannelCode payChannel =PaymentChannelCode.getPaymentChannelCodeByCode(type);
		switch (payChannel) {
		case BHU_WAP_ALIPAY:
			reckoningId = payLogicService.createPaymentId(out_trade_no,"0",total_fee_fen,ip,PaymentChannelCode.BHU_WAP_ALIPAY.i18n(),usermac,paymentName,appid);
			sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_id", AlipayConfig.seller_id);
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", AlipayConfig.payment_type);
			sParaTemp.put("notify_url", notify_url);
			sParaTemp.put("return_url", return_url);
			sParaTemp.put("show_url", return_url);
			sParaTemp.put("exter_invoke_ip", ip);
			sParaTemp.put("out_trade_no", reckoningId);
			sParaTemp.put("subject", subject);
			sParaTemp.put("total_fee", total_fee);
			sParaTemp.put("body", body);
			sParaTemp.put("it_b_pay", ot);
			//sParaTemp.put("app_pay", "Y");
			break;
		case BHU_APP_ALIPAY:
			reckoningId = payLogicService.createPaymentId(out_trade_no,"0",total_fee_fen,ip,PaymentChannelCode.BHU_APP_ALIPAY.i18n(),usermac,paymentName,appid);
			sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_id", AlipayConfig.seller_id);
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", AlipayConfig.payment_type);
			sParaTemp.put("notify_url", notify_url);
			sParaTemp.put("return_url", return_url);
			sParaTemp.put("show_url", return_url);
			sParaTemp.put("exter_invoke_ip", ip);
			sParaTemp.put("out_trade_no", reckoningId);
			sParaTemp.put("subject", subject);
			sParaTemp.put("total_fee", total_fee);
			sParaTemp.put("body", body);
			sParaTemp.put("it_b_pay", ot);
			//sParaTemp.put("app_pay", "Y");
			break;
		case BHU_PC_ALIPAY:
			reckoningId = payLogicService.createPaymentId(out_trade_no,"0",total_fee_fen,ip,PaymentChannelCode.BHU_PC_ALIPAY.i18n(),usermac,paymentName,appid);
			sParaTemp.put("service", AlipayConfig.service);
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_id", AlipayConfig.seller_id);
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", AlipayConfig.payment_type);
			sParaTemp.put("notify_url", notify_url);
			sParaTemp.put("return_url", return_url);
			sParaTemp.put("anti_phishing_key", AlipayConfig.anti_phishing_key);
			sParaTemp.put("exter_invoke_ip", ip);
			sParaTemp.put("out_trade_no", reckoningId);
			sParaTemp.put("subject", subject);
			sParaTemp.put("total_fee", total_fee);
			sParaTemp.put("body", body);
			sParaTemp.put("it_b_pay", ot);
			break;
		default:
			break;
		}
		if (!StringUtils.isBlank(locationUrl)) {
			PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
			orderLocation.setTid(reckoningId);
			orderLocation.setLocation(locationUrl);
			paymentAlipaylocationService.insert(orderLocation);
			logger.info(String.format("apply alipay set location reckoningId [%s] locationUrl [%s] insert finished.",reckoningId, locationUrl));
		}
		String sHtmlText = "";
        try {
        	long request_alipay_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
            sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"post","确认");
    		long request_unifiedorder_end = System.currentTimeMillis() - request_alipay_begin; // 这段代码放在程序执行后
    		logger.info(out_trade_no+"请求支付宝url接口耗时：" + request_unifiedorder_end + "毫秒");
    		int nowOutTimeLevel = payHttpService.getOt();
    		try{
    			if(request_unifiedorder_end > nowOutTimeLevel){
    				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "支付宝支付",request_unifiedorder_end+"");
    				if(request_unifiedorder_end > 7000){
    					alipayWarningCount++;
    				}
    				if(request_unifiedorder_end > 30000){
    					String sendMsg = payLogicService.updatePaymentParam(6,smsg,alipayErrorTime);
    					if(!StringUtils.isBlank(sendMsg)){
    						alipayErrorTime =sendMsg;
    					}
    				}	
    				PaymentOutimeErrorLog  paymentNowpayErrorLog = new PaymentOutimeErrorLog();
    				paymentNowpayErrorLog.setId(reckoningId);
    				paymentNowpayErrorLog.setOrder_id(out_trade_no);
    				paymentNowpayErrorLog.setOt(new Long(request_unifiedorder_end).intValue());
    				paymentNowpayErrorLog.setC_type("Alipay");
    				paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
    			}
    			
    			switch (alipayWarningCount) {
    			case 3:
    				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "支付宝支付",request_unifiedorder_end+"");
    				String resp = "ture";
    				String acc = PayHttpService.Internal_level2_warning_man;
    				String curDate = (BusinessHelper.getTimestamp()+"").substring(0, 10);
    				if(!alipayWarningTime.equals(curDate)){
    					alipayWarningTime = curDate;
    					SendMailHelper.doSendMail(2,smsg);
    					resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
     				}
    				logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,resp));
    				alipayWarningCount = 0;
    				break;
    			default:
    				break;
    			}
    		}catch(Exception e){
    			logger.info(String.format("apply alipay catch exception [%s]",e.getMessage()+e.getCause()));
    		}
            result = new PaymentTypeVTO();
            result.setType("http");
            result.setUrl(sHtmlText);
            return result;
        } catch (Exception e) {
        	result.setType("FAIL");
            result.setUrl("支付请求失败");
            return result;
        }
	}
}