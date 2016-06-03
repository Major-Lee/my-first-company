package com.bhu.vas.web.payment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreateWithdrawDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentCompletedNotifyDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.payment.model.PaymentAlipaylocation;
import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.api.rpc.payment.model.PaymentWithdraw;
import com.bhu.vas.api.rpc.payment.vto.PaymentReckoningVTO;
import com.bhu.vas.api.vto.payment.PaymentTypeVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.bhu.vas.business.ds.payment.service.PaymentAlipaylocationService;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.ds.payment.service.PaymentWithdrawService;
import com.bhu.vas.business.helper.BusinessHelper;
import com.bhu.vas.business.helper.XMLUtil;
import com.bhu.vas.web.cache.BusinessCacheService;
import com.bhu.vas.web.http.response.PaySuccessNotifyResponse;
import com.bhu.vas.web.http.response.UnifiedOrderResponse;
import com.bhu.vas.web.http.response.WithDrawNotifyResponse;
import com.bhu.vas.web.service.PayHttpService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.heepay.api.Heepay;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.PaymentResponseSuccess;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.smartwork.msip.localunit.RandomPicker;

/**
 * uPay支付控制层+业务逻辑层
 * @author Pengyu
 *
 */
@Controller
public class PaymentController extends BaseController{
	private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
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
	
