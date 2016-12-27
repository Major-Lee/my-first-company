package com.bhu.vas.web.payment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreateWithdrawDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.payment.model.PaymentAlipaylocation;
import com.bhu.vas.api.rpc.payment.model.PaymentOutimeErrorLog;
import com.bhu.vas.api.rpc.payment.model.PaymentParameter;
import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.api.rpc.payment.model.PaymentWithdraw;
import com.bhu.vas.api.rpc.payment.vto.PaymentReckoningVTO;
import com.bhu.vas.api.vto.payment.PaymentTypeVTO;
import com.bhu.vas.business.ds.payment.service.PaymentAlipaylocationService;
import com.bhu.vas.business.ds.payment.service.PaymentOutimeErrorLogService;
import com.bhu.vas.business.ds.payment.service.PaymentParameterService;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.ds.payment.service.PaymentWithdrawService;
import com.bhu.vas.business.helper.BusinessHelper;
import com.bhu.vas.business.helper.PaymentChannelCode;
import com.bhu.vas.business.helper.XMLUtil;
import com.bhu.vas.business.qqmail.SendMailHelper;
import com.bhu.vas.web.http.PostRequestUtil;
import com.bhu.vas.web.http.response.AppUnifiedOrderResponse;
import com.bhu.vas.web.http.response.PaySuccessNotifyResponse;
import com.bhu.vas.web.http.response.UnifiedOrderResponse;
import com.bhu.vas.web.http.response.WithDrawNotifyResponse;
import com.bhu.vas.web.model.MidasRespone;
import com.bhu.vas.web.service.PayHttpService;
import com.bhu.vas.web.service.PayLogicService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.zxing.WriterException;
import com.heepay.api.Heepay;
import com.midas.api.MidasUtils;
import com.nowpay.config.NowpayConfig;
import com.nowpay.core.NowpaySubmit;
import com.nowpay.sign.MD5Facade;
import com.nowpay.util.FormDateReportConvertor;
import com.nowpay.util.UtilDate;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.PaymentResponseSuccess;
import com.smartwork.msip.jdo.Response;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.smartwork.msip.localunit.RandomPicker;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

/**
 * Pay支付控制层
 * @author Pengyu
 *
 */
@Controller
public class PaymentController extends BaseController{
	private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	private int weixinWithdrawWarningCount;
    private String weixinWithdrawWarningTime = "";
    private String weixinWithdrawErrorTime = "";    
    private int weixinNativeWarningCount;
    private String weixinNativeWarningTime = "";
    private String weixinNativeErrorTime = "";	
    private int weixinAppWarningCount;
    private String weixinAppWarningTime = "";
    private String weixinAppErrorTime = "";    
    private int midasWarningCount;
    private String midasWarningTime = "";
    private String midasErrorTime = "";    
    private int alipayWarningCount;
	private String alipayWarningTime = "";
	private String alipayErrorTime = "";	
    private int nowWarningCount;
    private String nowWarningTime = "";
    private String nowErrorTime = "";    
    private int heeWarningCount;
	private String heeWarningTime = "";
	private String heeErrorTime = "";

