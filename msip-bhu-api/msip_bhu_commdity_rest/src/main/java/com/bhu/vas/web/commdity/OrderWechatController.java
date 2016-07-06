package com.bhu.vas.web.commdity;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.commdity.OrderSMSVTO;
import com.bhu.vas.api.helper.BusinessEnumType.CaptchaCodeActType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserCaptchaCodeRpcService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.web.business.validate.ValidateService;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/order/wechat")
public class OrderWechatController extends BaseController{
	
	@Resource
	private IUserCaptchaCodeRpcService userCaptchaCodeRpcService;
	
	@Resource
	private IOrderRpcService orderRpcService;
	/**
	 * wechat授权认证完成
	 * @param response
	 * @param countrycode
	 * @param acc
	 */
	@ResponseBody()
	@RequestMapping(value="/authorize_completed",method={RequestMethod.POST,RequestMethod.GET})
	public void fetch_captcha(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String auid,
			@RequestParam(required = true) String umac,
			@RequestParam(required = true) String mac,
			@RequestParam(required = false, defaultValue = "2") Integer umactype
			) {
		try{
			ValidateService.validateDeviceMac(mac);
			String user_agent = request.getHeader("User-Agent");
			RpcResponseDTO<OrderSMSVTO> orderRpcResult = orderRpcService.createSMSOrder(mac, umac, 
					umactype, context, user_agent);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
}
