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
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentCompletedNotifyDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.payment.model.PaymentAlipaylocation;
import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.api.rpc.payment.vto.PaymentReckoningVTO;
import com.bhu.vas.api.vto.payment.PaymentTypeVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.bhu.vas.business.ds.payment.service.PaymentAlipaylocationService;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.helper.BusinessHelper;
import com.bhu.vas.business.helper.XMLUtil;
import com.bhu.vas.web.cache.BusinessCacheService;
import com.bhu.vas.web.http.response.PaySuccessNotifyResponse;
import com.bhu.vas.web.http.response.UnifiedOrderResponse;
import com.bhu.vas.web.service.PayHttpService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heepay.api.Heepay;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
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
@RequestMapping("/payment")
public class PaymentController extends BaseController{
	private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	@Resource
	PaymentReckoningService paymentReckoningService;
	@Resource
	PaymentAlipaylocationService paymentAlipaylocationService;
	@Autowired
    PayHttpService payHttpService;
	//@Autowired
	//LocalEhcacheService localEhcacheService;
	@Autowired
	BusinessCacheService businessCacheService;
	
	@ResponseBody()
	@RequestMapping(value="/submitWithdrawals",method={RequestMethod.GET,RequestMethod.POST})
	public void submitWithdrawals(HttpServletRequest request,HttpServletResponse response,
			String withdraw_type,String total_fee,String userId,String userName,
			String withdraw_no,String exter_invoke_ip,String appid,String secret){
		logger.info(String.format(" query Withdrawals order[%s]", withdraw_no));
		
		try{
    		//判断非空参数
        	if (StringUtils.isBlank(withdraw_type)) {
    			logger.error("请求参数(withdraw_type)有误,不能为空");
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
    			return;
    		}
        	if (StringUtils.isBlank(total_fee)) {
        		logger.error("请求参数(total_fee)有误,不能为空");
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if (StringUtils.isBlank(userId)) {
        		logger.error("请求参数(userId)有误,不能为空");
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if (StringUtils.isBlank(withdraw_no)) {
        		logger.error("请求参数(withdraw_no)有误,不能为空");
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
    		
    		PaymentReckoning paymentReckoning = paymentReckoningService.findByOrderId(withdraw_no);
        	if(paymentReckoning != null){
        		throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST,new String[]{""}); 
        	}
        	PaymentTypeVTO result = null;
        	//判断请求支付类型    	
        	if(withdraw_type.equals("weixin")){ //微信支付
        		result =  doWxWithdrawals(request,response,total_fee,withdraw_no,exter_invoke_ip,userId,userName);
        	}else if(withdraw_type.equals("alipay")){ //支付宝
        		//result =  doAlipayWithdrawals(response,request, total_fee, withdraw_no,exter_invoke_ip);
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT)));
        		return;
        	}else{//提示暂不支持的支付方式
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT)));
        		return;
        	}
        	if(result != null){
        		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
        	}else{
        		SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        	}
        	
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
//	private PaymentTypeVTO doAlipayWithdrawals(HttpServletResponse response, HttpServletRequest request,
//			String total_fee, String withdraw_no, String exter_invoke_ip) {
//		return null;
//	}

	private PaymentTypeVTO doWxWithdrawals(HttpServletRequest request, HttpServletResponse response, String total_fee,
			String withdraw_no,String userId,String userName, String Ip) {
		PaymentTypeVTO result= null;
        String NOTIFY_URL = PayHttpService.PAY_HOST_URL +"/wxPayNotifySuccess";
        String product_name="必虎提现";//订单名称
    	total_fee = BusinessHelper.getMoney(total_fee);
        //记录请求的Goods_no
        String reckoningId = createPaymentReckoning(withdraw_no,total_fee,Ip,"WXWD");
        System.out.println("NOTIFY_URL:"+NOTIFY_URL);

        //UnifiedOrderResponse unifiedOrderResponse = payHttpService.unifiedorder(reckoningId, product_name, total_fee, request.getRemoteAddr(), NOTIFY_URL, "");
        UnifiedOrderResponse unifiedOrderResponse = payHttpService.sendWithdraw(reckoningId, product_name, total_fee, request.getRemoteAddr(), NOTIFY_URL, userId,userName);
        
        System.out.println(payHttpService.getAppId());

        if(!unifiedOrderResponse.isResultSuccess()){
        	String status = unifiedOrderResponse.getResultErrorCode();
			String msg = unifiedOrderResponse.getResultMessage();
			logger.info(status+msg);
        }
        try {
        	String url= "http://qr.liantu.com/api.php?text="+ URLEncoder.encode(unifiedOrderResponse.getCode_url(), "UTF-8");
        	result = new PaymentTypeVTO();
        	result.setType("img");
        	result.setUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
	}

	/**
	 * 查询支付流水号接口
	 * @param request
	 * @param response
	 * @param orderid 请求支付流水号
	 */
	@ResponseBody()
	@RequestMapping(value="/queryOrderPayStatus",method={RequestMethod.GET,RequestMethod.POST})
	public void queryPaymentOrder(HttpServletRequest request,HttpServletResponse response,String goods_no,String exter_invoke_ip,String appid,String secret){
		logger.info(String.format(" query_paymentorder order[%s]", goods_no));
		
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
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(payReckoningVTO));
				return;
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.COMMON_DATA_NOTEXIST)));
				return;
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
	 * 支付请求接口
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
   	@RequestMapping(value = "/submitPayment", method = { RequestMethod.GET,RequestMethod.POST })
    public void submitPayment(HttpServletResponse response,HttpServletRequest request,
    				String total_fee,String goods_no,String payment_type,String exter_invoke_ip,
    				String payment_completed_url,String usermac,String appid,String secret){
    	try{
    		//判断非空参数
        	if (StringUtils.isBlank(payment_type)) {
    			logger.error("请求参数(payment_type)有误,不能为空");
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
    			return;
    		}
        	if (StringUtils.isBlank(total_fee)) {
        		logger.error("请求参数(total_fee)有误,不能为空");
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if (StringUtils.isBlank(goods_no)) {
        		logger.error("请求参数(goods_no)有误,不能为空");
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
    		
    		PaymentReckoning paymentReckoning = paymentReckoningService.findByOrderId(goods_no);
        	if(paymentReckoning != null){
        		throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST,new String[]{""}); 
        	}
        	PaymentTypeVTO result = null;
        	//判断请求支付类型    	
        	if(payment_type.equals("PcWeixin")){ //PC微信支付
        		result =  doNativeWxPayment(request,response,total_fee,goods_no,exter_invoke_ip);
        	}else if(payment_type.equals("PcAlipay")){ //PC微信支付宝
        		result =  doAlipay(response,request, total_fee, goods_no,payment_completed_url,exter_invoke_ip);
        	}else if(payment_type.equals("Midas")){ //米大师
        		result =  doMidas(response, total_fee, goods_no); //TODO：暂未对接完成。。。
        	}else if(payment_type.equals("Hee")){ //汇付宝
        		result =  doHee(response, total_fee, goods_no,exter_invoke_ip); 
        	}else{//提示暂不支持的支付方式
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT)));
        		return;
        	}
        	if(result != null){
        		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
        	}else{
        		SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
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
     * 处理微信扫描支付请求
     * @param request
     * @param response
     * @param totalPrice
     * @param goodsNo
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
	private PaymentTypeVTO doNativeWxPayment(HttpServletRequest request, HttpServletResponse response,String total_fee,String out_trade_no,String Ip){
		PaymentTypeVTO result= null;
        String NOTIFY_URL = PayHttpService.PAY_HOST_URL +"/wxPayNotifySuccess";
        String product_name="打赏";//订单名称
    	total_fee = BusinessHelper.getMoney(total_fee);
        //记录请求的Goods_no
        String reckoningId = createPaymentReckoning(out_trade_no,total_fee,Ip,"PCWX");
        System.out.println("NOTIFY_URL:"+NOTIFY_URL);

        UnifiedOrderResponse unifiedOrderResponse = payHttpService.unifiedorder(reckoningId, product_name, total_fee, request.getRemoteAddr(), NOTIFY_URL, "");
        System.out.println(payHttpService.getAppId());

        if(!unifiedOrderResponse.isResultSuccess()){
        	String status = unifiedOrderResponse.getResultErrorCode();
			String msg = unifiedOrderResponse.getResultMessage();
			logger.info(status+msg);
        }
        try {
        	String url= "http://qr.liantu.com/api.php?text="+ URLEncoder.encode(unifiedOrderResponse.getCode_url(), "UTF-8");
        	result = new PaymentTypeVTO();
        	result.setType("img");
        	result.setUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    
    /**
     * 支付宝支付请求接口(支付宝8月25日新版本支付请求返回有所变化，不在是一个文本文件型)
     * @param response
     * @param subjectStr 支付名称
     * @param amount 支付金额
     * @param orderId 订单号
     * @return
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    private PaymentTypeVTO doAlipay(HttpServletResponse response,HttpServletRequest request,String totalPrice,String out_trade_no,String locationUrl,String ip){
    	response.setCharacterEncoding("utf-8");
    	PaymentTypeVTO result = null;
    	
		//服务器异步通知页面路径
		String notify_url = "http://pay.bhuwifi.com/msip_bhu_payment_rest/payment/alipayNotifySuccess";
		//需http://格式的完整路径，不能加?id=123这类自定义参数

		//页面跳转同步通知页面路径
		String return_url = "www.bhuwifi.com";
		//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/

		//订单名称
		String subject = "打赏";//;new String("打赏".getBytes("ISO-8859-1"), "utf-8");
		//付款金额
		String total_fee = totalPrice;

		//订单描述
		String body = "BHUwifi service";

		//////////////////////////////////////////////////////////////////////////////////
			
		 //以上为正式支付前必有的订单信息，用户信息验证，接下来将用订单号生成一个支付流水号进行在线支付
		//数据库存的是分，此处需要把传来的支付金额转换成分，而传给支付宝的保持不变（默认元）
		String total_fee_fen = BusinessHelper.getMoney(total_fee);
		String reckoningId = createPaymentReckoning(out_trade_no,total_fee_fen,ip,"PCAL");
		
		//记录请求支付完成后返回的地址
		if (!StringUtils.isBlank(locationUrl)) {
			logger.error("请求参数(locationUrl)不为空");
			PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
			orderLocation.setTid(reckoningId);
			orderLocation.setLocation(locationUrl);
			paymentAlipaylocationService.insert(orderLocation);
			return_url = locationUrl;
		}
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", AlipayConfig.service);
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("seller_id", AlipayConfig.seller_id);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", AlipayConfig.payment_type);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("anti_phishing_key", AlipayConfig.anti_phishing_key);
		sParaTemp.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
		sParaTemp.put("out_trade_no", reckoningId);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("body", body);
		
		//建立支付宝支付请求
		
		//建立请求
		String sHtmlText = "";
        try {
            sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"post","确认");  
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String s = gson.toJson(sHtmlText);
            request.setAttribute("sHtmlText", s);
            logger.info("文本："+sHtmlText);
            logger.info("gson："+s);
            result = new PaymentTypeVTO();
            result.setType("text");
            result.setUrl(sHtmlText);
            //BusinessHelper.writeToWeb(result, "html", response);
            return result;
        } catch (Exception e) {
            if(logger.isErrorEnabled()){
                logger.error("订单号："+out_trade_no+"流水号："+reckoningId+"支付请求返回参数："+sHtmlText);
            }
            //BusinessHelper.writeToWeb(result, "html", response);
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
    private PaymentTypeVTO doHee(HttpServletResponse response, String total_fee, String out_trade_no,String ip) {
    	PaymentTypeVTO result = null;
    	if(ip == "" || ip == null){
    		ip = "213.42.3.24";
    	}
    	String total_fee_fen = BusinessHelper.getMoney(total_fee);
    	//记录请求的Goods_no
    	String reckoningId = createPaymentReckoning(out_trade_no,total_fee_fen,ip,"MOHE");
    	String url = Heepay.order(reckoningId, total_fee, ip);
    	System.out.println("url:"+url);
    	url = url.replace("¬", "&not");
    	result = new PaymentTypeVTO();
    	result.setType("http");
    	result.setUrl(url);
    	return result;
    }
    
    /**
     * 接收微信支付通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/wxPayNotifySuccess")
    public String wxPayNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("/wxPayNotifySuccess******************收到微信订单支付通知***********************");

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
            logger.info("获取微信通知信息:"+result);
            map = XMLUtil.doXMLParse(result);
            paySuccessNotifyResponse=new PaySuccessNotifyResponse();
            paySuccessNotifyResponse.setResponseContent(result);
            paySuccessNotifyResponse.setPropertyMap(map);
            
            out_trade_no = paySuccessNotifyResponse.getOut_trade_no().trim();
            thirdPartCode = paySuccessNotifyResponse.getTransaction_id().trim();
            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
            PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
            String isShow = "存   在  ...";
            // 1.1 如果订单不存在则返回订单不存在
            if (payReckoning == null) {
            	isShow = " 不   存   在  ...";
            	logger.info("查询账单流水号："+out_trade_no+isShow);
            	return "error";
            }
            String orderId = payReckoning.getOrder_id();
            logger.info("支付订单号："+orderId+"查询账单流水号："+out_trade_no+isShow);
            
            //判断当前账单的实际状态，如果是以支付状态就不做处理了
			int payStatus = payReckoning.getPay_status();
			if(payStatus == 0){ //0未支付;1支付成功
				logger.info("账单流水号："+out_trade_no+"支付状态未修改,将进行修改。。。");
	            if("SUCCESS".equals(paySuccessNotifyResponse.getReturn_code()) && "SUCCESS".equals(paySuccessNotifyResponse.getResult_code())){
	            	 logger.info("账单流水号："+out_trade_no+"支付成功.微信返回SUCCESS.");
	 				//修改成账单状态    1:已支付 2：退款已支付 3：退款成功 4：退款失败
	            	 updatePaymentStatus(payReckoning,out_trade_no,thirdPartCode);
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
   	@RequestMapping(value = "/heepayNotifySuccess", method = { RequestMethod.GET,RequestMethod.POST })
   	public String heepayNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
    	
    	logger.info("/heepayNotifySuccess************接收汇元宝通知*****************************"); 

    	//notify.html?result=1&pay_message=&agent_id=2067044&jnet_bill_no=H1605114812464AV&agent_bill_id=BHU20160511170522&pay_type=30&pay_amt=0.52&remark=&sign=93184de6bd847891e0f4b116fe3a68b4
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
		
		//获取通知返回参数，可参考技术文档中页面跳转同步通知参数列表
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
		

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		logger.info("账单号："+out_trade_no+"修改账单，订单的支付状态。。。。");
		String mySign = Heepay.sign(trade_no,out_trade_no,pay_amt);
		System.out.println("-------sign:"+sign+"=======mySign:"+mySign);
		if(mySign.equals(sign)){//验证成功
			 PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
	            String isShow = "存   在  ...";
	            // 1.1 如果订单不存在则返回订单不存在
	            if (payReckoning == null) {
	            	isShow = " 不   存   在  ...";
	            	logger.info("查询账单流水号："+out_trade_no+isShow);
	            	return "查询账单流水号："+out_trade_no+isShow;
	            }
	            logger.info("查询账单流水号："+out_trade_no+isShow);
	            
	            //判断当前账单的实际状态，如果是以支付状态就不做处理了
				int payStatus = payReckoning.getPay_status();
				if(payStatus == 0){ //0未支付;1支付成功
					//查询账单号对应的订单号
					 if (trade_status.equals("1")){
						//支付成功
						logger.info("支付成功 修改订单的支付状态,TRADE_SUCCESS");
						updatePaymentStatus(payReckoning,out_trade_no,trade_no);
						return "OK";
					}else{
						//支付s失败
						logger.info("支付失败 修改订单的支付状态");
						//1:已支付 2：退款已支付 3：退款成功 4：退款失败
						logger.info("支付失败！");	//请不要修改或删除
						return "error";
					}
				}else{
					logger.info("账单支付状态修改，通知完成。");
				}
			
			return "OK";
				
		}else{//验证失败
			logger.info("sign verify fail");
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
	@RequestMapping(value = "/alipayNotifySuccess")
   	public String alipayNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
    	
   		logger.info("/alipayNotifySuccess************接收支付宝通知*****************************"); 

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
		//商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//支付宝交易号
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		logger.info("账单号："+out_trade_no+"修改账单，订单的支付状态。。。。");
		if(AlipayNotify.verify(params)){//验证成功
			 PaymentReckoning payReckoning =  paymentReckoningService.findByReckoningId(out_trade_no);
	            String isShow = "存   在  ...";
	            // 1.1 如果订单不存在则返回订单不存在
	            if (payReckoning == null) {
	            	isShow = " 不   存   在  ...";
	            	logger.info("查询账单流水号："+out_trade_no+isShow);
	            	return "查询账单流水号："+out_trade_no+isShow;
	            }
	            logger.info("查询账单流水号："+out_trade_no+isShow);
	            
	            //判断当前账单的实际状态，如果是以支付状态就不做处理了
				int payStatus = payReckoning.getPay_status();
				if(payStatus == 0){ //0未支付;1支付成功
					//查询账单号对应的订单号
					if(trade_status.equals("TRADE_FINISHED")){
						logger.info(" TRADE_FINISHED success");	//请不要修改或删除
						return " TRADE_FINISHED success";
						//注意：
						//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
					} else if (trade_status.equals("TRADE_SUCCESS")){
						//支付成功
						logger.info("支付成功 修改订单的支付状态,TRADE_SUCCESS");
						updatePaymentStatus(payReckoning,out_trade_no,trade_no);
						return "SUCCESS";
						//注意：
						//付款完成后，支付宝系统发送该交易状态通知
					}else{
						//支付s失败
						logger.info("支付失败 修改订单的支付状态");
						//1:已支付 2：退款已支付 3：退款成功 4：退款失败
						logger.info("支付失败！");	//请不要修改或删除
						return "SUCCESS";
					}
				}
			
			return "SUCCESS";
				
		}else{//验证失败
			logger.info("fail");
		}
		return "SUCCESS";
    }
    
    /**
     * 第一次请求来的订单号，默认入库
     * @param out_trade_no 请求的订单号
     * @param total_fee 支付金额
     * @param type 支付类型
     * @return 支付流水号
     */
    private String createPaymentReckoning(String out_trade_no,String total_fee,String Ip,String type){
    	String paymentType = "Midas";
    	if(type.equalsIgnoreCase("PCWX")){
 			paymentType = "PcWeixin";
 		}else if(type.equalsIgnoreCase("PCAL")){
 			paymentType = "PcAlipay";
 		}else if(type.equalsIgnoreCase("MOHE")){
 			paymentType = "WapWeixin";
 		}
    	
    	if(Ip == "" || Ip == null){
    		Ip = "213.42.3.24";
    	}
    	
    	PaymentReckoning order = new PaymentReckoning();
 		String reckoningId = BusinessHelper.generatePaymentReckoningNoByType(type);
 		order.setId(reckoningId);
 		order.setOrder_id(out_trade_no);
 		order.setAmount(Integer.parseInt(total_fee));
 		order.setPayment_type(paymentType);
 		order.setOpenid("BHUUSERMAC000000000000");
 		order.setSubject("打赏");
 		order.setExter_invoke_ip(Ip);
 		order.setAppid("1000");
 		order.setToken(RandomPicker.randString(BusinessHelper.letters, 10));
 		paymentReckoningService.insert(order);
 		
 		return reckoningId;
    }
    
    
    private void updatePaymentStatus(PaymentReckoning updatePayStatus,String out_trade_no,String thirdPartCode){
		updatePayStatus.setThird_party_code(thirdPartCode);
		updatePayStatus.setPay_status(1);
		updatePayStatus.setPaid_at(new Date());
 		paymentReckoningService.update(updatePayStatus);//.updateByReckoningId(updatePayStatus);
 		
 		logger.info("订单："+updatePayStatus.getOrder_id()+";支付流水号："+out_trade_no+"支付状态已修改完成.");
 		
 		//通知订单
 		PaymentReckoning payNotice =  paymentReckoningService.getById(out_trade_no);
 		ResponsePaymentCompletedNotifyDTO rpcn_dto = new ResponsePaymentCompletedNotifyDTO();
 		rpcn_dto.setSuccess(true);
 		rpcn_dto.setOrderid(payNotice.getOrder_id());
 		rpcn_dto.setPayment_type(payNotice.getPayment_type());
 		String fmtDate = BusinessHelper.formatDate(payNotice.getPaid_at(), "yyyy-MM-dd HH:mm:ss");
 		rpcn_dto.setPaymented_ds(fmtDate);
 		String notify_message = JsonHelper.getJSONString(rpcn_dto);
 		System.out.println(notify_message);
 		CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(notify_message);
 		
 		logger.info("订单："+payNotice.getOrder_id()+";支付流水号："+out_trade_no+"已写入Redis.");
 		
 		//修改订单的通知状态
 		//PaymentReckoning updatePayNoticeStatus = new PaymentReckoning();
 		//updatePayNoticeStatus.setReckoning_id(out_trade_no);
 		updatePayStatus.setNotify_status(1);
 		updatePayStatus.setNotify_at(new Date());
 		paymentReckoningService.update(updatePayStatus);//.updateByReckoningId(updatePayStatus);
 		
 		logger.info("订单："+payNotice.getOrder_id()+";支付流水号："+out_trade_no+"通知状态已修改完成.");
 		
 		PaymentReckoningVTO payOrderCache = updatePaymentCache(payNotice.getOrder_id(),out_trade_no);
		
		if(payOrderCache != null){
			logger.info("订单："+payNotice.getOrder_id()+";支付流水号："+out_trade_no+"状态已写入缓存.");
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
    	System.out.println(RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR));
    	//ResponseError.embed();
		
    }
}
