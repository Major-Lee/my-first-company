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

import com.alipay.util.AlipayNotify;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreateWithdrawDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.PaymentSceneChannelType;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
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
import com.bhu.vas.web.http.response.PaySuccessNotifyResponse;
import com.bhu.vas.web.service.AlipayService;
import com.bhu.vas.web.service.AppWeixinService;
import com.bhu.vas.web.service.HeeService;
import com.bhu.vas.web.service.NativeWeixinService;
import com.bhu.vas.web.service.NowService;
import com.bhu.vas.web.service.PayHttpService;
import com.bhu.vas.web.service.PayLogicService;
import com.bhu.vas.web.service.PayPalService;
import com.bhu.vas.web.service.WithdrawalsService;
import com.heepay.api.Heepay;
import com.nowpay.config.NowpayConfig;
import com.nowpay.sign.MD5Facade;
import com.nowpay.util.FormDateReportConvertor;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.web.business.helper.BusinessWebHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.PaymentResponseSuccess;
import com.smartwork.msip.jdo.Response;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

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
	    

	@Autowired
	PayPalService payPalService;
	@Autowired
	HeeService heeService;
	@Autowired
	NowService nowService;
	@Autowired
	NativeWeixinService nativeWeixinService;
	@Autowired
	AlipayService alipayService;
	@Autowired
	AppWeixinService appWeixinService;
	@Autowired
	PayLogicService payLogicService;
	@Autowired
    PayHttpService payHttpService;
	@Resource
	PaymentReckoningService paymentReckoningService;
	@Resource
	PaymentWithdrawService paymentWithdrawService;
	@Resource
	WithdrawalsService withdrawalsService;
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"queryOrderPayStatus接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.ERROR);
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
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	if (StringUtils.isBlank(appid)) {
        		logger.error(String.format("apply payment appid[%s]", appid));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
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
        					ResponseErrorCode.USER_APPID_UNSUPPORT), BusinessWebHelper.getLocale(request)));
					return;
				}
			}else{
				logger.error(String.format("apply payment appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_USERORPWD_ERROR), BusinessWebHelper.getLocale(request)));
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
						ResponseErrorCode.INVALID_COMMDITY_ORDERID_UNSUPPORT), BusinessWebHelper.getLocale(request)));
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"updatePaymentStatus接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.ERROR);
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
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
				return;
			}
			if (StringUtils.isBlank(value)) {
				logger.error(String.format("apply payment value[%s]", value));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
				return;
			}
			if (StringUtils.isBlank(rate)) {
				logger.error(String.format("apply payment rate[%s]", rate));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
				return;
			}
			if (StringUtils.isBlank(secret)) {
				logger.error(String.format("apply payment secret[%s]", secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
				return;
			}
			if (StringUtils.isBlank(appid)) {
				logger.error(String.format("apply payment appid[%s]", appid));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
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
							ResponseErrorCode.USER_APPID_UNSUPPORT), BusinessWebHelper.getLocale(request)));
					return;
				}
			}else{
				logger.error(String.format("apply payment appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.VALIDATE_USERORPWD_ERROR), BusinessWebHelper.getLocale(request)));
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"updateChangeRate接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.ERROR);
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
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
				return;
			}
			if (StringUtils.isBlank(appid)) {
				logger.error(String.format("apply payment judgmentChannels appid[%s]", appid));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
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
							ResponseErrorCode.USER_APPID_UNSUPPORT), BusinessWebHelper.getLocale(request)));
					return;
				}
			}else{
				logger.error(String.format("apply payment judgmentChannels appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.VALIDATE_USERORPWD_ERROR), BusinessWebHelper.getLocale(request)));
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"judgmentChannels接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.ERROR);
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
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	if (StringUtils.isBlank(appid)) {
        		logger.error(String.format("apply prepaid appid[%s]", appid));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
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
        					ResponseErrorCode.USER_APPID_UNSUPPORT), BusinessWebHelper.getLocale(request)));
					return;
				}
			}else{
				logger.error(String.format("apply prepaid appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_USERORPWD_ERROR), BusinessWebHelper.getLocale(request)));
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
						ResponseErrorCode.INVALID_COMMDITY_ORDERID_UNSUPPORT), BusinessWebHelper.getLocale(request)));
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			SendMailHelper.doSendMail(3,"updatePrepaidStatus接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.ERROR);
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
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	if (StringUtils.isBlank(appid)) {
        		logger.error(String.format("apply withdrawals appid[%s]", appid));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
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
    					ResponseErrorCode.VALIDATE_USERORPWD_ERROR), BusinessWebHelper.getLocale(request)));
				return;
			}
			
        	if (StringUtils.isBlank(withdraw_type)) {
    			logger.error(String.format("apply withdrawals withdraw_type [%s]", withdraw_type));
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
    			return;
    		}
        	if (StringUtils.isBlank(total_fee)) {
        		logger.error(String.format("apply withdrawals total_fee[%s]", total_fee));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	if (StringUtils.isBlank(userId)) {
        		logger.error(String.format("apply withdrawals userId[%s]", userId));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	if (StringUtils.isBlank(withdraw_no)) {
        		logger.error(String.format("apply withdrawals withdraw_no[%s]", withdraw_no));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	
        	if (StringUtils.isBlank(secret)) {
        		logger.error(String.format("apply withdrawals secret[%s]", secret));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	
        	String total_fee_fen = BusinessHelper.getMoney(total_fee);
        	int temp = Integer.parseInt(total_fee_fen);
//        	if(temp < 1000){
//        		logger.error(String.format("apply withdrawals total_fee[%s] errorMsg:[%s] , [%s]", total_fee,ResponseErrorCode.USER_WALLET_WITHDRAW_LOWERTHEN_MINLIMIT.i18n(),"10元"));
//        		payLogicService.updateWithdrawalsStatus(null, withdraw_no, withdraw_type,false);
//				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
//    					ResponseErrorCode.USER_WALLET_WITHDRAW_LOWERTHEN_MINLIMIT,new String[]{"10元"}), BusinessWebHelper.getLocale(request)));
//        		return;
//        	}
        	
    		PaymentWithdraw paymentWithdraw = paymentWithdrawService.findByOrderId(withdraw_no);
    		if(paymentWithdraw != null){
    			logger.error(String.format("apply withdrawals paymentWithdraw [%s]", paymentWithdraw));
        		throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST,new String[]{""}); 
        	}
        	ResponseCreateWithdrawDTO result = null;
        	if(withdraw_type.equals("weixin")){ //微信支付
        		result =  withdrawalsService.doWxWithdrawals(request,response,total_fee,withdraw_no,exter_invoke_ip,userId,userName);
        	}else{//提示暂不支持的支付方式
        		logger.info(String.format("apply withdrawals withdraw_type [%s]",withdraw_type + ResponseErrorCode.RPC_MESSAGE_UNSUPPORT));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT), BusinessWebHelper.getLocale(request)));
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
			logger.error(String.format("submitWithdrawals catch BusinessI18nCodeException [%s]",ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request))));
			SendMailHelper.doSendMail(2,"submitWithdrawals接口："+i18nex.getMessage()+i18nex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			logger.error(String.format("submitWithdrawals catch Exception [%s]",ResponseError.ERROR));
			SendMailHelper.doSendMail(2,"submitWithdrawals接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.ERROR);
		}finally{
			
		}
	}
	
	
	

	
	@ResponseBody
	@RequestMapping(value={"/payment/getPaymentUrl","/payUrl"},method={RequestMethod.GET,RequestMethod.POST})
    public void getPaymentUrl(
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
    		@RequestParam(required = false, value = "") String payment_name,
    		@RequestParam(required = false, value = "") String ot,
    		@RequestParam(required = false, value = "") String fee_type){ 
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info(String.format("get Payment Url goods_no [%s]", goods_no));
		
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
        		logger.error(String.format("get Payment Url appid [%s]", appid));
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
    			return;
    		}
			if (StringUtils.isBlank(secret)) {
        		logger.error(String.format("get Payment Url secret [%s]", secret));
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
    			return;
    		}
			logger.info(String.format("get Payment Url bussiness appid[%s] secret[%s]", appid,secret));
			int appId = Integer.parseInt(appid);
			boolean isAllowedBusiness = BusinessEnumType.CommdityApplication.verifyed(appId, secret);
			if(isAllowedBusiness){
				CommdityApplication app = BusinessEnumType.CommdityApplication.fromKey(appId);
				switch(app){
    			case BHU_PREPAID_BUSINESS:
    				if (StringUtils.isBlank(payment_name)) {
    					payment_name = "虎钻";
    				}
    				break;
    			case DEFAULT: 
    				if (StringUtils.isBlank(payment_name)) {
    					payment_name = "打赏";
    				}
    				break;
    			default:
    				payment_name = "打赏";
    				break;
				}
			}else{
				logger.error(String.format("get Payment Url appid[%s] secret[%s]", appid,secret));
				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_USERORPWD_ERROR), BusinessWebHelper.getLocale(request)));
				return;
			}
        	if (StringUtils.isBlank(payment_type)) {
        		logger.error(String.format("get Payment Url payment_type [%s]", payment_type));
    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
    			return;
    		}
        	if (StringUtils.isBlank(total_fee)) {
        		logger.error(String.format("get Payment Url total_fee [%s]", total_fee));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        		return;
        	}else{
        		logger.error(String.format("get Payment Url total_fee [%s]", total_fee));
        		double total_fees = Double.parseDouble(BusinessHelper.getMoney(total_fee));
        		if(payment_type.contains("Alipay")){
        			if(total_fees >= 1000000000){
        				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
            					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        				return;
        			}
        		}else if(payment_type.equalsIgnoreCase("WapWeixin")){
        			if(total_fees >= 300000){
        				if(!channel.equals("2")){
        					SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
        							ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        					return;
        				}
        			}
        		}else{
        			if(total_fees >= 2000000){
        				SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
            					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        				return;
        			}
        		}
        	}
        	
        	
        	if (StringUtils.isBlank(goods_no)) {
        		logger.error(String.format("get Payment Url goods_no [%s]", goods_no));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	
        	PaymentTypeVTO result = null;
        	//逻辑处理
        	long select_isExist_begin = System.currentTimeMillis(); // 这段代码放在程序执行前
    		PaymentReckoning paymentReckoning = paymentReckoningService.findByOrderId(goods_no);
    		long select_isExist_end = System.currentTimeMillis() - select_isExist_begin; // 这段代码放在程序执行后
    		logger.info(goods_no+"查询订单是否存在耗时：" + select_isExist_end + "毫秒");
        	if(paymentReckoning != null){
        		logger.error(String.format("get Payment Url goods_no [%s]", goods_no+ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST));
        		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    					ResponseErrorCode.VALIDATE_PAYMENT_DATA_ALREADY_EXIST), BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	umac = BusinessHelper.formatMac(umac);
        	PaymentChannelCode paymentChannel = PaymentChannelCode.getPaymentChannelCodeByCode(payment_type);
    		switch(paymentChannel){
    			case BHU_PC_WEIXIN: //PC微信支付
    				long PC_WEIXIN_begin = System.currentTimeMillis();
    				result =  nativeWeixinService.doNativeWxPayment(request,response,"BHU",total_fee,goods_no,exter_invoke_ip,payment_completed_url,umac,payment_name,appid);
    				long PC_WEIXIN_end = System.currentTimeMillis() - PC_WEIXIN_begin; 
    				logger.info(goods_no+"PC微信支付耗时：" + PC_WEIXIN_end + "毫秒");
    				break;
    			case BHU_PC_ALIPAY: //PC支付宝
    				long PC_ALIPAY_begin = System.currentTimeMillis();
    				result =  alipayService.doAlipay(response,request, total_fee, goods_no,payment_completed_url,exter_invoke_ip,payment_type,umac,payment_name,appid,ot);
    				long PC_ALIPAY_end = System.currentTimeMillis() - PC_ALIPAY_begin; 
    				logger.info(goods_no+"PC支付宝耗时：" + PC_ALIPAY_end + "毫秒");
    				break;
    			case BHU_APP_WEIXIN: //App微信支付
    				long APP_WEIXIN_begin = System.currentTimeMillis();
    				result =  appWeixinService.doAppPayment(request,response,total_fee,goods_no,exter_invoke_ip,payment_completed_url,umac,payment_name,appid,channel);
    				long APP_WEIXIN_end = System.currentTimeMillis() - APP_WEIXIN_begin; 
    				logger.info(goods_no+"APP微信支付耗时：" + APP_WEIXIN_end + "毫秒");
    				break;
    			case BHU_APP_ALIPAY: //App支付宝
    				long APP_ALIPAY_begin = System.currentTimeMillis();
    				result =  alipayService.doAlipay(response,request, total_fee, goods_no,payment_completed_url,exter_invoke_ip,payment_type,umac,payment_name,appid,ot);
    				long APP_ALIPAY_end = System.currentTimeMillis() - APP_ALIPAY_begin; 
    				logger.info(goods_no+"App支付宝耗时：" + APP_ALIPAY_end + "毫秒");
    				break;
    			case BHU_WAP_PAYPAL: //Wap Paypal
    				if (StringUtils.isBlank(fee_type)) {
    	        		logger.error(String.format("get Payment Url payment_type [%s] ,fee_type [%s]", payment_type,fee_type));
    	    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    	    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
    	    			return;
    	    		}
    				if(fee_type.equals("CNY")){
    					logger.error(String.format("get Payment Url payment_type [%s] ,fee_type [%s]", payment_type,fee_type));
    	    			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
    	    					ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL), BusinessWebHelper.getLocale(request)));
    	    			return;
    				}
    				long WAP_PAYPAL_begin = System.currentTimeMillis();
    				result =  payPalService.doPaypal(response,request,version, total_fee, goods_no,exter_invoke_ip,payment_completed_url,umac,payment_name,appid,fee_type); 
    				long WAP_PAYPAL_end = System.currentTimeMillis() - WAP_PAYPAL_begin; 
    				logger.info(goods_no+"Wap Paypal耗时：" + WAP_PAYPAL_end + "毫秒");
    				break;
    			case BHU_WAP_WEIXIN:
    				if(channel.equals(PaymentSceneChannelType.WAPH5.getName())){
    					long get_agentMerchant_begin = System.currentTimeMillis();
    					String agentMerchant = payLogicService.findWapWeixinMerchantServiceByCondition();
    					long get_agentMerchant_end = System.currentTimeMillis() -  get_agentMerchant_begin; 
        				logger.info(goods_no+"查询WAP微信获取支付渠道耗时：" + get_agentMerchant_end + "毫秒");
        				
        				if(agentMerchant.equals("Now")){
        					long WAP_WEIXIN_Now_begin = System.currentTimeMillis();
        					result =  nowService.doNow(response,"Now", total_fee, goods_no,exter_invoke_ip,payment_completed_url,umac,payment_name,appid); 
        					long WAP_WEIXIN_Now_end = System.currentTimeMillis() - WAP_WEIXIN_Now_begin; 
            				logger.info(goods_no+"Wap微信现在支付获取支付url耗时：" + WAP_WEIXIN_Now_end + "毫秒");
            				long end = System.currentTimeMillis() - begin;
            	    		logger.info(goods_no+"Wap微信现在支付，逻辑处理完成耗时：" + end + "毫秒！！！！！！");
        				}else if(agentMerchant.equals("Hee")){
        					long WAP_WEIXIN_Hee_begin = System.currentTimeMillis();
        					result =  heeService.doHee(response,"Hee", total_fee, goods_no,exter_invoke_ip,payment_completed_url,umac,payment_name,appid); 
        					long WAP_WEIXIN_Hee_end = System.currentTimeMillis() - WAP_WEIXIN_Hee_begin; 
            				logger.info(goods_no+"Wap微信汇元支付获取支付url耗时：" + WAP_WEIXIN_Hee_end + "毫秒");
        				}else{
        					long WAP_WEIXIN_Now_begin = System.currentTimeMillis();
        					result =  nowService.doNow(response,"Now", total_fee, goods_no,exter_invoke_ip,payment_completed_url,umac,payment_name,appid); 
        					long WAP_WEIXIN_Now_end = System.currentTimeMillis() - WAP_WEIXIN_Now_begin; 
            				logger.info(goods_no+"Wap微信现在支付获取支付url耗时：" + WAP_WEIXIN_Now_end + "毫秒");
            				long end = System.currentTimeMillis() - begin;
            	    		logger.info(goods_no+"Wap微信现在支付，逻辑处理完成耗时：" + end + "毫秒！！！！！！");
        				}
    				}else if(channel.equals(PaymentSceneChannelType.WAPQR.getName())){
    					long WAP_WEIXIN_NATIVE_begin = System.currentTimeMillis();
    					result =  nativeWeixinService.doNativeWxPayment(request,response,channel,total_fee,goods_no,exter_invoke_ip,payment_completed_url,umac,payment_name,appid);
    					long WAP_WEIXIN_NATIVE_end = System.currentTimeMillis() -  WAP_WEIXIN_NATIVE_begin; 
        				logger.info(goods_no+"WAP微信他人代付耗时：" + WAP_WEIXIN_NATIVE_end + "毫秒");
						
    				}else{
    					logger.info(String.format("get Payment Url payment_type [%s]",payment_type + ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
	        					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT), BusinessWebHelper.getLocale(request))));
						
    				}
                	break;
    			case BHU_WAP_ALIPAY: //Wap微信支付
    				long WAP_ALIPAY_begin = System.currentTimeMillis();
    				result =  alipayService.doAlipay(response,request, total_fee, goods_no,payment_completed_url,exter_invoke_ip,payment_type,umac,payment_name,appid,ot);
    				long WAP_ALIPAY_end = System.currentTimeMillis() - WAP_ALIPAY_begin; 
    				logger.info(goods_no+"Wap支付宝支付耗时：" + WAP_ALIPAY_end + "毫秒");
    				break;
    			default:
    				logger.info(String.format("get Payment Url payment_type [%s]",payment_type + ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
        					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT), BusinessWebHelper.getLocale(request))));
            		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
        					ResponseErrorCode.RPC_MESSAGE_UNSUPPORT), BusinessWebHelper.getLocale(request)));
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
    			logger.info(String.format("get Payment Url return result [%s]",JsonHelper.getJSONString(respone)));
    			SpringMVCHelper.renderJson(response, JsonHelper.getJSONString(respone));
    		}else{
    			logger.info(String.format("get Payment Url return result [%s]",JsonHelper.getJSONString(result)));
    			SpringMVCHelper.renderJson(response, PaymentResponseSuccess.embed(JsonHelper.getJSONString(result)));
    		}
		}catch(BusinessI18nCodeException i18nex){
			logger.error(String.format("get Payment Url catch BusinessI18nCodeException [%s]",ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request))));
			SendMailHelper.doSendMail(3,"get Payment Url接口："+i18nex.getMessage()+i18nex.getCause());
		}catch(Exception ex){
			logger.error(String.format("get Payment Url catch Exception [%s]",JsonHelper.getJSONString(ResponseError.ERROR)));
			//SendMailHelper.doSendMail(3,"submitPayment接口："+ex.getMessage()+ex.getCause());
			SpringMVCHelper.renderJson(response, ResponseError.ERROR);
		}finally{
			
		}
		long end = System.currentTimeMillis() - begin;
		logger.info(goods_no+"请求总耗时：" + end + "毫秒！！！！！！");
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
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
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
    					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
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
     * paypal通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
   	@RequestMapping(value = "/payment/paypalNotifySuccess", method = { RequestMethod.GET,RequestMethod.POST })
   	public void paypalNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
    	logger.info(String.format("******[%s]********[%s]*******[%s]********","PayPalNotifySuccess",BusinessHelper.gettimestamp(),"Starting"));
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
			System.out.println("get paypal notify success"+name + "  "+ valueStr);
			logger.info("get paypal notify success"+name + "  "+ valueStr);
		}
		
		String isNull = request.getParameter("out_trade_no");
		String locationUrl = PayHttpService.WEB_RETURN_URL;
		if (StringUtils.isBlank(isNull)) {
			logger.info(String.format("get paypal return notify and go to out_trade_no [%s] ,user canceled this pay.", isNull));
			response.sendRedirect(locationUrl);
			return;
		}
