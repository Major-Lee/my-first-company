package com.bhu.vas.web.service;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType.PaymentSceneChannelType;
import com.bhu.vas.api.rpc.payment.model.PaymentAlipaylocation;
import com.bhu.vas.api.rpc.payment.model.PaymentOutimeErrorLog;
import com.bhu.vas.api.vto.payment.PaymentTypeVTO;
import com.bhu.vas.business.ds.payment.service.PaymentAlipaylocationService;
import com.bhu.vas.business.ds.payment.service.PaymentOutimeErrorLogService;
import com.bhu.vas.business.ds.payment.service.PaymentParameterService;
import com.bhu.vas.business.helper.BusinessHelper;
import com.bhu.vas.business.helper.PaymentChannelCode;
import com.bhu.vas.business.qqmail.SendMailHelper;
import com.bhu.vas.web.http.response.AppUnifiedOrderResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;

/**
 * Weixin支付
 * @author Pengyu
 *
 */
@Service
public class AppWeixinService{
	private final Logger logger = LoggerFactory.getLogger(AppWeixinService.class);

	private int weixinAppWarningCount;
    private String weixinAppWarningTime = "";
    private String weixinAppErrorTime = "";    
	
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
	public PaymentTypeVTO doAppPayment(HttpServletRequest request, HttpServletResponse response,String total_fee,String out_trade_no,String Ip,String locationUrl,String usermac,String paymentName,String appid,String channel){
		PaymentTypeVTO result= new PaymentTypeVTO();
		String NOTIFY_URL = PayHttpService.WEIXIN_NOTIFY_URL;
		String product_name= paymentName;//订单名称
		String appId = "";
		String mchId = "";
		String mchKey = "";
		if(channel.equals(PaymentSceneChannelType.APPHELPER.getName())){
			appId = payHttpService.getAppDSAppId();
			mchId = payHttpService.getAppDSMchId();
			mchKey = payHttpService.getAppDSMchKey();
		}else if(channel.equals(PaymentSceneChannelType.APPMANAGER.getName())){
			appId = payHttpService.getAppAppId();
			mchId = payHttpService.getAppMchId();
			mchKey = payHttpService.getAppMchKey();
		}else{
			appId = payHttpService.getAppDSAppId();
			mchId = payHttpService.getAppDSMchId();
			mchKey = payHttpService.getAppDSMchKey();
		}
		total_fee = BusinessHelper.getMoney(total_fee);
		String reckoningId = payLogicService.createPaymentId(out_trade_no,channel,total_fee,Ip,PaymentChannelCode.BHU_APP_WEIXIN.i18n(),usermac,paymentName,appid,"");
		if (!StringUtils.isBlank(locationUrl)) {
			logger.info(String.format("apply App Wx Payment locationUrl [%s] ",locationUrl));
			PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
			orderLocation.setTid(reckoningId);
			orderLocation.setLocation(locationUrl);
			paymentAlipaylocationService.insert(orderLocation);
		}
		logger.info(String.format("apply App wx payment reckoningId [%s] product_name [%s] total_fee [%s] ip [%s]"
				+ "NOTIFY_URL [%s] ",reckoningId, product_name, total_fee, request.getRemoteAddr(),NOTIFY_URL ));
		long request_unifiedorder_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
		AppUnifiedOrderResponse unifiedOrderResponse = payHttpService.unifiedorderForApp(appId,mchId,mchKey,reckoningId, product_name, total_fee, request.getRemoteAddr(), NOTIFY_URL, "");
		long request_unifiedorder_end = System.currentTimeMillis() - request_unifiedorder_begin; // 这段代码放在程序执行后
		logger.info(out_trade_no+"调用App微信统一下单接口耗时：" + request_unifiedorder_end + "毫秒");
		int nowOutTimeLevel = payHttpService.getOt();
		try{
			
			if(request_unifiedorder_end > nowOutTimeLevel){
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "微信App支付",request_unifiedorder_end+"");
				if(request_unifiedorder_end > 7000){
					weixinAppWarningCount++;
				} 
				if(request_unifiedorder_end > 30000){
					String sendMsg = payLogicService.updatePaymentParam(5,smsg,weixinAppErrorTime);
					if(!StringUtils.isBlank(sendMsg)){
						weixinAppErrorTime =sendMsg;
					}
				}	
				PaymentOutimeErrorLog  paymentNowpayErrorLog = new PaymentOutimeErrorLog();
				paymentNowpayErrorLog.setId(reckoningId);
				paymentNowpayErrorLog.setOrder_id(out_trade_no);
				paymentNowpayErrorLog.setOt(new Long(request_unifiedorder_end).intValue());
				paymentNowpayErrorLog.setC_type("AppWeixin");
				paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
			}
			
			switch (weixinAppWarningCount) {
			case 3:
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "微信App支付",request_unifiedorder_end+"");
				String resp = "ture";
				String acc = PayHttpService.Internal_level2_warning_man;
				String curDate = (BusinessHelper.getTimestamp()+"").substring(0, 10);
				if(!weixinAppWarningTime.equals(curDate)){
					weixinAppWarningTime = curDate;
					SendMailHelper.doSendMail(2,smsg);
					resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
				}
				logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,resp));
				weixinAppWarningCount = 0;
				break;
				
			default:
				break;
			}
		}catch(Exception e){
			logger.info(String.format("apply weixinApp catch exception [%s]",e.getMessage()+e.getCause()));
		}
		if(!unifiedOrderResponse.isResultSuccess()){
			String status = unifiedOrderResponse.getResultErrorCode();
			String msg = unifiedOrderResponse.getResultMessage();
			logger.info(String.format("apply weixin app rqurest weixin server return  status [%s] msg [%s]", status,msg));
			result.setType("FAIL");
			result.setUrl("微信服务器连接异常，请求超时。");
			return result;
		}
		String timestamp = payHttpService.getTimeStamp();//生成1970年到现在的秒数.
		String noncestr = payHttpService.getNonceStr();//生成随机字符串
		String prepay_id = unifiedOrderResponse.getPrepay_id();
		SortedMap<Object, Object> params = new TreeMap<Object,Object>();
		params.put("appid", appId);
		params.put("partnerid", mchId);
		params.put("prepayid", prepay_id);
		params.put("package", "Sign=WXPay");
		params.put("noncestr", noncestr);
		params.put("timestamp",timestamp);
		String paySign =  payHttpService.createSign(mchKey,"UTF-8", params);
		params.put("sign", paySign);
		String json= JsonHelper.getJSONString(params);
		result.setType("json");
		result.setUrl(json);
		return result;
	}
}