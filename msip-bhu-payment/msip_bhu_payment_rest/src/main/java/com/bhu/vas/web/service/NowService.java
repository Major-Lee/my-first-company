package com.bhu.vas.web.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import com.nowpay.config.NowpayConfig;
import com.nowpay.core.NowpaySubmit;
import com.nowpay.sign.MD5Facade;
import com.nowpay.util.UtilDate;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;

/**
 * Weixin支付
 * @author Pengyu
 *
 */
@Service
public class NowService{
	private final Logger logger = LoggerFactory.getLogger(NowService.class);

    private int nowWarningCount;
    private String nowWarningTime = "";
    private String nowErrorTime = ""; 
	
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
	public PaymentTypeVTO doNow(HttpServletResponse response, 
    		String type,
    		String totalPrice,
    		String out_trade_no,
    		String ip,
    		String locationUrl,
    		String usermac,
    		String paymentName,
    		String appid){
		response.setCharacterEncoding("utf-8");
		PaymentTypeVTO result = new PaymentTypeVTO();
		// 服务器异步通知页面路径
		String notify_url = PayHttpService.NOWIPAY_NOTIFY_URL;
		// 打赏页面跳转同步通知页面路径
		String return_url = PayHttpService.NOWPAY_RETURN_URL;
		// 订单名称
		String subject = paymentName;// ;new String("打赏".getBytes("ISO-8859-1"),
										// "utf-8");
		// 付款金额
		String total_fee = totalPrice;
		// 订单描述
		String body = "BHU Service";
		// 数据库存的是分，此处需要把传来的支付金额转换成分，而传给支付宝的保持不变（默认元）
		String total_fee_fen = BusinessHelper.getMoney(total_fee);
		if (CommdityApplication.BHU_PREPAID_BUSINESS.getKey().equals(Integer.parseInt(appid))) {
			return_url = PayHttpService.ALIPAY_PREPAID_RETURN_URL;
		}
		Map<String, String> dataMap = new HashMap<String, String>();

		long get_now_reckoning_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
		String reckoningId = payLogicService.createPaymentId(out_trade_no, "Now", total_fee_fen, ip,
				PaymentChannelCode.BHU_WAP_WEIXIN.i18n(), usermac, paymentName, appid);
		long get_now_reckoning_end = System.currentTimeMillis() - get_now_reckoning_begin; // 这段代码放在程序执行后
		logger.info(out_trade_no + "现在支付获取支付流水号耗时：" + get_now_reckoning_end + "毫秒");
		// 做MD5签名
		dataMap.put("appId", NowpayConfig.appId);
		dataMap.put("mhtOrderNo", reckoningId);
		dataMap.put("mhtOrderName", subject);
		dataMap.put("mhtCurrencyType", NowpayConfig.mhtCurrencyType);
		dataMap.put("mhtOrderAmt", total_fee_fen);
		dataMap.put("mhtOrderDetail", body);
		dataMap.put("mhtOrderType", NowpayConfig.mhtOrderType);
		dataMap.put("mhtOrderStartTime", UtilDate.getOrderNum());
		dataMap.put("notifyUrl", notify_url);
		dataMap.put("frontNotifyUrl", return_url);
		dataMap.put("mhtCharset", NowpayConfig.mhtCharset);
		dataMap.put("payChannelType", NowpayConfig.payChannelType);
		dataMap.put("mhtReserved", NowpayConfig.mhtReserved);
		String mhtSignature = MD5Facade.getFormDataParamMD5(dataMap, NowpayConfig.appKey, "UTF-8");
		dataMap.put("mhtSignType", NowpayConfig.mhtSignType);
		dataMap.put("mhtSignature", mhtSignature);
		dataMap.put("funcode", NowpayConfig.funcode);
		dataMap.put("deviceType", NowpayConfig.deviceType);
		if (!StringUtils.isBlank(locationUrl)) {
			PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
			orderLocation.setTid(reckoningId);
			orderLocation.setLocation(locationUrl);
			long insert_locationUrl_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
			paymentAlipaylocationService.insert(orderLocation);
			long insert_locationUrl_end = System.currentTimeMillis() - insert_locationUrl_begin; // 这段代码放在程序执行后
			logger.info(out_trade_no + "现在支付locationUrlr入库耗时：" + insert_locationUrl_end + "毫秒");
			logger.info(String.format("apply nowpay set location reckoningId [%s] locationUrl [%s] insert finished.",
					reckoningId, locationUrl));
		}

		String sHtmlText = "";
		try {
			long request_now_url_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
			sHtmlText = NowpaySubmit.buildRequest(dataMap, "post", "确认");
			long request_now_url_end = System.currentTimeMillis() - request_now_url_begin; // 这段代码放在程序执行后
			logger.info(out_trade_no + "请求现在支付获取支付url耗时：" + request_now_url_end + "毫秒");
			int nowOutTimeLevel = payHttpService.getOt();
			try {
				if (request_now_url_end > nowOutTimeLevel) {
					String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "现在支付",
							request_now_url_end + "");
					if (request_now_url_end > 6500) {
						nowWarningCount++;
					}
					if (request_now_url_end > 30000) {
						String sendMsg = payLogicService.updatePaymentParam(1, smsg, nowErrorTime);
						if (!StringUtils.isBlank(sendMsg)) {
							nowErrorTime = sendMsg;
						}
					}

					logger.info(out_trade_no + "请求现在支付获取支付url超时" + (request_now_url_end - 1000) + "毫秒,记录错误数据。");
					PaymentOutimeErrorLog paymentNowpayErrorLog = new PaymentOutimeErrorLog();
					paymentNowpayErrorLog.setId(reckoningId);
					paymentNowpayErrorLog.setOrder_id(out_trade_no);
					paymentNowpayErrorLog.setOt(new Long(request_now_url_end).intValue());
					paymentNowpayErrorLog.setC_type("Now");
					paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
				}

				switch (nowWarningCount) {
				case 3:
					String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "现在支付",
							request_now_url_end + "");
					String resp = "ture";
					String acc = PayHttpService.Internal_level2_warning_man;
					String curDate = (BusinessHelper.getTimestamp() + "").substring(0, 10);
					if (!nowWarningTime.equals(curDate)) {
						nowWarningTime = curDate;
						SendMailHelper.doSendMail(2, smsg);
						resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway)
								.send(smsg, acc);
					}
					logger.info(
							String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]", acc, smsg, resp));
					nowWarningCount = 0;
					break;

				default:
					break;
				}
			} catch (Exception e) {
				logger.info(String.format("apply now pay catch exception [%s]", e.getMessage() + e.getCause()));
			}
			result = new PaymentTypeVTO();
			result.setChannel("Now");
			result.setType("http");
			result.setUrl(sHtmlText);
			return result;
		} catch (Exception e) {
			SendMailHelper.doSendMail(3, "请求现在支付获取支付url捕获异常：" + e.getMessage() + e.getCause());
			result.setType("FAIL");
			result.setUrl("支付请求失败");
			return result;
		}
	}
}