//		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//		String returnUrl = paymentAlipaylocationService.getLocationByTid(out_trade_no);
//		if(StringUtils.isNotBlank(returnUrl)){
//			if(returnUrl.startsWith("http")){
//				locationUrl = returnUrl;
//			}
//		}
		logger.info(String.format("get paypal return notify and go to locationUrl [%s]", locationUrl));
		response.sendRedirect(locationUrl);
		
//		String isNull = request.getParameter("out_trade_no");
//		if (isNull == null) {
//			logger.error(String.format("get paypal notify out_trade_no  [%s]", isNull));
//			return;
//		}
		//商户订单号
//		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//		//支付宝交易号
//		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
//		//交易状态
//		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
//		PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
//		if (payReckoning == null) {
//        	logger.info("get alipay notice payReckoning " +payReckoning);
//        	return;
//        }
//		String orderId = payReckoning.getOrder_id();
//        logger.info(String.format("get paypal notify reckoningId [%s] trade_no [%s] orderId [%s] trade_status [%s]",out_trade_no, trade_no,orderId,trade_status));
		//SpringMVCHelper.renderJson(response, "success");
		long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
//		logger.info(payReckoning.getOrder_id()+"支付宝回调通知总耗时：" + end + "毫秒");
		logger.info("paypal回调通知总耗时：" + end + "毫秒");
		return;
    }
   	/***************************Paypal end**************************************/
    
   	/**
   	 * paypal通知接口
   	 * @param mv
   	 * @param request
   	 * @param response
   	 * @throws IOException
   	 * @throws JDOMException
   	 */
   	@RequestMapping(value = "/payment/paypalWebHooks", method = { RequestMethod.GET,RequestMethod.POST })
   	public void paypalWebHooks(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","paypalWebHooks",BusinessHelper.gettimestamp(),"Starting"));
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
   			System.out.println("get paypal notify success"+name + "  "+ valueStr);
   			logger.info("get paypal notify success"+name + "  "+ valueStr);
   		}
   		