	/**
	 * 查询支付流水号接口
	 * @param request
	 * @param response
	 * @param orderid 请求支付流水号
	 */
	@ResponseBody()
	@RequestMapping(value={"/payment/queryOrderPayStatus","/query"},method={RequestMethod.GET,RequestMethod.POST})
	public void queryPaymentOrder(HttpServletRequest request,HttpServletResponse response,String goods_no,String exter_invoke_ip,String appid,String secret){
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("query_payment order status [%s]", goods_no));
		try{
			PaymentReckoning order = paymentReckoningService.findByOrderId(goods_no);
			if(order != null){
				Date payTime = order.getPaid_at();
				String fmtDate = "";
				if(payTime != null){
					fmtDate = BusinessHelper.formatDate(payTime, "yyyy-MM-dd HH:mm:ss");
				}
				PaymentReckoningVTO payReckoningVTO = new PaymentReckoningVTO();
				payReckoningVTO.setOrderId(order.getOrder_id());
				payReckoningVTO.setReckoningId(order.getId());
				payReckoningVTO.setPay_time(fmtDate);
				payReckoningVTO.setStatus(order.getPay_status());
				logger.info(String.format("query_payment order status success result [%s]", ResponseSuccess.embed(payReckoningVTO)));
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(payReckoningVTO));
				return;
			}else{
				logger.info(String.format("query_payment order status error result [%s]", ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST));
	        	throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST,new String[]{"商品数据不存在"}); 
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
	/**
	 * 请求提现接口
	 * @param request
	 * @param response
	 * @param withdraw_type
	 * @param total_fee
	 * @param userId
	 * @param userName
	 * @param withdraw_no
	 * @param exter_invoke_ip
	 * @param appid
	 * @param secret
	 */
	@ResponseBody()
	@RequestMapping(value={"/payment/submitWithdrawals","/withdraw"},method={RequestMethod.GET,RequestMethod.POST})
	public void submitWithdrawals(HttpServletRequest request,HttpServletResponse response,
			String withdraw_type,String total_fee,String userId,String userName,
			String withdraw_no,String exter_invoke_ip,String appid,String secret){
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("apply withdrawals withdraw_no [%s]", withdraw_no));
		try{
    		//判断非空参数
        	if (StringUtils.isBlank(withdraw_type)) {
    			logger.error(String.format("apply withdrawals withdraw_type [%s]", withdraw_type));
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
    			return;
    		}
        	if (StringUtils.isBlank(total_fee)) {
        		logger.error(String.format("apply withdrawals total_fee[%s]", total_fee));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if (StringUtils.isBlank(userId)) {
        		logger.error(String.format("apply withdrawals userId[%s]", userId));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if (StringUtils.isBlank(withdraw_no)) {
        		logger.error(String.format("apply withdrawals withdraw_no[%s]", withdraw_no));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
    		
        	if (StringUtils.isBlank(secret)) {
        		logger.error(String.format("apply withdrawals secret[%s]", secret));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	
        	if (StringUtils.isBlank(appid)) {
        		logger.error(String.format("apply withdrawals appid[%s]", appid));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if(!appid.equals("1000")|| !"1F915A8DA370422582CBAC1DB6A806DD".equals(secret)){
        		logger.error(String.format("apply withdrawals appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_USERORPWD_ERROR)));
        		return;
			}
        	
    		PaymentWithdraw paymentWithdraw = paymentWithdrawService.findByOrderId(withdraw_no);
    		if(paymentWithdraw != null){
    			logger.error(String.format("apply withdrawals paymentWithdraw [%s]", paymentWithdraw));
        		throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST,new String[]{""}); 
        	}
        	ResponseCreateWithdrawDTO result = null;
        	//判断请求支付类型    	
        	if(withdraw_type.equals("weixin")){ //微信支付
        		result =  doWxWithdrawals(request,response,total_fee,withdraw_no,exter_invoke_ip,userId,userName);
        	}else if(withdraw_type.equals("alipay")){ //支付宝
        		logger.info(String.format("apply withdrawals withdraw_type [%s]",withdraw_type + ResponseErrorCode.RPC_MESSAGE_UNSUPPORT));
        		//result =  doAlipayWithdrawals(response,request, total_fee, withdraw_no,exter_invoke_ip);
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT)));
        		return;
        	}else{//提示暂不支持的支付方式
        		logger.info(String.format("apply withdrawals withdraw_type [%s]",withdraw_type + ResponseErrorCode.RPC_MESSAGE_UNSUPPORT));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT)));
        		return;
        	}
        	if(result != null){
        		logger.info(String.format("apply withdrawals return result [%s]",JsonHelper.getJSONString(result)));
        		SpringMVCHelper.renderJson(response, result);
        	}else{
        		logger.info(String.format("apply withdrawals return result [%s]",ResponseError.BUSINESS_ERROR));
        		SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        	}
        	
		}catch(BusinessI18nCodeException i18nex){
			logger.error(String.format("submitWithdrawals catch BusinessI18nCodeException [%s]",ResponseError.embed(i18nex)));
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			logger.error(String.format("submitWithdrawals catch Exception [%s]",ResponseError.SYSTEM_ERROR));
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	

	/**
	 * 请求支付接口
	 * @param response
	 * @param request
	 * @param total_fee 支付金额
	 * @param goods_no 支付流水号号
	 * @param payment_type 支付类型
	 * @param exter_invoke_ip 客户端IP(非必填)
	 * @param payment_completed_url 支付完成后跳转地址(支付宝必填)
	 * @param usermac(非必填)
	 * @param appid
	 * @param secret
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value={"/payment/submitPayment","/pay"},method={RequestMethod.GET,RequestMethod.POST})
    public void submitPayment(HttpServletResponse response,HttpServletRequest request,
    				String total_fee,String goods_no,String payment_type,String exter_invoke_ip,
    				String payment_completed_url,String umac,String appid,String secret){
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("apply payment goods_no [%s]", goods_no));
		
		try{
    		//判断非空参数
        	if (StringUtils.isBlank(payment_type)) {
        		logger.error(String.format("apply payment payment_type [%s]", payment_type));
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
    			return;
    		}
        	if (StringUtils.isBlank(total_fee)) {
        		logger.error(String.format("apply payment total_fee [%s]", total_fee));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if (StringUtils.isBlank(umac)) {
        		logger.error(String.format("apply payment umac [%s]", umac));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if (StringUtils.isBlank(goods_no)) {
        		logger.error(String.format("apply payment goods_no [%s]", goods_no));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
    		
    		PaymentReckoning paymentReckoning = paymentReckoningService.findByOrderId(goods_no);
        	if(paymentReckoning != null){
        		logger.error(String.format("apply payment goods_no [%s]", goods_no+ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST));
        		throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST,new String[]{""}); 
        	}
        	PaymentTypeVTO result = null;
        	
        	umac = BusinessHelper.formatMac(umac);
        	//判断请求支付类型    	
        	if(payment_type.equals("PcWeixin")){ //PC微信支付
        		result =  doNativeWxPayment(request,response,total_fee,goods_no,exter_invoke_ip,payment_completed_url,umac);
        	}else if(payment_type.equals("WapAlipay")){ //Wap微信支付宝
        		result =  doAlipay(response,request, total_fee, goods_no,payment_completed_url,exter_invoke_ip,payment_type,umac);
        	}else if(payment_type.equals("PcAlipay")){ //PC微信支付宝
        		result =  doAlipay(response,request, total_fee, goods_no,payment_completed_url,exter_invoke_ip,payment_type,umac);
        	}else if(payment_type.equals("Midas")){ //米大师
        		result =  doMidas(response, total_fee, goods_no); //TODO：暂未对接完成。。。
        	}else if(payment_type.equals("WapWeixin")){ //汇付宝
        		result =  doHee(response, total_fee, goods_no,exter_invoke_ip,payment_completed_url,umac); 
        	}else{//提示暂不支持的支付方式
        		logger.info(String.format("apply payment payment_type [%s]",payment_type + ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT))));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT)));
        		return;
        	}
        	if(result != null){
        		logger.info(String.format("apply payment return result [%s]",JsonHelper.getJSONString(result)));
        		SpringMVCHelper.renderJson(response, PaymentResponseSuccess.embed(JsonHelper.getJSONString(result)));
        	}else{
        		logger.info(String.format("apply payment return result [%s]",ResponseError.BUSINESS_ERROR));
        		SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        	}
        	
		}catch(BusinessI18nCodeException i18nex){
			logger.error(String.format("submitPayment catch BusinessI18nCodeException [%s]",ResponseError.embed(i18nex)));
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			logger.error(String.format("submitPayment catch Exception [%s]",ResponseError.SYSTEM_ERROR));
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
    }
	
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
	private ResponseCreateWithdrawDTO doWxWithdrawals(HttpServletRequest request, HttpServletResponse response, String total_fee,
			String withdraw_no, String Ip,String userId,String userName) {
		ResponseCreateWithdrawDTO result= null;
        String certificateUrl = PayHttpService.WITHDRAW_URL;
        logger.info(String.format("apply WxWithdrawals withdraw_no [%s] total_fee [%s] Ip [%s]"
        		+ " withdraw_type [%s] userId [%s]",withdraw_no,total_fee,Ip,"WDWX",userId));
        String product_name="必虎提现";//订单名称
    	total_fee = BusinessHelper.getMoney(total_fee);
        String reckoningId = createPaymentWithdraw(withdraw_no,total_fee,Ip,"WDWX",userId);

        logger.info(String.format("doWxWithdrawals reckoningId [%s] product_name [%s] total_fee [%s] ip [%s]"
        		+ "certificateUrl [%s] userId [%s] userName [%s]",reckoningId, product_name, total_fee, request.getRemoteAddr(), certificateUrl, userId,userName));
        WithDrawNotifyResponse unifiedOrderResponse = payHttpService.sendWithdraw(reckoningId, product_name, total_fee, request.getRemoteAddr(), certificateUrl, userId,userName);
        
        if(unifiedOrderResponse == null){
        	logger.error(String.format("apply payment unifiedOrderResponse [%s]", unifiedOrderResponse));
        	return result;
        }

        if(!unifiedOrderResponse.isResultSuccess()){
        	String status = unifiedOrderResponse.getResultErrorCode();
			String msg = unifiedOrderResponse.getResultMessage();
			logger.info(String.format("apply payment status [%s] msg [%s]", status,msg));
        }
        
        //收到微信的提现成功请求
        String out_trade_no = unifiedOrderResponse.getPartner_trade_no();
        String trade_no = unifiedOrderResponse.getPayment_no();
        
        PaymentWithdraw payWithdraw =  paymentWithdrawService.getById(out_trade_no);
        // 1.1 如果订单不存在则返回订单不存在
        if (payWithdraw == null) {
        	logger.error(String.format("get WxWithdrawals payWithdraw [%s] ",payWithdraw));
        	return null;
        }
        String orderId = payWithdraw.getOrderId();
        logger.info(String.format("return WxWithdrawals reckoningId [%s] trade_no [%s] orderId [%s]",out_trade_no, trade_no,orderId));
        
        //判断当前账单的实际状态，如果是以支付状态就不做处理了
		int withdrawStatus = payWithdraw.getWithdrawStatus();
		if(withdrawStatus == 0){ //0未支付;1支付成功
            if("SUCCESS".equals(unifiedOrderResponse.getReturn_code()) && "SUCCESS".equals(unifiedOrderResponse.getResult_code())){
            	 logger.info("账单流水号："+out_trade_no+"支付成功.微信返回SUCCESS.");
 				//修改成账单状态    1:已支付 2：退款已支付 3：退款成功 4：退款失败
            	 updateWithdrawalsStatus(payWithdraw, out_trade_no, trade_no);
                 result = new ResponseCreateWithdrawDTO();
             	result.setWithdraw_type("weixin");
             	result.setSuccess(true);
             	result.setUrl("");
             	return result;
            }else{
                //支付s失败
            	logger.info("支付流水号："+out_trade_no+"支付失败 修改订单的支付状态.");
		            return null;
            }
		}else{
			logger.info("账单流水号："+out_trade_no+"支付账单、订单状态状态修改成功!");
	            return null;
		}
       
	}
    
//	private PaymentTypeVTO doAlipayWithdrawals(HttpServletResponse response, HttpServletRequest request,
//		String total_fee, String withdraw_no, String exter_invoke_ip) {
//		return null;
//	}
	
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
	private PaymentTypeVTO doNativeWxPayment(HttpServletRequest request, HttpServletResponse response,String total_fee,String out_trade_no,String Ip,String locationUrl,String usermac){
		PaymentTypeVTO result= null;
        String NOTIFY_URL = PayHttpService.NOTIFY_URL;
        String product_name="打赏";//订单名称
    	total_fee = BusinessHelper.getMoney(total_fee);
        //记录请求的Goods_no
        String reckoningId = createPaymentReckoning(out_trade_no,total_fee,Ip,"PCWX",usermac);
        
      //记录请求支付完成后返回的地址
		if (!StringUtils.isBlank(locationUrl)) {
			logger.info(String.format("doNativeWxPayment locationUrl [%s] ",locationUrl));
			PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
			orderLocation.setTid(reckoningId);
			orderLocation.setLocation(locationUrl);
			paymentAlipaylocationService.insert(orderLocation);
		}

		logger.info(String.format("apply wx payment reckoningId [%s] product_name [%s] total_fee [%s] ip [%s]"
        		+ "NOTIFY_URL [%s] ",reckoningId, product_name, total_fee, request.getRemoteAddr(),NOTIFY_URL ));
        UnifiedOrderResponse unifiedOrderResponse = payHttpService.unifiedorder(reckoningId, product_name, total_fee, request.getRemoteAddr(), NOTIFY_URL, "");

        if(!unifiedOrderResponse.isResultSuccess()){
        	String status = unifiedOrderResponse.getResultErrorCode();
			String msg = unifiedOrderResponse.getResultMessage();
			logger.info(String.format("apply wx payment status [%s] msg [%s]", status,msg));
        }
        try {
        	String url= "http://qr.liantu.com/api.php?text="+ URLEncoder.encode(unifiedOrderResponse.getCode_url(), "UTF-8");
        	result = new PaymentTypeVTO();
        	result.setType("img");
        	result.setUrl(url);
        } catch (IOException e) {
        	logger.error(String.format("apply wx payment e.getCause [%s]", e.getCause()));
        }
        return result;
    }
    
    
	/**
	 *  支付宝支付请求接口(支付宝2015年8月25日新版本支付请求返回有所变化，是一个文本型)
     * @param response
     * @param request
	 * @param totalPrice 支付金额
	 * @param out_trade_no 订单号
	 * @param locationUrl 支付完成后返回页面地址
	 * @param ip 用户Ip
	 * @return
	 */
    private PaymentTypeVTO doAlipay(HttpServletResponse response,HttpServletRequest request,
    		String totalPrice,String out_trade_no,String locationUrl,String ip,String type,String usermac){
    	response.setCharacterEncoding("utf-8");
    	PaymentTypeVTO result = null;
    	
		//服务器异步通知页面路径
		String notify_url = "http://pay.bhuwifi.com/msip_bhu_payment_rest/payment/alipayNotifySuccess";
		//需http://格式的完整路径，不能加?id=123这类自定义参数

		//页面跳转同步通知页面路径
		String return_url = "http://pay.bhuwifi.com/msip_bhu_payment_rest/payment/alipayReturn";
		//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/

		//订单名称
		String subject = "打赏";//;new String("打赏".getBytes("ISO-8859-1"), "utf-8");
		//付款金额
		String total_fee = totalPrice;

		//订单描述
		String body = "必虎路由器打赏服务";

		//////////////////////////////////////////////////////////////////////////////////
			
		 //以上为正式支付前必有的订单信息，用户信息验证，接下来将用订单号生成一个支付流水号进行在线支付
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		String reckoningId = null;
		//数据库存的是分，此处需要把传来的支付金额转换成分，而传给支付宝的保持不变（默认元）
		String total_fee_fen = BusinessHelper.getMoney(total_fee);
		if(type.equals("WapAlipay")){
			reckoningId = createPaymentReckoning(out_trade_no,total_fee_fen,ip,"WPAL",usermac);
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
			sParaTemp.put("it_b_pay", "600");
		}else{
			reckoningId = createPaymentReckoning(out_trade_no,total_fee_fen,ip,"PCAL",usermac);
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
		}
		
		
		//记录请求支付完成后返回的地址
		if (!StringUtils.isBlank(locationUrl)) {
			PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
			orderLocation.setTid(reckoningId);
			orderLocation.setLocation(locationUrl);
			paymentAlipaylocationService.insert(orderLocation);
			logger.info(String.format("apply alipay set location reckoningId [%s] locationUrl [%s] insert finished.",reckoningId, locationUrl));
		}
		
		//建立支付宝支付请求
		
		//建立请求
		String sHtmlText = "";
        try {
            sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"post","确认"); 
            result = new PaymentTypeVTO();
            result.setType("http");
            result.setUrl(sHtmlText);
            return result;
        } catch (Exception e) {
            return result;
        }
	}
	
