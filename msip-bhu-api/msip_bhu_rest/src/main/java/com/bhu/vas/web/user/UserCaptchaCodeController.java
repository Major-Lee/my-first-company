package com.bhu.vas.web.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.user.UserIdentityAuthVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserCaptchaCodeRpcService;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/user/captcha")
public class UserCaptchaCodeController extends BaseController{
	
	@Resource
	private IUserCaptchaCodeRpcService userCaptchaCodeRpcService;
	
	
	/**
	 * 请求获取验证码接口
	 * @param response
	 * @param countrycode
	 * @param acc
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_captcha",method={RequestMethod.POST})
	public void fetch_captcha(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = false,defaultValue="R") String act
			) {
		ResponseError validateError = ValidateService.validateMobilenoRegx(countrycode,acc,request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		
		RpcResponseDTO<UserCaptchaCodeDTO> rpcResult = userCaptchaCodeRpcService.fetchCaptchaCode(countrycode, acc,act);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	

	
//	@ResponseBody()
//	@RequestMapping(value="/identity_auth",method={RequestMethod.POST})
//	public void check_identity(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@RequestParam(required = true) String hdmac
//			) {
//		
//		RpcResponseDTO<UserIdentityAuthVTO> rpcResult = userCaptchaCodeRpcService.validateIdentity(hdmac.toLowerCase());
//		if(!rpcResult.hasError()){
//			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
//		}else{
//			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
//		}
//	}
	
	@ResponseBody()
	@RequestMapping(value="/validate_captcha",method={RequestMethod.POST})
	public void validate_code(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String hdmac,
			@RequestParam(required = true) String captcha
			) {
		
		ResponseError validateError = ValidateService.validateMobilenoRegx(countrycode,acc,request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		
		RpcResponseDTO<Boolean> rpcResult = userCaptchaCodeRpcService.validateIdentityCode(countrycode, acc, hdmac, captcha);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
}