//		try {
//			SSLParameters sslParams = SSLContext.getDefault().getSupportedSSLParameters();
//			String[] protocols = sslParams.getProtocols();
//			System.out.println("Supported protocol versions: " + Arrays.asList(protocols));
//			logger.info("Supported protocol versions: " + Arrays.asList(protocols));
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//		if (TlsCheck.isSuccessfulTLS12Connection()) {
//			System.out.println("Successfully connected to TLS 1.2 endpoint.");
//			logger.info("Successfully connected to TLS 1.2 endpoint.");
//		} else {
//			System.out.println("Failed to connect to TLS 1.2 endpoint.");
//			logger.info("Failed to connect to TLS 1.2 endpoint.");
//		}
   		
//   		try{
//   			APIContext apiContext = new APIContext(clientID, clientSecret, mode);
//   			
//   			apiContext.addConfiguration(Constants.PAYPAL_WEBHOOK_ID, PayPalService.WebhookId);
//   			
//   			Boolean result = Event.validateReceivedEvent(apiContext, PayPalService.getHeadersInfo(request), PayPalService.getBody(request));
//   			
//   			System.out.println("Result is " + result);
//   			logger.info("Webhook Validated:  "+ result);
//   			ResultPrinter.addResult(request, response, "Webhook Validated:  ", CreditCard.getLastRequest(), CreditCard.getLastResponse(), null);
//   		} catch (PayPalRESTException e) {
//   			logger.error(e.getMessage());
//   			ResultPrinter.addResult(request, response, "Webhook Validated:  ", CreditCard.getLastRequest(), null, e.getMessage());
//   		} catch (InvalidKeyException e) {
//   			logger.error(e.getMessage());
//   			ResultPrinter.addResult(request, response, "Webhook Validated:  ", CreditCard.getLastRequest(), null, e.getMessage());
//   		} catch (NoSuchAlgorithmException e) {
//   			logger.error(e.getMessage());
//   			ResultPrinter.addResult(request, response, "Webhook Validated:  ", CreditCard.getLastRequest(), null, e.getMessage());
//   		} catch (SignatureException e) {
//   			logger.error(e.getMessage());
//   			ResultPrinter.addResult(request, response, "Webhook Validated:  ", CreditCard.getLastRequest(), null, e.getMessage());
//   		}
   		
