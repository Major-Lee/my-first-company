package com.bhu.vas.web.noauth;

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
import com.smartwork.msip.cores.web.business.validate.ValidateService;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
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
		try{
			ValidateService.validateDeviceMac(mac);
			ValidateService.validateMobilenoRegx(countrycode,acc);
			RpcResponseDTO<UserCaptchaCodeDTO> rpcResult = userCaptchaCodeRpcService.fetchCaptchaCode(countrycode, acc,act);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
		try{
			ValidateService.validateDeviceMac(mac);
			ValidateService.validateMobilenoRegx(countrycode,acc);
			RpcResponseDTO<Boolean> rpcResult = userCaptchaCodeRpcService.validateCaptchaCode(countrycode, acc,captcha,act);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
}
