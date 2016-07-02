package com.bhu.vas.web.service;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentCompletedNotifyDTO;
import com.bhu.vas.api.rpc.payment.model.PaymentParameter;
import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.api.rpc.payment.model.PaymentWithdraw;
import com.bhu.vas.api.rpc.payment.vto.PaymentReckoningVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.bhu.vas.business.ds.payment.service.PaymentAlipaylocationService;
import com.bhu.vas.business.ds.payment.service.PaymentParameterService;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.ds.payment.service.PaymentWithdrawService;
import com.bhu.vas.business.helper.BusinessHelper;
import com.bhu.vas.business.helper.PaymentChannelCode;
import com.bhu.vas.web.cache.BusinessCacheService;
import com.midas.api.MidasUtils;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.RandomPicker;

/**
 * 功能：提供web层需要的支付业务服务
 * Pengyu on 2016/6/17.
 */
@Service
public class PayLogicService {
	@Resource
	PaymentParameterService paymentParameterService;
	@Resource
	PaymentReckoningService paymentReckoningService;
	@Resource
	PaymentWithdrawService paymentWithdrawService;
	@Resource
	PaymentAlipaylocationService paymentAlipaylocationService;
	@Autowired
    PayHttpService payHttpService;
	//@Autowired
	//LocalEhcacheService localEhcacheService;
	@Autowired
	BusinessCacheService businessCacheService;
    private  Logger logger = LoggerFactory.getLogger(PayLogicService.class);
    
    
    public String findWapWeixinMerchantServiceByName(){
    	String result = null;
    	String cacheMerchName = businessCacheService.getWapWeixinMerchantNameFromCache();
    	if(cacheMerchName != null ){
    		result = cacheMerchName;
    		return result;
    	}
    	PaymentParameter paymentParameter = paymentParameterService.findByName("WAP_WEIXIN");
    	if(paymentParameter == null){
    		result = "Midas";
    		return result;
    	}
    	result = paymentParameter.getValue();
    	businessCacheService.storePaymentWapWeixinMerchantCacheResult(result);    	
    	return result;
    }

	public PaymentReckoningVTO findPayStatusByOrderId(String goods_no) {
		logger.info(String.format("query payment order status [%s]", goods_no));
		PaymentReckoning order = paymentReckoningService.findByOrderId(goods_no);
		if(order == null){
			return null;
		}
		PaymentReckoningVTO payReckoningVTO = queryPaymentVTO(order);
		return payReckoningVTO;
	}

	public PaymentReckoningVTO updatePaymentStatusByOrderId(PaymentReckoning order) {
		String thirdPartCode = RandomPicker.randString(BusinessHelper.letters, 12);
		logger.info(String.format("update payment order status [%s] thirdPartCode [%s]", order.getId(),thirdPartCode));
		updatePaymentStatus(order,order.getId(), thirdPartCode, "","");
		PaymentReckoningVTO payReckoningVTO = queryPaymentVTO(order);
		return payReckoningVTO;
	}
	
	private PaymentReckoningVTO queryPaymentVTO(PaymentReckoning order){
		PaymentReckoningVTO payReckoningVTO = null;
		Date payTime = order.getPaid_at();
		String fmtDate = "";
		if(payTime != null){
			fmtDate = BusinessHelper.formatDate(payTime, "yyyy-MM-dd HH:mm:ss");
		}
		payReckoningVTO = new PaymentReckoningVTO();
		payReckoningVTO.setOrderId(order.getOrder_id());
		payReckoningVTO.setReckoningId(order.getId());
		payReckoningVTO.setPay_time(fmtDate);
		payReckoningVTO.setStatus(1);
		return payReckoningVTO;
	}
    
    /**
     * 提现申请，第一次请求来的订单号，默认入库
     * @param out_trade_no 请求的订单号
     * @param total_fee 提现金额
     * @param Ip 用户Ip
     * @param type 提现方式
     * @param userId
     * @return 提现流水号
     */
    public String createPaymentWithdraw(String out_trade_no,String total_fee,String Ip,String type,String userId){
    	String withdrawType = "weixin";
    	if(type.equalsIgnoreCase("WDWX")){
    		withdrawType = "weixin";
 		}else if(type.equalsIgnoreCase("WDAL")){
 			withdrawType = "alipay";
 		}
    	
    	if(Ip == "" || Ip == null){
    		Ip = "213.42.3.24";
    	}
    	
    	PaymentWithdraw order = new PaymentWithdraw();
 		String reckoningId = BusinessHelper.generatePaymentReckoningNoByType(payHttpService.getEnv().toUpperCase()+type);
 		order.setId(reckoningId);
 		order.setOrderId(out_trade_no);
 		order.setAmount(Integer.parseInt(total_fee));
 		order.setWithdrawType(withdrawType);
 		order.setSubject("必虎提现");
 		order.setExterInvokeIp(Ip);
 		order.setUserId(userId);
 		order.setAppid("1000");
 		order.setCreatedAt(new Date());
 		paymentWithdrawService.insert(order);
 		logger.info(String.format("createPaymentWithdraw subject[%s] reckoningId[%s] "
				+ "orderId[%s] withdrawType[%s] total_fee[%s] Ip[%s] userId[%s]","必虎提现", reckoningId, out_trade_no, 
				withdrawType, total_fee, Ip, userId));
 		return reckoningId;
    }
    