//		String isNull = request.getParameter("out_trade_no");
//		if (isNull == null) {
//			logger.error(String.format("get paypal notify out_trade_no  [%s]", isNull));
//			return;
//		}
   		//商户订单号
//		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//		//支付宝交易号
//		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
//		//交易状态
//		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
//		PaymentReckoning payReckoning =  paymentReckoningService.getById(out_trade_no);
//		if (payReckoning == null) {
//        	logger.info("get alipay notice payReckoning " +payReckoning);
//        	return;
//        }
//		String orderId = payReckoning.getOrder_id();
//        logger.info(String.format("get paypal notify reckoningId [%s] trade_no [%s] orderId [%s] trade_status [%s]",out_trade_no, trade_no,orderId,trade_status));
   		//SpringMVCHelper.renderJson(response, "success");
   		long end = System.currentTimeMillis() - begin; // 这段代码放在程序执行后
//		logger.info(payReckoning.getOrder_id()+"支付宝回调通知总耗时：" + end + "毫秒");
   		logger.info("paypal回调通知总耗时：" + end + "毫秒");
   		return;
   	}
   	
   	/**
     * paypal通知接口
     * @param mv
     * @param request
     * @param response
     * @throws IOException
     * @throws JDOMException
     */
   	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/payment/paypalReturn" , method = { RequestMethod.GET,RequestMethod.POST })
   	public void paypalReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
   		logger.info(String.format("******[%s]********[%s]*******[%s]********","paypalReturn",BusinessHelper.gettimestamp(),"Starting"));
   		String locationUrl = PayHttpService.WEB_NOTIFY_URL;
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		if(requestParams.isEmpty()){
			logger.error(String.format("user canceled this payment ,get paypal return notify locationUrl [%s] requestParams [%s]", locationUrl, requestParams));
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
		
		String payerID = request.getParameter("PayerID");
		String paymentId = request.getParameter("paymentId");
		String accessToken = request.getParameter("token");
		logger.info(String.format("get paypal payerID [%s] paymentId [%s] accessToken [%s]",payerID,paymentId,accessToken));
		
		PaymentReckoning paymentReckoning = payLogicService.findPaymentByThirdInfo(paymentId);
		if(paymentReckoning == null){
			logger.error(String.format("user canceled this payment ,get paypal return notify locationUrl [%s] requestParams [%s]", locationUrl, requestParams));
			response.sendRedirect(locationUrl);
			return;
		}
		String out_trade_no =paymentReckoning.getId();
		payLogicService.updatePaymentStatus(paymentReckoning,out_trade_no,payerID,"PayPal",paymentId);
		String returnUrl = paymentAlipaylocationService.getLocationByTid(out_trade_no);
		if(StringUtils.isNotBlank(returnUrl)){
			if(returnUrl.startsWith("http")){
				locationUrl = returnUrl;
			}
		}
		response.sendRedirect(locationUrl);
	}
   	
   	/***************************Paypal end**************************************/
   	
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
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
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
					ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request)));
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
}