    /**
     * 处理米大师支付服务请求
     * @param response
     * @param total_fee
     * @param goods_no
     * @return
     */
    private PaymentTypeVTO doMidas(HttpServletResponse response, String total_fee, String goods_no) {
    	throw new BusinessI18nCodeException(ResponseErrorCode.RPC_MESSAGE_UNSUPPORT,new String[]{"Midas"}); 
    	//SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
		//		ResponseErrorCode.RPC_MESSAGE_UNSUPPORT)));
    	//return "";
	}
   
    /**
     * 处理汇付宝支付服务请求
     * @param response
     * @param total_fee
     * @param out_trade_no
     * @param ip
     * @return
     */
    private PaymentTypeVTO doHee(HttpServletResponse response, String total_fee, String out_trade_no,String ip,String return_url,String usermac) {
    	PaymentTypeVTO result = null;
    	if(ip == "" || ip == null){
    		ip = "213.42.3.24";
    	}
    	
    	String total_fee_fen = BusinessHelper.getMoney(total_fee);
    	
    	int temp = Integer.parseInt(total_fee_fen);
    	if(temp < 50){
    		total_fee = "0.50";
    		total_fee_fen = BusinessHelper.getMoney(total_fee);
    	}
    	
    	String reckoningId = createPaymentReckoning(out_trade_no,total_fee_fen,ip,"WPWX",usermac);
    	//记录请求支付完成后返回的地址
    	if (!StringUtils.isBlank(return_url)) {
    		logger.info(String.format("get heepay location [%s] ",return_url));
    		PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
    		orderLocation.setTid(reckoningId);
    		orderLocation.setLocation(return_url);
    		paymentAlipaylocationService.insert(orderLocation);
    		logger.info(String.format("apply heepay set location reckoningId [%s] location [%s]  insert finished.",reckoningId,return_url));
    	}
    	String url = Heepay.order(reckoningId, total_fee, ip);
    	url = url.replace("¬", "&not");
    	result = new PaymentTypeVTO();
    	result.setType("http");
    	result.setUrl(url);
    	return result;
    }
    