    /**
     * 第一次请求来的订单号，默认入库
     * @param out_trade_no 请求的订单号
     * @param total_fee 支付金额
     * @param Ip 用户Ip
     * @param type 支付方式
     * @return 支付流水号
     */
    public String createPaymentReckoning(String out_trade_no,String total_fee,String Ip,String type,String usermac,String paymentName,String appId){
    	
    	String channelType = "";
    	//判断请求支付类型
    	String paymentType = PaymentChannelCode.BHU_PC_WEIXIN.code();
    	if(type.equals(PaymentChannelCode.BHU_PC_WEIXIN.i18n())){
 			paymentType = PaymentChannelCode.BHU_PC_WEIXIN.code();
 		}else if(type.equals(PaymentChannelCode.BHU_PC_ALIPAY.i18n())){
 			paymentType = PaymentChannelCode.BHU_PC_ALIPAY.code();
 		}else if(type.equals(PaymentChannelCode.BHU_APP_ALIPAY.i18n())){
 			paymentType = PaymentChannelCode.BHU_APP_ALIPAY.code();
 		}else if(type.equals(PaymentChannelCode.BHU_APP_WEIXIN.i18n())){
 			paymentType = PaymentChannelCode.BHU_APP_WEIXIN.code();
 		}else if(type.equals(PaymentChannelCode.BHU_WAP_WEIXIN.i18n())){
 			channelType = PaymentChannelCode.BHU_HEEPAY_WEIXIN.code();
 			paymentType = PaymentChannelCode.BHU_WAP_WEIXIN.code();
 		}else if(type.equals(PaymentChannelCode.BHU_WAP_ALIPAY.i18n())){
 			paymentType = PaymentChannelCode.BHU_WAP_ALIPAY.code();
 		}else if(type.equals(PaymentChannelCode.BHU_MIDAS_WEIXIN.i18n())){
 			channelType = PaymentChannelCode.BHU_MIDAS_WEIXIN.code();
 			paymentType = PaymentChannelCode.BHU_WAP_WEIXIN.code();
 		}else if(type.equals(PaymentChannelCode.BHU_NOW_WEIXIN.i18n())){
 			channelType = PaymentChannelCode.BHU_NOW_WEIXIN.code();
 			paymentType = PaymentChannelCode.BHU_WAP_WEIXIN.code();
 		}
    	
    	if(Ip == "" || Ip == null){
    		Ip = "213.42.3.24";
    	}
    	
    	PaymentReckoning order = new PaymentReckoning();
 		String reckoningId = BusinessHelper.generatePaymentReckoningNoByType(payHttpService.getEnv().toUpperCase()+type);
 		order.setId(reckoningId);
 		order.setOrder_id(out_trade_no);
 		order.setAmount(Integer.parseInt(total_fee));
 		order.setPayment_type(paymentType);
 		order.setChannel_type(channelType);
 		order.setOpenid("BHUUSERMAC"+usermac);
 		order.setSubject(paymentName);
 		order.setExter_invoke_ip(Ip);
 		order.setAppid(appId);
 		String token = RandomPicker.randString(BusinessHelper.letters, 10);
 		order.setToken(token);
 		paymentReckoningService.insert(order);
 		logger.info(String.format("create Payment subject [%s] reckoningId [%s] "
				+ "orderId [%s] payment_type [%s] total_fee [%s] Ip [%s] openId [%s] token [%s]", "打赏",reckoningId, out_trade_no, 
				paymentType, total_fee, Ip, "BHUUSERMAC"+usermac,token));
 		return reckoningId;
    }
    
