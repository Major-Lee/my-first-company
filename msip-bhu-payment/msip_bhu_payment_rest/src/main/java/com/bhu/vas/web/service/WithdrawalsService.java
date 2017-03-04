package com.bhu.vas.web.service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreateWithdrawDTO;
import com.bhu.vas.api.rpc.payment.model.PaymentOutimeErrorLog;
import com.bhu.vas.api.rpc.payment.model.PaymentWithdraw;
import com.bhu.vas.business.ds.payment.service.PaymentOutimeErrorLogService;
import com.bhu.vas.business.ds.payment.service.PaymentWithdrawService;
import com.bhu.vas.business.helper.BusinessHelper;
import com.bhu.vas.business.qqmail.SendMailHelper;
import com.bhu.vas.web.http.response.WithDrawNotifyResponse;
import com.bhu.vas.web.payment.PaymentController;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;
/**
 * Paypal消息处理流程逻辑类
 * @author pamchen-1
 *
 */
@Service
public class WithdrawalsService {
	private int weixinWithdrawWarningCount;
    private String weixinWithdrawWarningTime = "";
    private String weixinWithdrawErrorTime = "";
	
	@Autowired
	PayLogicService payLogicService;
	@Autowired
    PayHttpService payHttpService;
	@Resource
	PaymentOutimeErrorLogService paymentOutimeErrorLogService;
	@Resource
	PaymentWithdrawService paymentWithdrawService;
	
	private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	
	
	 /**
	 * 处理微信提现
	 * @param request
	 * @param response
	 * @param total_fee
	 * @param withdraw_no
	 * @param Ip
	 * @param userId
	 * @param userName
	 * @return
	 */
	public ResponseCreateWithdrawDTO doWxWithdrawals(HttpServletRequest request, HttpServletResponse response, String total_fee,
			String withdraw_no, String Ip,String userId,String userName) {
		ResponseCreateWithdrawDTO result = new ResponseCreateWithdrawDTO();
        String certificateUrl = PayHttpService.WITHDRAW_URL;
        logger.info(String.format("apply WxWithdrawals withdraw_no [%s] total_fee [%s] Ip [%s]"
        		+ " withdraw_type [%s] userId [%s]",withdraw_no,total_fee,Ip,"WDWX",userId));
        String product_name="必虎提现";//订单名称
    	total_fee = BusinessHelper.getMoney(total_fee);
        String reckoningId = payLogicService.createPaymentWithdraw(withdraw_no,total_fee,Ip,"WDWX",userId);
        logger.info(String.format("doWxWithdrawals reckoningId [%s] product_name [%s] total_fee [%s] ip [%s]"
        		+ "certificateUrl [%s] userId [%s] userName [%s]",reckoningId, product_name, total_fee, request.getRemoteAddr(), certificateUrl, userId,userName));
        long request_unifiedorder_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
        WithDrawNotifyResponse unifiedOrderResponse = payHttpService.sendWithdraw(reckoningId, product_name, total_fee, request.getRemoteAddr(), certificateUrl, userId,userName);
        long request_unifiedorder_end = System.currentTimeMillis() - request_unifiedorder_begin; // 这段代码放在程序执行后
        logger.info(withdraw_no+"调用微信提现统一下单接口耗时：" + request_unifiedorder_end + "毫秒");
		int nowOutTimeLevel = payHttpService.getOt();
		try{
			
//			if(request_unifiedorder_end > nowOutTimeLevel){
//				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "微信提现支付",request_unifiedorder_end+"");
//				if(request_unifiedorder_end > 7000){
//					weixinWithdrawWarningCount++;
//				}
//				if(request_unifiedorder_end > 30000){
//					String sendMsg = payLogicService.updatePaymentParam(7,smsg,weixinWithdrawErrorTime);
//					if(!StringUtils.isBlank(sendMsg)){
//						weixinWithdrawErrorTime =sendMsg;
//					}
//				}	
//				PaymentOutimeErrorLog  paymentNowpayErrorLog = new PaymentOutimeErrorLog();
//				paymentNowpayErrorLog.setId(reckoningId);
//				paymentNowpayErrorLog.setOrder_id(withdraw_no);
//				paymentNowpayErrorLog.setOt(new Long(request_unifiedorder_end).intValue());
//				paymentNowpayErrorLog.setC_type("withdrawWeixin");
//				paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
//			}
//		
//			switch (weixinWithdrawWarningCount) {
//			case 3:
//				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "微信提现支付",request_unifiedorder_end+"");
//				String resp = "ture";
//				String acc = PayHttpService.Internal_level2_warning_man;
//				String curDate = (BusinessHelper.getTimestamp()+"").substring(0, 10);
//				if(!weixinWithdrawWarningTime.equals(curDate)){
//					weixinWithdrawWarningTime = curDate;
//					SendMailHelper.doSendMail(2,smsg);
//					resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
//				}
//				logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,resp));
//				weixinWithdrawWarningCount = 0;
//				break;
//			default:
//				break;
//			}
		}catch(Exception e){
			logger.info(String.format("apply Withdraw catch exception [%s]",e.getMessage()+e.getCause()));
		}
        PaymentWithdraw payWithdraw =  paymentWithdrawService.getById(reckoningId);
        if (payWithdraw == null) {
        	logger.error(String.format("get WxWithdrawals payWithdraw [%s] is null",payWithdraw));
        	payLogicService.updateWithdrawalsStatus(null, reckoningId, "weixin",false);
        	return null;
        }
        String orderId = payWithdraw.getOrderId();
        if(unifiedOrderResponse == null){
        	logger.error(String.format("apply payment unifiedOrderResponse [%s]", unifiedOrderResponse));
        	result.setWithdraw_type("FAIL");
         	result.setSuccess(false);
         	result.setUrl("");
        	payLogicService.updateWithdrawalsStatus(null, withdraw_no, "weixin",false);
        	return result;
        }

        if(!unifiedOrderResponse.isResultSuccess()){
        	String status = unifiedOrderResponse.getResultErrorCode();
			String msg = unifiedOrderResponse.getResultMessage();
			payLogicService.updateWithdrawalsStatus(null, withdraw_no, "weixin",false);
			logger.info(String.format("apply payment status [%s] msg [%s]", status,msg));
			result.setWithdraw_type("FAIL");
         	result.setSuccess(false);
         	result.setUrl(msg);
         	return result;
        }
        
        String out_trade_no = unifiedOrderResponse.getPartner_trade_no();
        String trade_no = unifiedOrderResponse.getPayment_no();
        logger.info(String.format("return WxWithdrawals reckoningId [%s] trade_no [%s] orderId [%s]",out_trade_no, trade_no,orderId));
		int withdrawStatus = payWithdraw.getWithdrawStatus();
		if(withdrawStatus == 0){ //0未支付;1支付成功
            if("SUCCESS".equals(unifiedOrderResponse.getReturn_code()) && "SUCCESS".equals(unifiedOrderResponse.getResult_code())){
            	payLogicService.updateWithdrawalsStatus(payWithdraw, out_trade_no, trade_no,true);
             	result.setWithdraw_type("weixin");
             	result.setSuccess(true);
             	result.setUrl("");
             	return result;
            }else{
            	result.setSuccess(false);
    	    	result.setUrl(unifiedOrderResponse.getResultMessage());
    			return result;
            }
		}else{
			result.setWithdraw_type("weixin");
         	result.setSuccess(true);
         	result.setUrl("");
         	return result;
		}
	}
}