	@Autowired
	PayLogicService payLogicService;
	@Autowired
    PayHttpService payHttpService;
	@Resource
	PaymentReckoningService paymentReckoningService;
	@Resource
	PaymentWithdrawService paymentWithdrawService;
	@Resource
	PaymentAlipaylocationService paymentAlipaylocationService;
	@Resource
	PaymentOutimeErrorLogService paymentOutimeErrorLogService;
	@Resource
	PaymentParameterService paymentParameterService;
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
		logger.info(String.format("query payment order status [%s]", goods_no));
		try{
			PaymentReckoningVTO order = payLogicService.findPayStatusByOrderId(goods_no);
			if(order != null){
				logger.info(String.format("query payment order status success result [%s]",JsonHelper.getJSONString(order)));
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(order));
				return;
			}else{
				logger.info(String.format("query payment order status error result [%s]", ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST));
	        	throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST,new String[]{"商品数据不存在"}); 
			}
		}catch(BusinessI18nCodeException i18nex){
			SendMailHelper.doSendMail(3,"queryOrderPayStatus接口："+i18nex.getMessage()+i18nex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"queryOrderPayStatus接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
	/**
	 * 模拟通知商品中心打赏成功接口
	 * @param request
	 * @param response
	 * @param orderid 请求支付流水号
	 */
	@ResponseBody()
	@RequestMapping(value={"/payment/updatePaymentStatus","/updatePaymentStatus"},method={RequestMethod.GET,RequestMethod.POST})
	public void updateOrderPayStatus(HttpServletRequest request,HttpServletResponse response,String goods_no,String exter_invoke_ip,String appid,String secret){
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("update_payment order status [%s]", goods_no));
		try{
			//判断非空参数
        	if (StringUtils.isBlank(secret)) {
        		logger.error(String.format("apply payment secret[%s]", secret));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if (StringUtils.isBlank(appid)) {
        		logger.error(String.format("apply payment appid[%s]", appid));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	
			int appId = Integer.parseInt(appid);
			boolean isAllowedBusiness = BusinessEnumType.CommdityApplication.verifyed(appId, secret);
			if(isAllowedBusiness){
				boolean flag = false;
				CommdityApplication app = BusinessEnumType.CommdityApplication.fromKey(appId);
				switch(app){
    			case DEFAULT:
    				logger.info(String.format("apply  DEFAULT payment appid[%s] secret[%s]", appid,secret));
    				flag = true;
    				break;
    			default:
    				break;
				}
				if(!flag){
					logger.info(String.format("apply payment appid [%s] is invaild.", appid));
            		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
        					ResponseErrorCode.USER_APPID_UNSUPPORT)));
					return;
				}
			}else{
				logger.error(String.format("apply payment appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_USERORPWD_ERROR)));
				return;
			}
			
			PaymentReckoning order = paymentReckoningService.findByOrderId(goods_no);
			if(order == null){
				logger.info(String.format("query payment order status error result [%s]", ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST));
	        	throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST,new String[]{"商品数据不存在"}); 
			}
			String paymentSubjectType  = order.getSubject();
			if(!paymentSubjectType.equals("打赏")){
				logger.info(String.format("apply payment Subject [%s]",paymentSubjectType));
	    		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.INVALID_COMMDITY_ORDERID_UNSUPPORT)));
				return;
			}
			
			PaymentReckoningVTO paymentVTO = payLogicService.updatePaymentStatusByOrderId(order);
			if(paymentVTO != null){
				logger.info(String.format("update payment order status success result [%s]", JsonHelper.getJSONString(paymentVTO)));
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(paymentVTO));
				return;
			}else{
				logger.info(String.format("update payment order status error result [%s]", ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST));
	        	throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST,new String[]{"商品数据不存在"}); 
			}
		}catch(BusinessI18nCodeException i18nex){
			SendMailHelper.doSendMail(3,"updatePaymentStatus接口："+i18nex.getMessage()+i18nex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"updatePaymentStatus接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
	@RequestMapping(value={"/payment/change","/change"},method={RequestMethod.GET,RequestMethod.POST})
	public void updateChangeRate(HttpServletRequest request,
			HttpServletResponse response,
			int level,
			String value,
			String rate,
			String appid,
			String secret){
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("updateChangeRate level[%s] value [%s] value [%s]", level,value,rate));
		try{
			//判断非空参数
			if (StringUtils.isBlank(level+"")) {
				logger.error(String.format("apply payment level[%s]", level));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
				return;
			}
			if (StringUtils.isBlank(value)) {
				logger.error(String.format("apply payment value[%s]", value));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
				return;
			}
			if (StringUtils.isBlank(rate)) {
				logger.error(String.format("apply payment rate[%s]", rate));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
				return;
			}
			if (StringUtils.isBlank(secret)) {
				logger.error(String.format("apply payment secret[%s]", secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
				return;
			}
			if (StringUtils.isBlank(appid)) {
				logger.error(String.format("apply payment appid[%s]", appid));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
				return;
			}
			
			int appId = Integer.parseInt(appid);
			boolean isAllowedBusiness = BusinessEnumType.CommdityApplication.verifyed(appId, secret);
			if(isAllowedBusiness){
				boolean flag = false;
				CommdityApplication app = BusinessEnumType.CommdityApplication.fromKey(appId);
				switch(app){
				case DEFAULT:
					logger.info(String.format("apply  DEFAULT payment appid[%s] secret[%s]", appid,secret));
					flag = true;
					break;
				default:
					break;
				}
				if(!flag){
					logger.info(String.format("apply payment appid [%s] is invaild.", appid));
					SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
							ResponseErrorCode.USER_APPID_UNSUPPORT)));
					return;
				}
			}else{
				logger.error(String.format("apply payment appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.VALIDATE_USERORPWD_ERROR)));
				return;
			}
			/////
			
			PaymentParameter paymentParameter = paymentParameterService.findByName("WAP_WEIXIN");
			//PaymentParameter paymentParameter = paymentParameterService.findByName("WAP_WEI_XIN");
			paymentParameter.setStatus(level);
	    	paymentParameter.setUpdated_at(new Date());
	    	paymentParameter.setValue(value);
	    	paymentParameter.setCharge_rate(rate);
	    	PaymentParameter paymentVTO =paymentParameterService.update(paymentParameter);
	    	if(paymentVTO != null){
				logger.info(String.format("update_payment order status success result [%s]", JsonHelper.getJSONString(paymentVTO)));
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(paymentVTO));
				return;
			}else{
				logger.info(String.format("update_payment order status error result [%s]", ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST));
			}
		}catch(BusinessI18nCodeException i18nex){
			SendMailHelper.doSendMail(3,"updateChangeRate接口："+i18nex.getMessage()+i18nex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"updateChangeRate接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
	@RequestMapping(value={"/payment/judgmentChannels","/judgmentChannels"},method={RequestMethod.GET,RequestMethod.POST})
	public void judgmentChannels(HttpServletRequest request,
			HttpServletResponse response,
			String ua,
			String appid,
			String secret){
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("judgmentChannels ua[%s]", ua));
		try{
			//判断非空参数
			if (StringUtils.isBlank(ua)) {
				 ua = request.getHeader("User-Agent"); 
			}
			if (StringUtils.isBlank(secret)) {
				logger.error(String.format("apply payment judgmentChannels secret[%s]", secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
				return;
			}
			if (StringUtils.isBlank(appid)) {
				logger.error(String.format("apply payment judgmentChannels appid[%s]", appid));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
				return;
			}
			
			int appId = Integer.parseInt(appid);
			boolean isAllowedBusiness = BusinessEnumType.CommdityApplication.verifyed(appId, secret);
			if(isAllowedBusiness){
				boolean flag = false;
				CommdityApplication app = BusinessEnumType.CommdityApplication.fromKey(appId);
				switch(app){
				case DEFAULT:
					logger.info(String.format("apply  DEFAULT payment appid[%s] secret[%s]", appid,secret));
					flag = true;
					break;
				default:
					break;
				}
				if(!flag){
					logger.info(String.format("apply payment appid [%s] is invaild.", appid));
					SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
							ResponseErrorCode.USER_APPID_UNSUPPORT)));
					return;
				}
			}else{
				logger.error(String.format("apply payment judgmentChannels appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.VALIDATE_USERORPWD_ERROR)));
				return;
			}
			System.out.println("当前用户手机浏览器信息："+ua);
	    	UserAgent userAgent = UserAgent.parseUserAgentString(ua);  
			Browser browser = userAgent.getBrowser(); 
			Version browserVersion = userAgent.getBrowserVersion();
			OperatingSystem os = userAgent.getOperatingSystem();
			System.out.println("browserVersion:"+browserVersion.getVersion());
			System.out.println("browserName:"+browser.getName());
			System.out.println("browserType:"+browser.getBrowserType());
			System.out.println("browser ManufacturerName:"+browser.getManufacturer());
			System.out.println("browser RenderingEngine:"+browser.getRenderingEngine());
			System.out.println("OS name:"+ os.getName());
			System.out.println("OS isMobileDevice:"+os.isMobileDevice());
			System.out.println("OS ID:"+os.getId());
			System.out.println("OS DeviceType:"+os.getDeviceType());
			System.out.println("OS Manufacturer:"+os.getManufacturer());
		}catch(BusinessI18nCodeException i18nex){
			SendMailHelper.doSendMail(3,"judgmentChannels接口："+i18nex.getMessage()+i18nex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"judgmentChannels接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
	/**
	 * 模拟通知商品中心充值成功接口
	 * @param request
	 * @param response
	 * @param orderid 请求支付流水号
	 */
	@ResponseBody()
	@RequestMapping(value={"/payment/updatePrepaidStatus","/updatePrepaidStatus"},method={RequestMethod.GET,RequestMethod.POST})
	public void updatePrepaidStatus(HttpServletRequest request,HttpServletResponse response,String goods_no,String exter_invoke_ip,String appid,String secret){
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("update_payment prepaid order status [%s]", goods_no));
		try{
        	if (StringUtils.isBlank(secret)) {
        		logger.error(String.format("apply prepaid secret[%s]", secret));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	if (StringUtils.isBlank(appid)) {
        		logger.error(String.format("apply prepaid appid[%s]", appid));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	
			int appId = Integer.parseInt(appid);
			boolean isAllowedBusiness = BusinessEnumType.CommdityApplication.verifyed(appId, secret);
			if(isAllowedBusiness){
				boolean flag = false;
				CommdityApplication app = BusinessEnumType.CommdityApplication.fromKey(appId);
				switch(app){
    			case BHU_PREPAID_BUSINESS:
    				logger.info(String.format("apply  BHU_TIP_BUSINESS prepaid appid[%s] secret[%s]", appid,secret));
    				flag = true;
    				break;
    			default:
    				break;
				}
				if(!flag){
					logger.info(String.format("apply prepaid appid [%s] is invaild.", appid));
            		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
        					ResponseErrorCode.USER_APPID_UNSUPPORT)));
					return;
				}
			}else{
				logger.error(String.format("apply prepaid appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_USERORPWD_ERROR)));
				return;
			}
			
			PaymentReckoning order = paymentReckoningService.findByOrderId(goods_no);
			if(order == null){
				logger.info(String.format("query payment order status error result [%s]", ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST));
	        	throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST,new String[]{"商品数据不存在"}); 
			}
			
			String paymentSubjectType  = order.getSubject();
			if(!paymentSubjectType.equals("虎钻")){
				logger.info(String.format("apply payment Subject [%s]",paymentSubjectType));
	    		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.INVALID_COMMDITY_ORDERID_UNSUPPORT)));
				return;
			}
			
			PaymentReckoningVTO paymentVTO = payLogicService.updatePaymentStatusByOrderId(order);
			if(paymentVTO != null){
				logger.info(String.format("update_payment order status success result [%s]", JsonHelper.getJSONString(paymentVTO)));
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(paymentVTO));
				return;
			}else{
				logger.info(String.format("update_payment order status error result [%s]", ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST));
	        	throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST,new String[]{"商品数据不存在"}); 
			}
		}catch(BusinessI18nCodeException i18nex){
			SendMailHelper.doSendMail(3,"updatePrepaidStatus接口："+i18nex.getMessage()+i18nex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"updatePrepaidStatus接口："+ex.getMessage()+ex.getCause());
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
	public void submitWithdrawals(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String appid,
			@RequestParam(required = true) String secret,
			@RequestParam(required = true) String withdraw_type,
			@RequestParam(required = true) String total_fee,
			@RequestParam(required = true) String userId,
			@RequestParam(required = true) String withdraw_no,
			@RequestParam(required = false, value = "") String userName,
			@RequestParam(required = false, value = "") String exter_invoke_ip){
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("apply withdrawals withdraw_no [%s]", withdraw_no));
		try{
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
        	
			int appId = Integer.parseInt(appid);
			boolean isAllowedBusiness = BusinessEnumType.CommdityApplication.verifyed(appId, secret);
			if(isAllowedBusiness){
				CommdityApplication app = BusinessEnumType.CommdityApplication.fromKey(appId);
				switch(app){
    			case BHU_PREPAID_BUSINESS:
    				logger.info(String.format("apply BHU_TIP_BUSINESS withdrawals appid[%s] secret[%s]", appid,secret));
    				break;
    			case DEFAULT: 
    				logger.error(String.format("apply DEFAULT withdrawals appid[%s] secret[%s]", appid,secret));
    				break;
    			default:
    				break;
				}
			}else{
				logger.error(String.format("apply payment appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_USERORPWD_ERROR)));
				return;
			}
			
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
        	
        	String total_fee_fen = BusinessHelper.getMoney(total_fee);
        	int temp = Integer.parseInt(total_fee_fen);
        	if(temp < 1000){
        		logger.error(String.format("apply withdrawals total_fee[%s] errorMsg:[%s] , [%s]", total_fee,ResponseErrorCode.USER_WALLET_WITHDRAW_LOWERTHEN_MINLIMIT.i18n(),"10元"));
        		payLogicService.updateWithdrawalsStatus(null, withdraw_no, withdraw_type,false);
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.USER_WALLET_WITHDRAW_LOWERTHEN_MINLIMIT,new String[]{"10元"})));
        		return;
        	}
        	
    		PaymentWithdraw paymentWithdraw = paymentWithdrawService.findByOrderId(withdraw_no);
    		if(paymentWithdraw != null){
    			logger.error(String.format("apply withdrawals paymentWithdraw [%s]", paymentWithdraw));
        		throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST,new String[]{""}); 
        	}
        	ResponseCreateWithdrawDTO result = null;
        	if(withdraw_type.equals("weixin")){ //微信支付
        		result =  doWxWithdrawals(request,response,total_fee,withdraw_no,exter_invoke_ip,userId,userName);
        	}else{//提示暂不支持的支付方式
        		logger.info(String.format("apply withdrawals withdraw_type [%s]",withdraw_type + ResponseErrorCode.RPC_MESSAGE_UNSUPPORT));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT)));
        		return;
        	}
        	String type = result.getWithdraw_type();
    		String msg = result.getUrl();
    		if(type.equalsIgnoreCase("FAIL")){
    			ResponsePaymentDTO respone = new ResponsePaymentDTO();
    			respone.setSuccess(false);
    			respone.setMsg(msg);
    			logger.info(String.format("apply withdrawals return result [%s]",JsonHelper.getJSONString(respone)));
    			SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(respone));
    		}else{
    			logger.info(String.format("apply withdrawals return result [%s]",JsonHelper.getJSONString(result)));
        		SpringMVCHelper.renderJson(response, result);
    		}
		}catch(BusinessI18nCodeException i18nex){
			logger.error(String.format("submitWithdrawals catch BusinessI18nCodeException [%s]",ResponseError.embed(i18nex)));
			SendMailHelper.doSendMail(2,"submitWithdrawals接口："+i18nex.getMessage()+i18nex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			logger.error(String.format("submitWithdrawals catch Exception [%s]",ResponseError.SYSTEM_ERROR));
			SendMailHelper.doSendMail(2,"submitWithdrawals接口："+ex.getMessage()+ex.getCause());
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
    public void submitPayment(
    		HttpServletResponse response,
    		HttpServletRequest request,
    		@RequestParam(required = true) String appid,
    		@RequestParam(required = true) String secret,
    		@RequestParam(required = true) String goods_no,
    		@RequestParam(required = true) String umac,
    		@RequestParam(required = true) String total_fee,
    		@RequestParam(required = true) String payment_type,
    		@RequestParam(required = false, value = "") String exter_invoke_ip,
    		@RequestParam(required = false, value = "") String payment_completed_url,
    		@RequestParam(required = false, value = "") String channel,
    		@RequestParam(required = false, value = "") String version,
    		@RequestParam(required = false, value = "") String paymentName){ 
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("apply payment goods_no [%s]", goods_no));
		
		String ot = request.getParameter("ot");
		//total_fee = RandomPicker.randomstart(20000, 1)+"";
		
		long begin = System.currentTimeMillis(); // 这段代码放在程序执行前
		try{
			if(StringUtils.isBlank(channel)){
				channel = "0";
			}			
			if(StringUtils.isBlank(version)){
				version = "0";
			}
			if(StringUtils.isBlank(ot)){
				ot = "10m";
			}
			if (StringUtils.isBlank(appid)) {
        		logger.error(String.format("apply payment appid [%s]", appid));
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
    			return;
    		}
			if (StringUtils.isBlank(secret)) {
        		logger.error(String.format("apply payment secret [%s]", secret));
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
    			return;
    		}
			logger.info(String.format("apply payment bussiness appid[%s] secret[%s]", appid,secret));
			int appId = Integer.parseInt(appid);
			boolean isAllowedBusiness = BusinessEnumType.CommdityApplication.verifyed(appId, secret);
			if(isAllowedBusiness){
				CommdityApplication app = BusinessEnumType.CommdityApplication.fromKey(appId);
				switch(app){
    			case BHU_PREPAID_BUSINESS:
    				if (StringUtils.isBlank(paymentName)) {
    					paymentName = "虎钻";
    				}
    				break;
    			case DEFAULT: 
    				if (StringUtils.isBlank(paymentName)) {
    					paymentName = "打赏";
    				}
    				break;
    			default:
    				paymentName = "打赏";
    				break;
				}
			}else{
				logger.error(String.format("apply payment appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_USERORPWD_ERROR)));
				return;
			}
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
        	}else{
        		logger.error(String.format("apply payment total_fee [%s]", total_fee));
        		double total_fees = Double.parseDouble(BusinessHelper.getMoney(total_fee));
        		if(payment_type.contains("Alipay")){
        			if(total_fees >= 1000000000){
        				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
            					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        				return;
        			}
        		}else if(payment_type.equalsIgnoreCase("WapWeixin")){
        			if(total_fees >= 300000){
        				if(!channel.equals("2")){
        					SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
        							ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        					return;
        				}
        			}
        		}else{
        			if(total_fees >= 2000000){
        				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
            					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        				return;
        			}
        		}
        	}
        	
        	
        	if (StringUtils.isBlank(goods_no)) {
        		logger.error(String.format("apply payment goods_no [%s]", goods_no));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
        		return;
        	}
        	
        	PaymentTypeVTO result = null;
//        	//逻辑处理
        	long select_isExist_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
    		PaymentReckoning paymentReckoning = paymentReckoningService.findByOrderId(goods_no);
    		long select_isExist_end = System.currentTimeMillis() - select_isExist_begin; // 这段代码放在程序执行后
    		logger.info(goods_no+"查询订单是否存在耗时：" + select_isExist_end + "毫秒");
        	if(paymentReckoning != null){
        		logger.error(String.format("apply payment goods_no [%s]", goods_no+ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST)));
        		return;
        	}
        	umac = BusinessHelper.formatMac(umac);
        	PaymentChannelCode paymentChannel = PaymentChannelCode.getPaymentChannelCodeByCode(payment_type);
    		switch(paymentChannel){
    			case BHU_PC_WEIXIN: //PC微信支付
    				long PC_WEIXIN_begin = System.currentTimeMillis();
    				result =  doNativeWxPayment(request,response,"0",total_fee,goods_no,exter_invoke_ip,payment_completed_url,umac,paymentName,appid);
    				long PC_WEIXIN_end = System.currentTimeMillis() - PC_WEIXIN_begin; 
    				logger.info(goods_no+"PC微信支付耗时：" + PC_WEIXIN_end + "毫秒");
    				break;
    			case BHU_PC_ALIPAY: //PC支付宝
    				long PC_ALIPAY_begin = System.currentTimeMillis();
    				result =  doAlipay(response,request, total_fee, goods_no,payment_completed_url,exter_invoke_ip,payment_type,umac,paymentName,appid,ot);
    				long PC_ALIPAY_end = System.currentTimeMillis() - PC_ALIPAY_begin; 
    				logger.info(goods_no+"PC支付宝耗时：" + PC_ALIPAY_end + "毫秒");
    				break;
    			case BHU_APP_WEIXIN: //App微信支付
    				long APP_WEIXIN_begin = System.currentTimeMillis();
    				result =  doAppWxPayment(request,response,total_fee,goods_no,exter_invoke_ip,payment_completed_url,umac,paymentName,appid,channel);
    				long APP_WEIXIN_end = System.currentTimeMillis() - APP_WEIXIN_begin; 
    				logger.info(goods_no+"APP微信支付耗时：" + APP_WEIXIN_end + "毫秒");
    				break;
    			case BHU_APP_ALIPAY: //App支付宝
    				long APP_ALIPAY_begin = System.currentTimeMillis();
    				result =  doAlipay(response,request, total_fee, goods_no,payment_completed_url,exter_invoke_ip,payment_type,umac,paymentName,appid,ot);
    				long APP_ALIPAY_end = System.currentTimeMillis() - APP_ALIPAY_begin; 
    				logger.info(goods_no+"App支付宝耗时：" + APP_ALIPAY_end + "毫秒");
    				break;
    			case BHU_WAP_WEIXIN: //汇付宝
    				int channelI = Integer.parseInt(channel);
    				switch (channelI) {
    				case 0:
    					long get_agentMerchant_begin = System.currentTimeMillis();
    					String agentMerchant = payLogicService.findWapWeixinMerchantServiceByCondition();
    					long get_agentMerchant_end = System.currentTimeMillis() -  get_agentMerchant_begin; 
        				logger.info(goods_no+"查询WAP微信获取支付渠道耗时：" + get_agentMerchant_end + "毫秒");
        				
        				/*******2016-09-26*******start****判断是内网环境WapWeixin 只用现在支付**/
        				String curEnv = payHttpService.getEnv().toUpperCase();
        				if(curEnv.equals("INNER") && agentMerchant.equals("Midas")){
        					agentMerchant = "Hee";
        				}
        				/*******2016-09-26****end*********/
        				
        				if(agentMerchant.equals("Now")){
        					long WAP_WEIXIN_Now_begin = System.currentTimeMillis();
        					result =  doNowpay(response,"4", total_fee, goods_no,exter_invoke_ip,payment_completed_url,umac,paymentName,appid); 
        					long WAP_WEIXIN_Now_end = System.currentTimeMillis() - WAP_WEIXIN_Now_begin; 
            				logger.info(goods_no+"Wap微信现在支付获取支付url耗时：" + WAP_WEIXIN_Now_end + "毫秒");
            				long end = System.currentTimeMillis() - begin;
            	    		logger.info(goods_no+"Wap微信现在支付，逻辑处理完成耗时：" + end + "毫秒！！！！！！");
        				}else if(agentMerchant.equals("Midas")){
        					long WAP_WEIXIN_MIDSA_begin = System.currentTimeMillis();
        					result =  doMidas(response,version,"1", total_fee, goods_no,exter_invoke_ip,payment_completed_url,umac,paymentName,appid); 
        					long WAP_WEIXIN_MIDSA_end = System.currentTimeMillis() - WAP_WEIXIN_MIDSA_begin; 
        					logger.info(goods_no+"Wap微信米大师支付获取支付url耗时：" + WAP_WEIXIN_MIDSA_end + "毫秒");
        				}else if(agentMerchant.equals("Hee")){
        					long WAP_WEIXIN_Hee_begin = System.currentTimeMillis();
        					result =  doHee(response,"3", total_fee, goods_no,exter_invoke_ip,payment_completed_url,umac,paymentName,appid); 
        					long WAP_WEIXIN_Hee_end = System.currentTimeMillis() - WAP_WEIXIN_Hee_begin; 
            				logger.info(goods_no+"Wap微信汇元支付获取支付url耗时：" + WAP_WEIXIN_Hee_end + "毫秒");
        				}else{
        					long WAP_WEIXIN_Now_begin = System.currentTimeMillis();
        					result =  doNowpay(response,"4", total_fee, goods_no,exter_invoke_ip,payment_completed_url,umac,paymentName,appid); 
        					long WAP_WEIXIN_Now_end = System.currentTimeMillis() - WAP_WEIXIN_Now_begin; 
            				logger.info(goods_no+"Wap微信现在支付获取支付url耗时：" + WAP_WEIXIN_Now_end + "毫秒");
            				long end = System.currentTimeMillis() - begin;
            	    		logger.info(goods_no+"Wap微信现在支付，逻辑处理完成耗时：" + end + "毫秒！！！！！！");
        				}
    					break;
					case 2:
						long WAP_WEIXIN_NATIVE_begin = System.currentTimeMillis();
    					result =  doNativeWxPayment(request,response,channel,total_fee,goods_no,exter_invoke_ip,payment_completed_url,umac,paymentName,appid);
    					long WAP_WEIXIN_NATIVE_end = System.currentTimeMillis() -  WAP_WEIXIN_NATIVE_begin; 
        				logger.info(goods_no+"WAP微信他人代付耗时：" + WAP_WEIXIN_NATIVE_end + "毫秒");
						break;
					case 3:
						long B_WEIXIN_NATIVE_begin = System.currentTimeMillis();
    					result =  doAppWxPayment(request,response,total_fee,goods_no,exter_invoke_ip,payment_completed_url,umac,paymentName,appid,channel);
    					long B_WEIXIN_NATIVE_end = System.currentTimeMillis() -  B_WEIXIN_NATIVE_begin; 
        				logger.info(goods_no+"utool耗时：" + B_WEIXIN_NATIVE_end + "毫秒");
						break;
					case 4:
						long A_WEIXIN_NATIVE_begin = System.currentTimeMillis();
    					result =  doAppWxPayment(request,response,total_fee,goods_no,exter_invoke_ip,payment_completed_url,umac,paymentName,appid,channel);
    					long A_WEIXIN_NATIVE_end = System.currentTimeMillis() -  A_WEIXIN_NATIVE_begin; 
        				logger.info(goods_no+"必虎wifi管家app耗时：" + A_WEIXIN_NATIVE_end + "毫秒");
						break;

					default:
						logger.info(String.format("apply payment payment_type [%s]",payment_type + ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
	        					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT))));
						break;
					}
                	break;
    			case BHU_WAP_ALIPAY: //Wap微信支付
    				long WAP_ALIPAY_begin = System.currentTimeMillis();
    				result =  doAlipay(response,request, total_fee, goods_no,payment_completed_url,exter_invoke_ip,payment_type,umac,paymentName,appid,ot);
    				long WAP_ALIPAY_end = System.currentTimeMillis() - WAP_ALIPAY_begin; 
    				logger.info(goods_no+"Wap支付宝支付耗时：" + WAP_ALIPAY_end + "毫秒");
    				break;
    			default:
    				logger.info(String.format("apply payment payment_type [%s]",payment_type + ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
        					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT))));
            		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
        					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT)));
    				break;
    		}
    		long end = System.currentTimeMillis() - begin;
    		logger.info(goods_no+"逻辑处理完成耗时：" + end + "毫秒！！！！！！");
    		
    		String types = result.getType();
    		String msg = result.getUrl();
    		if(types.equalsIgnoreCase("FAIL")){
    			Response respone = new Response();
    			respone.setSuccess(false);
    			respone.setMsg(msg);
    			logger.info(String.format("apply payment return result [%s]",JsonHelper.getJSONString(respone)));
    			SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(respone));
    		}else{
    			logger.info(String.format("apply payment return result [%s]",JsonHelper.getJSONString(result)));
    			SpringMVCHelper.renderJson(response, PaymentResponseSuccess.embed(JsonHelper.getJSONString(result)));
    		}
		}catch(BusinessI18nCodeException i18nex){
			logger.error(String.format("submitPayment catch BusinessI18nCodeException [%s]",ResponseError.embed(i18nex)));
			SendMailHelper.doSendMail(3,"submitPayment接口："+i18nex.getMessage()+i18nex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			logger.error(String.format("submitPayment catch Exception [%s]",JsonHelper.getJSONString(ResponseError.SYSTEM_ERROR)));
			SendMailHelper.doSendMail(3,"submitPayment接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
		long end = System.currentTimeMillis() - begin;
		logger.info(goods_no+"请求总耗时：" + end + "毫秒！！！！！！");
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
			
			if(request_unifiedorder_end > nowOutTimeLevel){
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "微信提现支付",request_unifiedorder_end+"");
				if(request_unifiedorder_end > 7000){
					weixinWithdrawWarningCount++;
				}
				if(request_unifiedorder_end > 30000){
					String sendMsg = payLogicService.updatePaymentParam(7,smsg,weixinWithdrawErrorTime);
					if(!StringUtils.isBlank(sendMsg)){
						weixinWithdrawErrorTime =sendMsg;
					}
				}	
				PaymentOutimeErrorLog  paymentNowpayErrorLog = new PaymentOutimeErrorLog();
				paymentNowpayErrorLog.setId(reckoningId);
				paymentNowpayErrorLog.setOrder_id(withdraw_no);
				paymentNowpayErrorLog.setOt(new Long(request_unifiedorder_end).intValue());
				paymentNowpayErrorLog.setC_type("withdrawWeixin");
				paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
			}
		
			switch (weixinWithdrawWarningCount) {
			case 3:
				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "微信提现支付",request_unifiedorder_end+"");
				String resp = "ture";
				String acc = PayHttpService.Internal_level2_warning_man;
				String curDate = (BusinessHelper.getTimestamp()+"").substring(0, 10);
				if(!weixinWithdrawWarningTime.equals(curDate)){
					weixinWithdrawWarningTime = curDate;
					SendMailHelper.doSendMail(2,smsg);
					resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
				}
				logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,resp));
				weixinWithdrawWarningCount = 0;
				break;
			default:
				break;
			}
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
	private PaymentTypeVTO doNativeWxPayment(HttpServletRequest request, HttpServletResponse response,String channel_type,String total_fee,String out_trade_no,String Ip,String locationUrl,String usermac,String paymentName,String appId){
		
		PaymentTypeVTO result= new PaymentTypeVTO();
        String NOTIFY_URL = PayHttpService.WEIXIN_NOTIFY_URL;
        String PRUE_LOGO_URL = PayHttpService.PRUE_LOGO_URL;
        String QR_CODE_URL = PayHttpService.QR_CODE_URL;
        String product_name= paymentName;//订单名称
    	total_fee = BusinessHelper.getMoney(total_fee);
    	String type = "";
    	if(channel_type.equals("0")){
    		type = PaymentChannelCode.BHU_PC_WEIXIN.i18n();
    	}else if(channel_type.equals("2")){
    		type = PaymentChannelCode.BHU_WAP_WEIXIN.i18n();
    	}
    	long begin = System.currentTimeMillis(); 
        String reckoningId = payLogicService.createPaymentReckoning(out_trade_no,channel_type,total_fee,Ip,type,usermac,paymentName,appId);
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
			base64CodeUrl = BusinessHelper.GetBase64ImageStr(codeUrl,QR_CODE_URL,PRUE_LOGO_URL);
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
	
	/**
     * 处理微信APP支付请求
     * @param request
     * @param response
     * @param totalPrice
     * @param goodsNo
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
	private PaymentTypeVTO doAppWxPayment(HttpServletRequest request, HttpServletResponse response,String total_fee,String out_trade_no,String Ip,String locationUrl,String usermac,String paymentName,String appid,String channel){
		PaymentTypeVTO result= new PaymentTypeVTO();
        String NOTIFY_URL = PayHttpService.WEIXIN_NOTIFY_URL;
        String product_name= paymentName;//订单名称
        String appId = "";
		 String mchId = "";
		 String mchKey = "";
		 if(appid.equals("1000")){
			 appId = payHttpService.getAppDSAppId();
			 mchId = payHttpService.getAppDSMchId();
			 mchKey = payHttpService.getAppDSMchKey();
		 }else if(appid.equals("1002")){
			 appId = payHttpService.getAppAppId();
			 mchId = payHttpService.getAppMchId();
			 mchKey = payHttpService.getAppMchKey();
		 }else{
			 appId = payHttpService.getAppDSAppId();
			 mchId = payHttpService.getAppDSMchId();
			 mchKey = payHttpService.getAppDSMchKey();
		 }
    	total_fee = BusinessHelper.getMoney(total_fee);
        String reckoningId = payLogicService.createPaymentReckoning(out_trade_no,"0",total_fee,Ip,PaymentChannelCode.BHU_APP_WEIXIN.i18n(),usermac,paymentName,appid);
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
    private PaymentTypeVTO doAlipay(HttpServletResponse response,HttpServletRequest request,
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
		String body = "必虎服务";
		Map<String, String> sParaTemp = new HashMap<String, String>();
		String reckoningId = null;
		String total_fee_fen = BusinessHelper.getMoney(total_fee);
		if(CommdityApplication.BHU_PREPAID_BUSINESS.getKey().equals(Integer.parseInt(appid))){
			return_url = PayHttpService.ALIPAY_PREPAID_RETURN_URL;
		}
		PaymentChannelCode payChannel =PaymentChannelCode.getPaymentChannelCodeByCode(type);
		switch (payChannel) {
		case BHU_WAP_ALIPAY:
			reckoningId = payLogicService.createPaymentReckoning(out_trade_no,"0",total_fee_fen,ip,PaymentChannelCode.BHU_WAP_ALIPAY.i18n(),usermac,paymentName,appid);
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
			reckoningId = payLogicService.createPaymentReckoning(out_trade_no,"0",total_fee_fen,ip,PaymentChannelCode.BHU_APP_ALIPAY.i18n(),usermac,paymentName,appid);
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
			reckoningId = payLogicService.createPaymentReckoning(out_trade_no,"0",total_fee_fen,ip,PaymentChannelCode.BHU_PC_ALIPAY.i18n(),usermac,paymentName,appid);
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
    private PaymentTypeVTO doNowpay(HttpServletResponse response, 
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
		//服务器异步通知页面路径
		String notify_url = PayHttpService.NOWIPAY_NOTIFY_URL;
		//打赏页面跳转同步通知页面路径
		String return_url = PayHttpService.NOWPAY_RETURN_URL;
		//订单名称
		String subject = paymentName;//;new String("打赏".getBytes("ISO-8859-1"), "utf-8");
		//付款金额
		String total_fee = totalPrice;
		//订单描述
		String body = "必虎服务";
		//数据库存的是分，此处需要把传来的支付金额转换成分，而传给支付宝的保持不变（默认元）
		String total_fee_fen = BusinessHelper.getMoney(total_fee);
		if(CommdityApplication.BHU_PREPAID_BUSINESS.getKey().equals(Integer.parseInt(appid))){
			return_url = PayHttpService.ALIPAY_PREPAID_RETURN_URL;
		}
		Map<String, String> dataMap = new HashMap<String, String>();
		
		long get_now_reckoning_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
		String reckoningId = payLogicService.createPaymentReckoning(out_trade_no,"4",total_fee_fen,ip,PaymentChannelCode.BHU_WAP_WEIXIN.i18n(),usermac,paymentName,appid);
		long get_now_reckoning_end = System.currentTimeMillis() - get_now_reckoning_begin; // 这段代码放在程序执行后
		logger.info(out_trade_no+"现在支付获取支付流水号耗时：" + get_now_reckoning_end + "毫秒");
		//做MD5签名
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
			logger.info(out_trade_no+"现在支付locationUrlr入库耗时：" + insert_locationUrl_end + "毫秒");
			logger.info(String.format("apply nowpay set location reckoningId [%s] locationUrl [%s] insert finished.",reckoningId, locationUrl));
		}
		
		String sHtmlText = "";
        try {
        	long request_now_url_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
            sHtmlText = NowpaySubmit.buildRequest(dataMap,"post","确认");
            long request_now_url_end = System.currentTimeMillis() - request_now_url_begin; // 这段代码放在程序执行后
    		logger.info(out_trade_no+"请求现在支付获取支付url耗时：" + request_now_url_end + "毫秒");
    		int nowOutTimeLevel = payHttpService.getOt();
    		try{
    			if(request_now_url_end > nowOutTimeLevel){
    				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "现在支付",request_now_url_end+"");
    				if(request_now_url_end > 6500){
    					nowWarningCount++;
    				}
    				if(request_now_url_end > 30000){
    					String sendMsg = payLogicService.updatePaymentParam(1,smsg,nowErrorTime);
    					if(!StringUtils.isBlank(sendMsg)){
    						nowErrorTime =sendMsg;
    					}
    				}
    				
    				logger.info(out_trade_no+"请求现在支付获取支付url超时"+(request_now_url_end-1000)+"毫秒,记录错误数据。");
    				PaymentOutimeErrorLog  paymentNowpayErrorLog = new PaymentOutimeErrorLog();
    				paymentNowpayErrorLog.setId(reckoningId);
    				paymentNowpayErrorLog.setOrder_id(out_trade_no);
    				paymentNowpayErrorLog.setOt(new Long(request_now_url_end).intValue());
    				paymentNowpayErrorLog.setC_type("Now");
    				paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
    			}
    			
    			switch (nowWarningCount) {
    			case 3:
    				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "现在支付",request_now_url_end+"");
    				String resp = "ture";
    				String acc = PayHttpService.Internal_level2_warning_man;
    				String curDate = (BusinessHelper.getTimestamp()+"").substring(0, 10);
    				if(!nowWarningTime.equals(curDate)){
    					nowWarningTime = curDate;
    					SendMailHelper.doSendMail(2,smsg);
    					resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
    				}
    				logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,resp));
    				nowWarningCount = 0;
    				break;
    				
    			default:
    				break;
    			}
    		}catch(Exception e){
    			logger.info(String.format("apply now pay catch exception [%s]",e.getMessage()+e.getCause()));
    		}
            result = new PaymentTypeVTO();
            result.setChannel("Now");
            result.setType("http");
            result.setUrl(sHtmlText);
            return result;
        } catch (Exception e) {
        	SendMailHelper.doSendMail(3,"请求现在支付获取支付url捕获异常："+e.getMessage()+e.getCause());
        	result.setType("FAIL");
            result.setUrl("支付请求失败");
            return result;
        }
	}
    
    /**
     * 处理米大师支付服务请求
     * @param response
     * @param total_fee
     * @param goods_no
     * @param paymentName 
     * @param umac 
     * @param payment_completed_url 
     * @param exter_invoke_ip 
     * @return
     */
    private PaymentTypeVTO doMidas(HttpServletResponse response,String version, String type,String total_fee, String out_trade_no, String ip, String return_url, String usermac, String paymentName,String appid) {
    	PaymentTypeVTO result = new PaymentTypeVTO();
    	if(ip == "" || ip == null){
    		ip = "213.42.3.24";
    	}
    	if(usermac.equals("")){
    		usermac = RandomPicker.randString(BusinessHelper.letters, 8);
    	}
    	String total_fee_fen = BusinessHelper.getMoney(total_fee);
    	long get_midas_reckoning_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
    	String reckoningId = payLogicService.createPaymentReckoning(out_trade_no,type,total_fee_fen,ip,PaymentChannelCode.BHU_WAP_WEIXIN.i18n(),usermac,paymentName,appid);
    	long get_midas_reckoning_end = System.currentTimeMillis() - get_midas_reckoning_begin; // 这段代码放在程序执行后
		logger.info(out_trade_no+"midas支付获取支付流水号耗时：" + get_midas_reckoning_end + "毫秒");
    	if(version.equals("v1")){
        	if (StringUtils.isBlank(return_url)) {
        		return_url = PayHttpService.WEB_NOTIFY_URL;
        		logger.info(String.format(" midas  return_url location is null so we set default value %s ",return_url));
        	}else{
        		logger.info(String.format("get midas location [%s] ",return_url));
        		PaymentAlipaylocation orderLocation = new PaymentAlipaylocation();
        		orderLocation.setTid(reckoningId);
        		orderLocation.setLocation(return_url);
        		long insert_midas_url_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
        		paymentAlipaylocationService.insert(orderLocation);
        		long insert_midas_url_end = System.currentTimeMillis() - insert_midas_url_begin; // 这段代码放在程序执行后
        		logger.info(out_trade_no+"midas插入支付完成URL耗时：" + insert_midas_url_end + "毫秒");
        		logger.info(String.format("apply midas set location reckoningId [%s] location [%s]  insert finished.",reckoningId,return_url));
        		if(return_url.contains("payokurl=")){
        			String[] Str =return_url.split("payokurl=");
        			if( Str.length > 0){
        				return_url = Str[1];
        			}
        		}
        	}

        	double fenTemp = Double.parseDouble(total_fee_fen);
        	double jiaoTemp =fenTemp/10;
        	long get_midas_url_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
        	String par = "reckoningId="+reckoningId+"&jiaoTemp="+jiaoTemp+"&ip="+ip+"&paymentName="+paymentName+
        			"&usermac="+usermac+"&return_url="+return_url;
        	String results = PostRequestUtil.sendPost(PayHttpService.MIDAS_REQURST_URL, par);
        	//TODO:生产环境请注释掉此方法，放开上方远程调用方法
//        	String results = MidasUtils.submitOrder("v1",reckoningId, jiaoTemp+"", ip,paymentName,usermac,return_url);
        	long get_midas_url_end = System.currentTimeMillis() - get_midas_url_begin; // 这段代码放在程序执行后
    		logger.info(out_trade_no+"请求midas获取支付URL耗时：" + get_midas_url_end + "毫秒");
    		int nowOutTimeLevel = payHttpService.getOt();
    		
    		try{
    			if(get_midas_url_end > nowOutTimeLevel){
    				
    				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "Midas",get_midas_url_end+"");
    				if(get_midas_url_end > 7000){
    					midasWarningCount++;
    				}
    				if(get_midas_url_end > 30000){
    					String sendMsg = payLogicService.updatePaymentParam(2,smsg,midasErrorTime);
    					if(!StringUtils.isBlank(sendMsg)){
    						midasErrorTime =sendMsg;
    					}
    				}	
    				PaymentOutimeErrorLog  paymentNowpayErrorLog = new PaymentOutimeErrorLog();
    				paymentNowpayErrorLog.setId(reckoningId);
    				paymentNowpayErrorLog.setOrder_id(out_trade_no);
    				paymentNowpayErrorLog.setOt(new Long(get_midas_url_end).intValue());
    				paymentNowpayErrorLog.setC_type("Midas");
    				paymentOutimeErrorLogService.insert(paymentNowpayErrorLog);
    			}
    			
    			switch (midasWarningCount) {
    			case 3:
    				String smsg = String.format(BusinessRuntimeConfiguration.Internal_payment_warning_Template, "Midas",get_midas_url_end+"");
    				String resp = "ture";
    				String acc = PayHttpService.Internal_level2_warning_man;
    				String curDate = (BusinessHelper.getTimestamp()+"").substring(0, 10);
    				if(!midasWarningTime.equals(curDate)){
    					midasWarningTime = curDate;
    					SendMailHelper.doSendMail(2,smsg);
    					resp = SmsSenderFactory.buildSender(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
    				}
    				logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,resp));
    				midasWarningCount = 0;
    				break;
    			default:
    				break;
    			}
    		}catch(Exception e){
    			logger.info(String.format("apply midas catch exception [%s]",e.getMessage()+e.getCause()));
    		}
    		
    		logger.info(String.format("apply midas results [%s]",results));
        	if("error".equalsIgnoreCase(results)){
        		result.setType("FAIL");
            	result.setUrl("支付请求失败");
            	return result;
        	}else{
            	result.setType("http");
            	result.setUrl(results);
            	return result;
        	}
    	}
    	return result;    	
	}
    
    @ResponseBody
	@RequestMapping(value={"/payment/getMidas","/midas"},method={RequestMethod.GET,RequestMethod.POST})
    public String getMidas(HttpServletResponse response,
    		@RequestParam(required = true)  String reckoningId, 
    		@RequestParam(required = true)  String jiaoTemp,
    		@RequestParam(required = true)  String ip,
    		@RequestParam(required = true)  String paymentName,
    		@RequestParam(required = true)  String return_url,
    		@RequestParam(required = true)  String usermac
    		) {
    	response.setHeader("Access-Control-Allow-Origin", "*");
    	return  MidasUtils.submitOrder("v1",reckoningId, jiaoTemp, ip,paymentName,usermac,return_url);
    		
    }
    
    /**
     * 处理汇付宝支付服务请求
     * @param response
     * @param total_fee
     * @param out_trade_no
     * @param ip
     * @return
     */
    private PaymentTypeVTO doHee(HttpServletResponse response, String type,String total_fee, String out_trade_no,String ip,String locationUrl,String usermac,String paymentName,String appid) {
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
    	String reckoningId = payLogicService.createPaymentReckoning(out_trade_no,"3",total_fee_fen,ip,PaymentChannelCode.BHU_WAP_WEIXIN.i18n(),usermac,paymentName,appid);
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
   		long begin = System.currentTimeMillis(); // 这段代码放在程序执行前
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
			params.put(name, valueStr);
		}
		
		String isNull = request.getParameter("out_trade_no");
		if (StringUtils.isBlank(isNull)) {
			logger.error(String.format("get alipay notify out_trade_no  [%s]", isNull));
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
			int payStatus = payReckoning.getPay_status();
			if(payStatus == 0){ //0未支付;1支付成功
				if(trade_status.equals("TRADE_FINISHED")){
					logger.info(" TRADE_FINISHED success");	//请不要修改或删除
					SpringMVCHelper.renderJson(response, "success");
					return;
				} else if (trade_status.equals("TRADE_SUCCESS")){
					//支付成功
					logger.info("支付成功 修改订单的支付状态,TRADE_SUCCESS");
					payLogicService.updatePaymentStatus(payReckoning,out_trade_no,trade_no,"Alipay","");
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
		long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
		logger.info(payReckoning.getOrder_id()+"支付宝回调通知总耗时：" + end + "毫秒");
		return;
    }
    
    /**
     * 现在支付通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
	@RequestMapping(value = "/payment/nowpayNotifySuccess")
   	public void nowpayNotifySuccess(HttpServletRequest request,
   			HttpServletResponse response) throws IOException, JDOMException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","现在订单支付通知",BusinessHelper.gettimestamp(),"Starting"));
   		long begin = System.currentTimeMillis(); // 这段代码放在程序执行前
		BufferedReader reader = request.getReader();
		StringBuilder reportBuilder = new StringBuilder();
		String tempStr = "";
		while((tempStr = reader.readLine()) != null){
			reportBuilder.append(tempStr);
		}		
		String reportContent = reportBuilder.toString();		
		Map<String,String> dataMap = FormDateReportConvertor.parseFormDataPatternReportWithDecode(reportContent, "UTF-8", "UTF-8");
		//去除签名类型和签名值
        dataMap.remove("signType");
        String signature = dataMap.remove("signature");
        //验证签名
        boolean isValidSignature = MD5Facade.validateFormDataParamMD5(dataMap,NowpayConfig.appKey,signature);
        String isNull = dataMap.get("mhtOrderNo");
        if (StringUtils.isBlank(isNull)) {
			logger.error(String.format("get nowpay notify out_trade_no  [%s]", isNull));
			response.getOutputStream().write("success=N".getBytes());
			return;
		}
        //商户订单号
		String out_trade_no = dataMap.get("mhtOrderNo");
		//现在支付订单号
		String trade_no = dataMap.get("nowPayOrderNo");
		//现在支付渠道订单号
		String channelOrderNo = dataMap.get("channelOrderNo");
		//交易状态
		String trade_status =dataMap.get("tradeStatus");
		PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
		if (payReckoning == null) {
        	logger.info("get nowpay notice payReckoning " +payReckoning);
        	return;
        }
		String orderId = payReckoning.getOrder_id();
        logger.info(String.format("get nowpay notify reckoningId [%s] trade_no [%s] channelOrderNo [%s] orderId [%s] trade_status [%s]",out_trade_no, trade_no, channelOrderNo, orderId,trade_status));
        
        if(isValidSignature){
			int payStatus = payReckoning.getPay_status();
			if(payStatus == 0){ //0未支付;1支付成功
				 if (trade_status.equals("A001")){
					logger.info("支付成功 修改订单的支付状态,TRADE_SUCCESS");
					payLogicService.updatePaymentStatus(payReckoning,out_trade_no,channelOrderNo,"Now",trade_no);
					response.getOutputStream().write("success=Y".getBytes());
					return;
				}else{
					logger.info("支付失败！");	//请不要修改或删除
					response.getOutputStream().write("success=N".getBytes());
					return;
				}
			}
        	response.getOutputStream().write("success=Y".getBytes());
        	long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
        	logger.info(payReckoning.getOrder_id()+"现在支付通知总耗时：" + end + "毫秒");
        	return;
        } else{
        	response.getOutputStream().write("success=N".getBytes());
        	long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
        	logger.info(payReckoning.getOrder_id()+"现在支付通知总耗时：" + end + "毫秒");
        	return;
        }
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
    public void wxPayNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info(String.format("******[%s]********[%s]*******[%s]********","微信订单支付通知",BusinessHelper.gettimestamp(),"Starting"));
        long begin = System.currentTimeMillis(); // 这段代码放在程序执行前
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
        String orderId = "";
        try {
            logger.info(result);
            map = XMLUtil.doXMLParse(result);
            paySuccessNotifyResponse=new PaySuccessNotifyResponse();
            paySuccessNotifyResponse.setResponseContent(result);
            paySuccessNotifyResponse.setPropertyMap(map);
            out_trade_no = paySuccessNotifyResponse.getOut_trade_no().trim();
            thirdPartCode = paySuccessNotifyResponse.getTransaction_id().trim();
        	if (StringUtils.isBlank(out_trade_no)) {
    			logger.error("请求参数(out_trade_no)有误,不能为空");
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
    			return;
    		}
            PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
            if (payReckoning == null) {
            	response.getWriter().println("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>");
		        return;
            }
            orderId = payReckoning.getOrder_id();
            logger.info(String.format("get wx notify reckoningId [%s] trade_no [%s] orderId [%s]",out_trade_no, thirdPartCode,orderId));
			int payStatus = payReckoning.getPay_status();
			if(payStatus == 0){ //0未支付;1支付成功
				logger.info("账单流水号："+out_trade_no+"支付状态未修改,将进行修改。。。");
	            if("SUCCESS".equals(paySuccessNotifyResponse.getReturn_code()) && "SUCCESS".equals(paySuccessNotifyResponse.getResult_code())){
	            	 logger.info("账单流水号："+out_trade_no+"支付成功.微信返回SUCCESS.");
	            	 payLogicService.updatePaymentStatus(payReckoning,out_trade_no,thirdPartCode,"BHU","");
	            	 response.getWriter().println("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
	            	 return;
	            }else{
	            	logger.info("支付流水号："+out_trade_no+"支付失败 修改订单的支付状态.");
	            	response.getWriter().println("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>");
  		            return;
	            }
			}else{
				logger.info("账单流水号："+out_trade_no+"支付账单、订单状态状态修改成功!");
				response.getWriter().println("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
		        return;
			}
        } catch (JDOMException e) {
            logger.info("账单流水号："+out_trade_no+"捕获到微信通知/notify_success方法的异常："+e.getMessage()+e.getCause());
            String noticeStr = "";//XMLUtil.setXML("FAIL", "");
            out.print(new ByteArrayInputStream(noticeStr.getBytes(Charset.forName("UTF-8"))));
        }
        long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
	    logger.info(orderId+"微信通知总耗时：" + end + "毫秒");
	    response.getWriter().println("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
        return;
    }

    /********END************微信接口*********END********************/
    
	
    /**
     * 汇元宝通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
    @SuppressWarnings("rawtypes")
   	@RequestMapping(value = "/payment/heepayNotifySuccess", method = { RequestMethod.GET,RequestMethod.POST })
   	public void heepayNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
    	logger.info(String.format("******[%s]********[%s]*******[%s]********","汇元宝通知",BusinessHelper.gettimestamp(),"Starting"));
    	long begin = System.currentTimeMillis(); // 这段代码放在程序执行前
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
			params.put(name, valueStr);
		}
		String goods_no = request.getParameter("agent_bill_id");
		if (StringUtils.isBlank(goods_no)) {
			logger.error(String.format("get heepay notify goods_no [%s]", goods_no));
			 response.getOutputStream().write("error".getBytes());
			return;
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
        	 response.getOutputStream().write("error".getBytes());
        	return;
        }
		String orderId = payReckoning.getOrder_id();
        logger.info(String.format("get heepay notify reckoningId [%s] trade_no [%s] orderId [%s] trade_status [%s]",out_trade_no, trade_no,orderId,trade_status));
		String mySign = Heepay.sign(trade_no,out_trade_no,pay_amt);
		logger.info(String.format("get heepay notify to create mySign[%s] pay_amt [%s] sign [%s]",mySign, pay_amt, sign));
		if(mySign.equals(sign)){//验证成功
			int payStatus = payReckoning.getPay_status();
			if(payStatus == 0){ //0未支付;1支付成功
				 if (trade_status.equals("1")){
					logger.info("支付成功 修改订单的支付状态,TRADE_SUCCESS");
					payLogicService.updatePaymentStatus(payReckoning,out_trade_no,trade_no,"Hee","");
					response.getOutputStream().write("ok".getBytes());
					return;
				}else{
					logger.info("支付失败！");	//请不要修改或删除
					 response.getOutputStream().write("error".getBytes());
					return;
				}
			}
			
			 long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
			 logger.info(orderId+"微信通知总耗时：" + end + "毫秒");
			 response.getOutputStream().write("ok".getBytes());
			return;
		}else{//验证失败
			logger.info(String.format("get heepay notify  mysign [%s] sign [%s] sign verify fail",mySign, sign));
			 long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
			 logger.info(orderId+"汇元通知总耗时：" + end + "毫秒");
			 response.getOutputStream().write("error".getBytes());
			return;
		}
    }
    
    /***************************Hee end**************************************/
    
    /**
     * 米大师通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
   	@RequestMapping(value = "/payment/midasNotifySuccess", method = { RequestMethod.GET,RequestMethod.POST })
   	public void midasNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
    	logger.info(String.format("******[%s]********[%s]*******[%s]********","米大师midasNotifySuccess",BusinessHelper.gettimestamp(),"Starting"));
    	long begin = System.currentTimeMillis(); // 这段代码放在程序执行前
   		MidasRespone result = null;
   		result = new MidasRespone(1,"通知订单信息无效");
    	HashMap<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		if(requestParams.isEmpty()){
			result.setMsg("通知参数为空");
			result.setRet(4);
			SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
			return;
		}
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		
		String goods_no = request.getParameter("payitem");
		String notify_url = PayHttpService.MIDAS_NOTIFY_URL;
		//商户订单号
		String out_trade_no = BusinessHelper.formatPayItem(payHttpService.getEnv(), goods_no);
		//交易号 (米大师给的微信流水号)
		String trade_no = new String(request.getParameter("cftid").getBytes("ISO-8859-1"),"UTF-8");
		//交易号(米大师流水号)
		String billno = new String(request.getParameter("billno").getBytes("ISO-8859-1"),"UTF-8");
		//交易签名
		String sign = new String(request.getParameter("sig").getBytes("ISO-8859-1"),"UTF-8");
		long select_ID_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
		PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
		long select_ID_end = System.currentTimeMillis() - select_ID_begin; // 这段代码放在程序执行后
		logger.info(out_trade_no+"查询订单是否存在耗时：" + select_ID_end + "毫秒");
		if (payReckoning == null) {
        	logger.info("get midas notice payReckoning " +payReckoning);
        	SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
			return;
        }
		String orderId = payReckoning.getOrder_id();
        logger.info(String.format("get midas notify reckoningId [%s] trade_no [%s] orderId [%s] billno [%s]",out_trade_no, trade_no,orderId,billno));
		boolean verifySig = MidasUtils.verifySig(params,notify_url,sign);
		logger.info(String.format("get midas notify to create verifySig[%s]",verifySig));
		if(verifySig){//验证成功
			int payStatus = payReckoning.getPay_status();
			if(payStatus == 0){ //0未支付;1支付成功
				logger.info("支付成功 修改订单的支付状态,TRADE_SUCCESS");
				payLogicService.updatePaymentStatus(payReckoning,out_trade_no,trade_no,"Midas",billno);
				result.setMsg("OK");
				result.setRet(0);
				SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
				return;
			}else{
				PaymentReckoningVTO paidPaymentOrder = payLogicService.findPaymentByRemark(billno);
				if(paidPaymentOrder != null){
					logger.info("订单支付成功状态已修改完成,记录 midas 再次发送回掉通知信息out_trade_no[%s],  billno[%s],trade_no[%s]",out_trade_no,billno,trade_no);
					payLogicService.updatePaymentStatusBackup(payReckoning,out_trade_no,trade_no,"Midas",billno);
				}else{
					logger.info("订单支付成功状态已修改完成,记录 midas 再次发送回掉通知信息out_trade_no[%s],  billno[%s],trade_no[%s]",out_trade_no,billno,trade_no);
					payLogicService.updatePaymentStatus(payReckoning,out_trade_no,trade_no,"Midas",billno);
				}
				result.setMsg("OK");
				result.setRet(0);
				SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
				return;
			}
		}else{//验证失败
			logger.info(String.format("get midas notifysign [%s] verify fail", sign));
			result.setMsg("请求参数错误：（sig）");
			result.setRet(2);
			SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
			 long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
			 logger.info(orderId+"汇元通知总耗时：" + end + "毫秒");
			return;
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
   		String locationUrl = PayHttpService.WEB_NOTIFY_URL;
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		if(requestParams.isEmpty()){
			logger.error(String.format("user canceled this payment ,get heepay return notify locationUrl [%s] requestParams [%s]", locationUrl, requestParams));
			response.sendRedirect(locationUrl);
			return;
		}
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		
		String pay_amt = request.getParameter("pay_amt");
		String result = request.getParameter("result");
		String fbtn = request.getParameter("fbtn");
		if(fbtn == null){
			fbtn = "";
		}
		if(pay_amt.equals("0.00")||result.equals("0") || fbtn.equals("goback")){
			String pay_message = request.getParameter("pay_message");
			logger.info(String.format("get heepay return notify.user canceled this pay and go to locationUrl [%s] pay_message[%s]", locationUrl,pay_message));
			response.sendRedirect(locationUrl);
			return;
		}
		//商户订单号
		String out_trade_no = new String(request.getParameter("agent_bill_id").getBytes("ISO-8859-1"),"UTF-8");
		if (StringUtils.isBlank(out_trade_no)) {
			logger.error(String.format("get heepay return notify out_trade_no [%s]", out_trade_no));
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
		}
		String returnUrl = paymentAlipaylocationService.getLocationByTid(out_trade_no);
		if(StringUtils.isNotBlank(returnUrl)){
			if(returnUrl.startsWith("http")){
				locationUrl = returnUrl;
			}
		}
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
	@RequestMapping(value = "/payment/heePrepaidReturn" , method = { RequestMethod.GET,RequestMethod.POST })
   	public void heePrepaidReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","汇元宝充值返回通知",BusinessHelper.gettimestamp(),"Starting"));
   		String locationUrl = PayHttpService.PREPAID_NOTIFY_URL;
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		if(requestParams.isEmpty()){
			logger.error(String.format("user canceled this payment ,get heepay return notify locationUrl [%s] requestParams [%s]", locationUrl, requestParams));
		}
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		
		String pay_amt = request.getParameter("pay_amt");
		String result = request.getParameter("result");
		String fbtn = request.getParameter("fbtn");
		if(fbtn == null){
			fbtn = "";
		}
		if(pay_amt.equals("0.00")||result.equals("0") || fbtn.equals("goback")){
			String pay_message = request.getParameter("pay_message");
			logger.info(String.format("get heepay return notify.user canceled this pay and go to locationUrl [%s] pay_message[%s]", locationUrl,pay_message));
			response.sendRedirect(locationUrl);
			return;
		}
		String out_trade_no = new String(request.getParameter("agent_bill_id").getBytes("ISO-8859-1"),"UTF-8");
		if (StringUtils.isBlank(out_trade_no)) {
			logger.error(String.format("get heepay return notify out_trade_no [%s]", out_trade_no));
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
		}
		String returnUrl = paymentAlipaylocationService.getLocationByTid(out_trade_no);
		if(StringUtils.isNotBlank(returnUrl)){
			if(returnUrl.startsWith("http")){
				locationUrl = returnUrl;
			}
		}
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
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","接收支付宝打赏返回通知",BusinessHelper.gettimestamp(),"Starting"));
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
			logger.info("N:"+name+"V:"+valueStr);
			params.put(name, valueStr);
		}
		String isNull = request.getParameter("out_trade_no");
		String locationUrl = PayHttpService.WEB_NOTIFY_URL;
		if (StringUtils.isBlank(isNull)) {
			logger.info(String.format("get alipay return notify and go to out_trade_no [%s] ,user canceled this pay.", isNull));
			response.sendRedirect(locationUrl);
			return;
		}
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
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
     * 现在支付通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
   	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/payment/nowpayReturn" , method = { RequestMethod.GET,RequestMethod.POST })
   	public void nowpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","接收现在支付打赏返回通知",BusinessHelper.gettimestamp(),"Starting"));
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
			logger.info("N:"+name+"V:"+valueStr);
			params.put(name, valueStr);
		}
		String locationUrl = PayHttpService.WEB_NOTIFY_URL;
		String isNull = request.getParameter("mhtOrderNo");
		if (StringUtils.isBlank(isNull)) {
			logger.info(String.format("get nowpay return notify out_trade_no [%s]", isNull));
			response.sendRedirect(locationUrl);
			return;
		}
		String transStatus = request.getParameter("transStatus");
		if(!transStatus.equals("A001")){
			logger.info(String.format("get nowpay return notify.user canceled this pay and go to locationUrl [%s] reckoning_Id[%s]", locationUrl,isNull));
			response.sendRedirect(locationUrl);
			return;
		}
		String out_trade_no = new String(request.getParameter("mhtOrderNo").getBytes("ISO-8859-1"),"UTF-8");
		String returnUrl = paymentAlipaylocationService.getLocationByTid(out_trade_no);
		if(StringUtils.isNotBlank(returnUrl)){
			if(returnUrl.startsWith("http")){
				locationUrl = returnUrl;
			}
		}
		logger.info(String.format("get nowpay return notify and go to locationUrl [%s]", locationUrl));
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
	@RequestMapping(value = "/payment/alipayPrepaidReturn" , method = { RequestMethod.GET,RequestMethod.POST })
   	public void alipayPrepaidReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","接收支付宝充值返回通知",BusinessHelper.gettimestamp(),"Starting"));
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
			params.put(name, valueStr);
		}
		String isNull = request.getParameter("out_trade_no");
		String locationUrl = PayHttpService.PREPAID_NOTIFY_URL;
		if (StringUtils.isBlank(isNull)) {
			logger.info(String.format("get alipay return notify and go to out_trade_no [%s] ,user canceled this pay.", isNull));
			response.sendRedirect(locationUrl);
			return;
		}
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
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
     * 支付宝通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
   	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/payment/nowpayPrepaidReturn" , method = { RequestMethod.GET,RequestMethod.POST })
   	public void nowpayPrepaidReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","接收现在支付充值返回通知",BusinessHelper.gettimestamp(),"Starting"));
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
			params.put(name, valueStr);
		}
		
		String locationUrl = PayHttpService.PREPAID_NOTIFY_URL;
		String isNull = request.getParameter("mhtOrderNo");
		if (StringUtils.isBlank(isNull)) {
			logger.error(String.format("get nowpay return notify out_trade_no [%s]", isNull));
			return;
		}
		String transStatus = request.getParameter("transStatus");
		if(!transStatus.equals("A001")){
			logger.info(String.format("get nowpay return notify.user canceled this pay and go to locationUrl [%s] reckoning_Id[%s]", locationUrl,isNull));
			response.sendRedirect(locationUrl);
			return;
		}
		String out_trade_no = new String(request.getParameter("mhtOrderNo").getBytes("ISO-8859-1"),"UTF-8");
		String returnUrl = paymentAlipaylocationService.getLocationByTid(out_trade_no);
		if(StringUtils.isNotBlank(returnUrl)){
			if(returnUrl.startsWith("http")){
				locationUrl = returnUrl;
			}
		}
		logger.info(String.format("get nowpay return notify and go to locationUrl [%s]", locationUrl));
		response.sendRedirect(locationUrl);
	}
   	
   	/**
     * 米大师通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
	@RequestMapping(value = "/payment/midasReturn" , method = { RequestMethod.GET,RequestMethod.POST })
   	public void midasReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","米大师备用通知midasReturn",BusinessHelper.gettimestamp(),"Starting"));
   		long begin = System.currentTimeMillis(); // 这段代码放在程序执行前
   		MidasRespone result = null;
   		result = new MidasRespone(1,"通知订单信息无效");
    	HashMap<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		if(requestParams.isEmpty()){
			result.setMsg("通知参数为空");
			result.setRet(4);
			SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
			return;
		}
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		
		String goods_no = request.getParameter("payitem");
		String notify_url = PayHttpService.MIDAS_RETURN_URL;
		//商户订单号
		String out_trade_no = BusinessHelper.formatPayItem(payHttpService.getEnv(), goods_no) ;
		//交易号 (米大师给的微信流水号)
		String trade_no = new String(request.getParameter("cftid").getBytes("ISO-8859-1"),"UTF-8");
		//交易号(米大师流水号)
		String billno = new String(request.getParameter("billno").getBytes("ISO-8859-1"),"UTF-8");
		//交易签名
		String sign = new String(request.getParameter("sig").getBytes("ISO-8859-1"),"UTF-8");
		PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
		if (payReckoning == null) {
        	logger.info("get midas notice payReckoning " +payReckoning);
        	SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
			return;
        }
		String orderId = payReckoning.getOrder_id();
        logger.info(String.format("get midas notify reckoningId [%s] trade_no [%s] orderId [%s] billno [%s]",out_trade_no, trade_no,orderId,billno));
		boolean verifySig = MidasUtils.verifySig(params,notify_url,sign);
		logger.info(String.format("get midas notify to create verifySig[%s]",verifySig));
		if(verifySig){//验证成功
			int payStatus = payReckoning.getPay_status();
			if(payStatus == 0){ //0未支付;1支付成功
				logger.info("支付成功 修改订单的支付状态,TRADE_SUCCESS");
				payLogicService.updatePaymentStatus(payReckoning,out_trade_no,trade_no,"Midas",billno);
				result.setMsg("OK");
				result.setRet(0);
				SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
				return;
			}else{
				PaymentReckoningVTO paidPaymentOrder = payLogicService.findPaymentByRemark(billno);
				if(paidPaymentOrder != null){
					logger.info("订单支付成功状态已修改完成,记录 midas 再次发送回掉通知信息out_trade_no[%s],  billno[%s],trade_no[%s]",out_trade_no,billno,trade_no);
					payLogicService.updatePaymentStatusBackup(payReckoning,out_trade_no,trade_no,"Midas",billno);
				}else{
					logger.info("订单支付成功状态已修改完成,记录 midas 再次发送回掉通知信息out_trade_no[%s],  billno[%s],trade_no[%s]",out_trade_no,billno,trade_no);
					payLogicService.updatePaymentStatus(payReckoning,out_trade_no,trade_no,"Midas",billno);
				}
				result.setMsg("OK");
				result.setRet(0);
				SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
				long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
				logger.info(orderId+"米大师通知总耗时：" + end + "毫秒");
				return;
			}
		}else{//验证失败
			logger.info(String.format("get midas notifysign [%s] verify fail", sign));
			result.setMsg("请求参数错误：(sig)");
			result.setRet(2);
			SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(result));
			 long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
			 logger.info(orderId+"米大师通知总耗时：" + end + "毫秒");
			return;
		}
	}
}
