package com.bhu.vas.web.service;

import java.io.IOException;

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
import com.bhu.vas.business.helper.BusinessHelperBK;
import com.bhu.vas.business.helper.PaymentChannelCode;
import com.bhu.vas.business.qqmail.SendMailHelper;
import com.bhu.vas.web.http.response.UnifiedOrderResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.zxing.WriterException;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;

/**
 * Weixin支付
 * @author Pengyu
 *
 */
@Service
public class NativeWeixinService{
	private final Logger logger = LoggerFactory.getLogger(NativeWeixinService.class);

    private int weixinNativeWarningCount;
    private String weixinNativeWarningTime = "";
    private String weixinNativeErrorTime = "";
	
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
	public PaymentTypeVTO doNativeWxPayment(HttpServletRequest request, HttpServletResponse response,String channel_type,String total_fee,String out_trade_no,String Ip,String locationUrl,String usermac,String paymentName,String appId){
		
		PaymentTypeVTO result= new PaymentTypeVTO();
        String NOTIFY_URL = PayHttpService.WEIXIN_NOTIFY_URL;
        String PRUE_LOGO_URL = PayHttpService.PRUE_LOGO_URL;
        String QR_CODE_URL = PayHttpService.QR_CODE_URL;
        String product_name= paymentName;//订单名称
    	total_fee = BusinessHelper.getMoney(total_fee);
    	String type = "";
    	if(channel_type.equals(PaymentSceneChannelType.WAPQR.getName())){
    		type = PaymentChannelCode.BHU_WAP_WEIXIN.i18n();
    	}else{
    		type = PaymentChannelCode.BHU_PC_WEIXIN.i18n();
    	}
    		
    	long begin = System.currentTimeMillis(); 
        String reckoningId = payLogicService.createPaymentId(out_trade_no,channel_type,total_fee,Ip,type,usermac,paymentName,appId,"");
        long end = System.currentTimeMillis() - begin; 
        logger.info(out_trade_no+"生成支付流水号耗时：" + end + "毫秒");
        
		if (!StringUtils.isBlank(locationUrl)) {
			logger.info(String.format("doNativeWxPayment locationUrl [%s] ",locationUrl));
			PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
			orderLocation.setTid(reckoningId);
			orderLocation.setLocation(locationUrl);
			long insert_locationUrl_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
			paymentAlipaylocationService.insert(orderLocation);
			long insert_locationUrl_end = System.currentTimeMillis() - insert_locationUrl_begin; // 这段代码放在程序执行后
			logger.info(out_trade_no+"插入完成地址入库耗时：" + insert_locationUrl_end + "毫秒");
		}

		logger.info(String.format("apply wx payment reckoningId [%s] product_name [%s] total_fee [%s] ip [%s]"
        		+ "NOTIFY_URL [%s] ",reckoningId, product_name, total_fee, request.getRemoteAddr(),NOTIFY_URL ));
		long request_unifiedorder_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
		UnifiedOrderResponse unifiedOrderResponse = payHttpService.unifiedorder(reckoningId, product_name, total_fee, request.getRemoteAddr(), NOTIFY_URL, "");
		long request_unifiedorder_end = System.currentTimeMillis() - request_unifiedorder_begin; // 这段代码放在程序执行后
		logger.info(out_trade_no+"调用native微信统一下单接口耗时：" + request_unifiedorder_end + "毫秒");
		int nowOutTimeLevel = payHttpService.getOt();
		try{
			if(request_unifiedorder_end > nowOutTimeLevel){
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "微信扫码支付",request_unifiedorder_end+"");
				if(request_unifiedorder_end > 7000){
					weixinNativeWarningCount++;
				} 
				if(request_unifiedorder_end > 30000){
					String sendMsg = payLogicService.updatePaymentParam(4,smsg,weixinNativeErrorTime);
					if(!StringUtils.isBlank(sendMsg)){
						weixinNativeErrorTime =sendMsg;
					}
				}	
				PaymentOutimeErrorLog  paymentNowpayErrorLog = new PaymentOutimeErrorLog();
				paymentNowpayErrorLog.setId(reckoningId);
				paymentNowpayErrorLog.setOrder_id(out_trade_no);
				paymentNowpayErrorLog.setOt(new Long(request_unifiedorder_end).intValue());
				paymentNowpayErrorLog.setC_type("NativeWeixin");
				paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
			}
			
			switch (weixinNativeWarningCount) {
			case 3:
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "微信扫码支付",request_unifiedorder_end+"");
				String resp = "ture";
				String acc = PayHttpService.Internal_level2_warning_man;
				String curDate = (BusinessHelper.getTimestamp()+"").substring(0, 10);
				if(!weixinNativeWarningTime.equals(curDate)){
					weixinNativeWarningTime = curDate;
					SendMailHelper.doSendMail(2,smsg);
					resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
				}
				logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,resp));
				weixinNativeWarningCount = 0;
				break;
	
			default:
				break;
			}
		}catch(Exception e){
			logger.info(String.format("apply  NativeWeixin catch exception [%s]",e.getMessage()+e.getCause()));
		}
        if(!unifiedOrderResponse.isResultSuccess()){
        	String status = unifiedOrderResponse.getResultErrorCode();
			String msg = unifiedOrderResponse.getResultMessage();
			logger.info(String.format("apply weixin native rqurest weixin server return status [%s] msg [%s]", status,msg));
	    	result.setType("FAIL");
	    	result.setUrl("微信服务器连接异常，请求超时。");
			return result;
        }
    	String codeUrl = unifiedOrderResponse.getCode_url();
    	String base64CodeUrl = "";
		try {
			long base64Code_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
//			base64CodeUrl = BusinessHelper.GetBase64ImageStr(codeUrl,QR_CODE_URL,PRUE_LOGO_URL);
			base64CodeUrl = BusinessHelperBK.GetBase64ImageStr(codeUrl,PRUE_LOGO_URL);
			long base64Code_end = System.currentTimeMillis() - base64Code_begin; // 这段代码放在程序执行后
			logger.info(out_trade_no+"生成二维码64位code耗时：" + base64Code_end + "毫秒");
		} catch (WriterException | IOException e) {
			logger.error(String.format("return pc weixin catch WriterException "+e.getCause()));
		}
    	base64CodeUrl = base64CodeUrl.replace("\r\n", "");
    	base64CodeUrl = base64CodeUrl.replace("\n", "");	
    	result.setType("img");
    	result.setUrl(base64CodeUrl);
        return result;
    }
	
    
	

   	
   	
}