    /**
     * 支付宝通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
   	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/payment/alipayNotifySuccess")
   	public void alipayNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","支付宝订单支付通知",BusinessHelper.gettimestamp(),"Starting"));

        //获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		
		String isNull = request.getParameter("out_trade_no");
		if (StringUtils.isBlank(isNull)) {
			logger.error(String.format("get alipay nitify out_trade_no  [%s]", isNull));
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
			return;
		}
		//商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//支付宝交易号
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
		
		PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
		if (payReckoning == null) {
        	logger.info("get alipay notice payReckoning " +payReckoning);
        	return;
        }
		String orderId = payReckoning.getOrder_id();
        logger.info(String.format("get alipay notify reckoningId [%s] trade_no [%s] orderId [%s] trade_status [%s]",out_trade_no, trade_no,orderId,trade_status));
        
		if(AlipayNotify.verify(params)){//验证成功
	            
	            //判断当前账单的实际状态，如果是以支付状态就不做处理了
				int payStatus = payReckoning.getPay_status();
				if(payStatus == 0){ //0未支付;1支付成功
					//查询账单号对应的订单号
					if(trade_status.equals("TRADE_FINISHED")){
						logger.info(" TRADE_FINISHED success");	//请不要修改或删除
						SpringMVCHelper.renderJson(response, "success");
						return;
						//注意：
						//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
					} else if (trade_status.equals("TRADE_SUCCESS")){
						//支付成功
						logger.info("支付成功 修改订单的支付状态,TRADE_SUCCESS");
						updatePaymentStatus(payReckoning,out_trade_no,trade_no,"");
						SpringMVCHelper.renderJson(response, "success");
						return;
					}else{
						logger.info("支付失败！");	//请不要修改或删除
						SpringMVCHelper.renderJson(response, "fail");
						return;
					}
				}
			
				SpringMVCHelper.renderJson(response, "success");
				return;
				
		}else{//验证失败
			logger.info("fail");
		}
		SpringMVCHelper.renderJson(response, "success");
		return;
    }
    
    
    
    /**
     * 接收微信支付通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/payment/wxPayNotifySuccess")
    public String wxPayNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info(String.format("******[%s]********[%s]*******[%s]********","微信订单支付通知",BusinessHelper.gettimestamp(),"Starting"));

        PrintWriter out = response.getWriter();
        InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        String result = new String(outSteam.toByteArray(), "utf-8");
        PaySuccessNotifyResponse paySuccessNotifyResponse=null;
        Map<String, String> map = null;
        String out_trade_no = "";
        String thirdPartCode = "";
        try {
            logger.info(result);
            map = XMLUtil.doXMLParse(result);
            paySuccessNotifyResponse=new PaySuccessNotifyResponse();
            paySuccessNotifyResponse.setResponseContent(result);
            paySuccessNotifyResponse.setPropertyMap(map);
            
            out_trade_no = paySuccessNotifyResponse.getOut_trade_no().trim();
            thirdPartCode = paySuccessNotifyResponse.getTransaction_id().trim();
            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
          //判断非空参数
        	if (StringUtils.isBlank(out_trade_no)) {
    			logger.error("请求参数(out_trade_no)有误,不能为空");
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
    			return "error";
    		}
            PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
            // 1.1 如果订单不存在则返回订单不存在
            if (payReckoning == null) {
            	return "error";
            }
            String orderId = payReckoning.getOrder_id();
            logger.info(String.format("get wx notify reckoningId [%s] trade_no [%s] orderId [%s]",out_trade_no, thirdPartCode,orderId));
            
            //判断当前账单的实际状态，如果是以支付状态就不做处理了
			int payStatus = payReckoning.getPay_status();
			if(payStatus == 0){ //0未支付;1支付成功
				logger.info("账单流水号："+out_trade_no+"支付状态未修改,将进行修改。。。");
	            if("SUCCESS".equals(paySuccessNotifyResponse.getReturn_code()) && "SUCCESS".equals(paySuccessNotifyResponse.getResult_code())){
	            	 logger.info("账单流水号："+out_trade_no+"支付成功.微信返回SUCCESS.");
	 				//修改成账单状态    1:已支付 2：退款已支付 3：退款成功 4：退款失败
	            	 updatePaymentStatus(payReckoning,out_trade_no,thirdPartCode,"");
					return "success";
	            }else{
	                //支付s失败
	            	logger.info("支付流水号："+out_trade_no+"支付失败 修改订单的支付状态.");
  		            return "error";
	            }
			}else{
				logger.info("账单流水号："+out_trade_no+"支付账单、订单状态状态修改成功!");
		            return "error";
			}
        } catch (JDOMException e) {
            logger.info("账单流水号："+out_trade_no+"捕获到微信通知/notify_success方法的异常："+e.getMessage()+e.getCause());
            String noticeStr = "";//XMLUtil.setXML("FAIL", "");
            out.print(new ByteArrayInputStream(noticeStr.getBytes(Charset.forName("UTF-8"))));
        }
        return "success";
    }

    /********END************微信接口*********END********************/
    