    /**
     * 模拟充值通知接口
     * @param updatePayStatus 需要修改的流水号对象
     * @param out_trade_no 支付流水号
     * @param thirdPartCode 第三方支付流水号
     * @param thridType 第三方标识
     * @param billo 第三方流程号
     */
    public void updatePaymentStatus(PaymentReckoning updatePayStatus,String out_trade_no,String thirdPartCode,
    		String thridType,String billo){
		updatePayStatus.setThird_party_code(thirdPartCode);
		updatePayStatus.setPay_status(1);
		updatePayStatus.setPaid_at(new Date());
		if(!thridType.isEmpty()){
			updatePayStatus.setRemark(billo);
		}
 		paymentReckoningService.update(updatePayStatus);
 		logger.info(String.format("update out_trade_no [%s] payment status finished.",out_trade_no));
 		
 		//通知订单
 		PaymentReckoning payNotice =  paymentReckoningService.getById(out_trade_no);
 		ResponsePaymentCompletedNotifyDTO rpcn_dto = new ResponsePaymentCompletedNotifyDTO();
 		rpcn_dto.setSuccess(true);
 		rpcn_dto.setOrderid(payNotice.getOrder_id());
 		rpcn_dto.setPayment_type(payNotice.getPayment_type());
 		String fmtDate = BusinessHelper.formatDate(payNotice.getPaid_at(), "yyyy-MM-dd HH:mm:ss");
 		rpcn_dto.setPaymented_ds(fmtDate);
 		if(thridType != null){
 			rpcn_dto.setPayment_proxy_type(thridType);
 		}
 		String notify_message = JsonHelper.getJSONString(rpcn_dto);
 		CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(notify_message);
 		logger.info(String.format("notify out_trade_no [%s] payment status to redis: [%s]",out_trade_no,notify_message));
 		
 		//修改订单的通知状态
 		updatePayStatus.setNotify_status(1);
 		updatePayStatus.setNotify_at(new Date());
 		paymentReckoningService.update(updatePayStatus);
 		logger.info(String.format("update out_trade_no [%s] notify status finished.",out_trade_no));
 		
 		PaymentReckoningVTO payOrderCache = updatePaymentCache(payNotice.getOrder_id(),out_trade_no);
		
		if(payOrderCache != null){
			logger.info(String.format("write out_trade_no [%s] order_id [%s] to cache finished.",out_trade_no,payNotice.getOrder_id()));
		}
		logger.info("success");
    }
    
    /**
     * 
     * @param updatePayStatus
     * @param out_trade_no
     * @param thirdPartCode
     * @param invalidCreateReckoningidFailed 
     */
    public void updateWithdrawalsStatus(PaymentWithdraw updateWithdrawStatus,String out_trade_no,String thirdPartCode,boolean result){
    	if(result){
    		updateWithdrawStatus.setThirdPartCode(thirdPartCode);
        	updateWithdrawStatus.setWithdrawStatus(1);
        	updateWithdrawStatus.setWithdrawAt(new Date());
    		paymentWithdrawService.update(updateWithdrawStatus);
    		logger.info(String.format("update out_trade_no [%s] withdrawals status finished.",out_trade_no));
     		
     		//通知订单
     		PaymentWithdraw payNotice =  paymentWithdrawService.getById(out_trade_no);
     		ResponsePaymentCompletedNotifyDTO rpcn_dto = new ResponsePaymentCompletedNotifyDTO();
     		rpcn_dto.setSuccess(true);
     		rpcn_dto.setOrderid(payNotice.getOrderId());
     		rpcn_dto.setPayment_type(payNotice.getWithdrawType());
     		String fmtDate = BusinessHelper.formatDate(payNotice.getWithdrawAt(), "yyyy-MM-dd HH:mm:ss");
     		rpcn_dto.setPaymented_ds(fmtDate);
     		String notify_message = JsonHelper.getJSONString(rpcn_dto);
            
     		CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(notify_message);
     		logger.info(String.format("notify out_trade_no [%s] payment status to redis: [%s]",out_trade_no,notify_message));
     		
     		//修改订单的通知状态
     		updateWithdrawStatus.setNotifyStatus(1);
     		updateWithdrawStatus.setNotifiedAt(new Date());
     		paymentWithdrawService.update(updateWithdrawStatus);
     		
     		logger.info(String.format("update out_trade_no [%s] notify status finished.",out_trade_no));
     		
     		PaymentReckoningVTO payOrderCache = updatePaymentCache(payNotice.getOrderId(),out_trade_no);
    		
    		if(payOrderCache != null){
    			logger.info(String.format("write out_trade_no [%s] order_id [%s] to cache finished.",out_trade_no,payNotice.getOrderId()));
    		}
    		logger.info("success");	
    	}else{
    		ResponsePaymentCompletedNotifyDTO rpcn_dto = new ResponsePaymentCompletedNotifyDTO();
    		rpcn_dto.setSuccess(false);
     		rpcn_dto.setOrderid(out_trade_no);
     		rpcn_dto.setPayment_type(thirdPartCode);
     		rpcn_dto.setPaymented_ds("");
     		String notify_message = JsonHelper.getJSONString(rpcn_dto);
            
     		CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(notify_message);
     		logger.info(String.format("notify out_trade_no [%s] payment status to redis: [%s]",out_trade_no,notify_message));
     		
     		logger.info("end");	
    	}
    	
    }
    
