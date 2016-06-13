package com.bhu.vas.web.query;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserCaptchaCodeRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/noauth/captcha")
public class CaptchaCodeController extends BaseController{
	
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
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String act,
			@RequestParam(required = true) String mac
			) {
		ResponseError validateError = null;
		validateError = ValidateService.validateDeviceMac(mac);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		validateError = ValidateService.validateMobilenoRegx(countrycode,acc);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<UserCaptchaCodeDTO> rpcResult = userCaptchaCodeRpcService.fetchCaptchaCode(countrycode, acc,act);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	
	/**
	 * 请求获取验证码接口
	 * @param response
	 * @param countrycode
	 * @param acc
	 */
	@ResponseBody()
	@RequestMapping(value="/validate_captcha",method={RequestMethod.POST})
	public void validate_captcha(
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String captcha,
			@RequestParam(required = true) String act,
			@RequestParam(required = true) String mac
			) {
		ResponseError validateError = null;
		validateError = ValidateService.validateDeviceMac(mac);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		validateError = ValidateService.validateMobilenoRegx(countrycode,acc);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<Boolean> rpcResult = userCaptchaCodeRpcService.validateCaptchaCode(countrycode, acc, captcha, act);//(countrycode, acc,act);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
}