    @SuppressWarnings("rawtypes")
	/**
     * 支付宝通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
   	@RequestMapping(value = "/payment/heepayNotifySuccess", method = { RequestMethod.GET,RequestMethod.POST })
   	public String heepayNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
    	logger.info(String.format("******[%s]********[%s]*******[%s]********","汇元宝通知",BusinessHelper.gettimestamp(),"Starting"));
    	//notify.html?result=1&pay_message=&agent_id=2067044&jnet_bill_no=H1605114812464AV&agent_bill_id=BHU20160511170522&pay_type=30&pay_amt=0.52&remark=&sign=93184de6bd847891e0f4b116fe3a68b4
        //获取POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		

		String goods_no = request.getParameter("agent_bill_id");
		if (StringUtils.isBlank(goods_no)) {
			logger.error(String.format("get heepay notify goods_no [%s]", goods_no));
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
			return "error";
		}
		//商户订单号
		String out_trade_no = new String(request.getParameter("agent_bill_id").getBytes("ISO-8859-1"),"UTF-8");

		//汇元交易号
		String trade_no = new String(request.getParameter("jnet_bill_no").getBytes("ISO-8859-1"),"UTF-8");

		//交易金额
		String pay_amt = new String(request.getParameter("pay_amt").getBytes("ISO-8859-1"),"UTF-8");
		
		//交易签名
		String sign = new String(request.getParameter("sign").getBytes("ISO-8859-1"),"UTF-8");
		
		//交易状态
		String trade_status = new String(request.getParameter("result").getBytes("ISO-8859-1"),"UTF-8");

		PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
		if (payReckoning == null) {
        	logger.info("get heepay notice payReckoning " +payReckoning);
        	return "error";
        }
		String orderId = payReckoning.getOrder_id();
        logger.info(String.format("get heepay notify reckoningId [%s] trade_no [%s] orderId [%s] trade_status [%s]",out_trade_no, trade_no,orderId,trade_status));
        
		String mySign = Heepay.sign(trade_no,out_trade_no,pay_amt);
		logger.info(String.format("get heepay notify to create mySign[%s] pay_amt [%s] sign [%s]",mySign, pay_amt, sign));
		if(mySign.equals(sign)){//验证成功
	            //判断当前账单的实际状态，如果是以支付状态就不做处理了
				int payStatus = payReckoning.getPay_status();
				if(payStatus == 0){ //0未支付;1支付成功
					//查询账单号对应的订单号
					 if (trade_status.equals("1")){
						//支付成功
						logger.info("支付成功 修改订单的支付状态,TRADE_SUCCESS");
						updatePaymentStatus(payReckoning,out_trade_no,trade_no,"Hee");
						return "ok";
					}else{
						//1:已支付 2：退款已支付 3：退款成功 4：退款失败
						logger.info("支付失败！");	//请不要修改或删除
						return "error";
					}
				}
				return "ok";
				
		}else{//验证失败
			logger.info(String.format("get heepay notify  mysign [%s] sign [%s] sign verify fail",mySign, sign));
			return "error";
		}
    	
    }
    
    /***************************Hee end**************************************/
    