    private PaymentReckoningVTO updatePaymentCache(String orderId,String tId){
    	PaymentReckoningVTO result = new PaymentReckoningVTO();
    	result.setOrderId(orderId);
    	result.setReckoningId(tId);
    	businessCacheService.storePaymentCacheResult(tId, result);
    	PaymentReckoningVTO getPayOrder = businessCacheService.getPaymentOrderCacheByOrderId(tId);
    	return getPayOrder;
    	
    }

    /*private PaymentReckoningVTO updatePaymentEhCache(String orderId,String tId){
	
		PaymentReckoningVTO result = new PaymentReckoningVTO();
		result.setOrderId(orderId);
		result.setReckoningId(tId);
		localEhcacheService.putLocalCache(orderId, result);;
		PaymentReckoningVTO getPayOrder = localEhcacheService.getLocalCache(tId);
		return getPayOrder;
	}*/
    public static void main(String[] args) {
    	//System.out.println(RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR));
    	//ResponseError.embed();
//    	PaymentTypeVTO result = new PaymentTypeVTO();
//    	result.setType("weixin");
//    	result.setUrl("@#$%^&*(");
//    	System.out.println(1002==BusinessEnumType.CommdityApplication.BHU_PREPAID_BUSINESS.getKey());
//    	System.out.println(BusinessEnumType.CommdityApplication.BHU_PREPAID_BUSINESS.getSecret().equals("1F915A8DA370422582CBAC1DB6A806UU"));
//    	if("1002".equals(BusinessEnumType.CommdityApplication.BHU_PREPAID_BUSINESS.getKey())&&BusinessEnumType.CommdityApplication.BHU_PREPAID_BUSINESS.getSecret().equals("1F915A8DA370422582CBAC1DB6A806UU")){
//			System.out.println("虎钻"); ;
//		}else if("1001".equals(BusinessEnumType.CommdityApplication.DEFAULT.getKey())&&BusinessEnumType.CommdityApplication.DEFAULT.getSecret().equals("1F915A8DA370422582CBAC1DB6A806DD")){
//			System.out.println("打赏"); ;
//		}else{
//			System.out.println("err"); ;
//		}
    	
//    	System.out.println(CommdityApplication.BHU_PREPAID_BUSINESS.getKey().equals(Integer.parseInt("1002")));
    	HashMap<String,String> params = new HashMap<String,String>();
         params.put("pubacct_payamt_coins", "");
         params.put("ts", "1466061172");
         params.put("payitem", "TESTMDWX1466061152460achy*0.10*1");
         params.put("zoneid", "1");
         params.put("cftid","4000952001201606167356678736");
         params.put("appid", "1450006356");
         params.put("channel_id", "2001-html5-2011-bhuwifi-st_dummy");
         params.put("version", "v3");
         params.put("amt", "1");
         
         params.put("providetype", "5");
         params.put("appmeta", "*wechat*st_dummy");
         
         params.put("token", "070AC9513366A723769DE3889EB34A4D30614");
         params.put("clientver", "html5");
         params.put("mbazinga", "1");
         params.put("payamt_coins", "0");
         params.put("openid", "WSWW22");
         params.put("billno", "-APPDJ54004-20160616-1512375035");
         //params.put("sig", "ApNHSKKUkPrO/bZqHOEeaciBsoY=");
         String sign = "ApNHSKKUkPrO/bZqHOEeaciBsoY=";
         String notify_url = PayHttpService.MIDAS_RETURN_URL;
         boolean verifySig = MidasUtils.verifySig(params,notify_url,sign);
         System.out.println(verifySig);

         //生成支付签名,这个签名 给 微信支付的调用使用
         //String paySign =  payHttpService.createSign(payHttpService.getMchKey(),"UTF-8", params);
//         
//         params.put("sign", "222");
//     	String json= JsonHelper.getJSONString(params);
    	
//      	SpringMVCHelper.renderJson(response, result);
    	//System.out.println(BusinessHelper.formatMac("84:82:f4:28:7a:ec"));;
    }
	
}