	  /********************支付宝接口*****************************/
        
    /**
     * 支付宝通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
   	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/payment/heeReturn" , method = { RequestMethod.GET,RequestMethod.POST })
   	public void heeReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","汇元宝返回通知",BusinessHelper.gettimestamp(),"Starting"));


        //获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		
		String aa = request.getParameter("agent_bill_id");
		if (StringUtils.isBlank(aa)) {
			logger.error("请求参数(agent_bill_id)有误,不能为空");
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
			return;
		}
		//商户订单号
		String out_trade_no = new String(request.getParameter("agent_bill_id").getBytes("ISO-8859-1"),"UTF-8");
		
		if (StringUtils.isBlank(out_trade_no)) {
			logger.error(String.format("get heepay return notify out_trade_no [%s]", out_trade_no));
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
		}
		
		String locationUrl = PayHttpService.WEB_NOTIFY_URL;
		String returnUrl = paymentAlipaylocationService.getLocationByTid(out_trade_no);
		if(StringUtils.isNotBlank(returnUrl)){
			if(returnUrl.startsWith("http")){
				locationUrl = returnUrl;
			}
		}
		logger.info(String.format("get heepay return notify and go to locationUrl [%s]", locationUrl));
		response.sendRedirect(locationUrl);

	}
   	
    /**
     * 支付宝通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
   	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/payment/alipayReturn" , method = { RequestMethod.GET,RequestMethod.POST })
   	public void alipayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
   		logger.info("/alipayReturn************接收支付宝返回通知*****************************"); 

        //获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		String isNull = request.getParameter("out_trade_no");
		if (StringUtils.isBlank(isNull)) {
			logger.error(String.format("get alipayReturn out_trade_no [%s]", isNull));
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
			return;
		}
		//商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		String locationUrl = PayHttpService.WEB_NOTIFY_URL;
		String returnUrl = paymentAlipaylocationService.getLocationByTid(out_trade_no);
		if(StringUtils.isNotBlank(returnUrl)){
			if(returnUrl.startsWith("http")){
				locationUrl = returnUrl;
			}
		}
		logger.info(String.format("get alipay return notify and go to locationUrl [%s]", locationUrl));
		response.sendRedirect(locationUrl);
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
    private String createPaymentWithdraw(String out_trade_no,String total_fee,String Ip,String type,String userId){
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
    private String createPaymentReckoning(String out_trade_no,String total_fee,String Ip,String type,String usermac){
    	String paymentType = "Midas";
    	if(type.equalsIgnoreCase("PCWX")){
 			paymentType = "PcWeixin";
 		}else if(type.equalsIgnoreCase("PCAL")){
 			paymentType = "PcAlipay";
 		}else if(type.equalsIgnoreCase("WPWX")){
 			paymentType = "WapWeixin";
 		}else if(type.equalsIgnoreCase("WPAL")){
 			paymentType = "WapAlipay";
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
 		order.setOpenid("BHUUSERMAC"+usermac);
 		order.setSubject("打赏");
 		order.setExter_invoke_ip(Ip);
 		order.setAppid("1000");
 		String token = RandomPicker.randString(BusinessHelper.letters, 10);
 		order.setToken(token);
 		paymentReckoningService.insert(order);
 		logger.info(String.format("create Payment subject [%s] reckoningId [%s] "
				+ "orderId [%s] payment_type [%s] total_fee [%s] Ip [%s] openId [%s] token [%s]", "打赏",reckoningId, out_trade_no, 
				paymentType, total_fee, Ip, "BHUUSERMAC"+usermac,token));
 		return reckoningId;
    }
    
    private void updatePaymentStatus(PaymentReckoning updatePayStatus,String out_trade_no,String thirdPartCode,String thridType){
		updatePayStatus.setThird_party_code(thirdPartCode);
		updatePayStatus.setPay_status(1);
		updatePayStatus.setPaid_at(new Date());
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
     */
    private void updateWithdrawalsStatus(PaymentWithdraw updateWithdrawStatus,String out_trade_no,String thirdPartCode){
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
//    	System.out.println(JsonHelper.getJSONString(result));;
//      	SpringMVCHelper.renderJson(response, result);
    	System.out.println(BusinessHelper.formatMac("84:82:f4:28:7a:ec"));;
    }
}
