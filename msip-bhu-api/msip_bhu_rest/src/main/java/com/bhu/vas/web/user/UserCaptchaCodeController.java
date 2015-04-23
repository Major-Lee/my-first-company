package com.bhu.vas.web.user;

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
	@RequestMapping(value="/fetch_captcha",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_captcha(
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc) {
		/*int charlen = acc.length();
		if(charlen < 6 || charlen > 16){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_LENGTH,new String[]{"6","16"});//renderHtml(response, html, headers)
		}
		
		if(!StringHelper.isValidMobilenoCharacter(mobileno)){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_FORMAT);//renderHtml(response, html, headers)
		}*/
		ResponseError validateError = ValidateService.validateMobilenoRegx(countrycode,acc);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<UserCaptchaCodeDTO> rpcResult = userCaptchaCodeRpcService.fetchCaptchaCode(countrycode, acc);
		if(rpcResult.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
		}
		
		/*try{
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			CaptchaCode code = captchaCodeService.doGenerateCaptchaCode(accWithCountryCode);
			deliverMessageService.sendUserCaptchaCodeFetchActionMessage(DeliverMessageType.AC.getPrefix(), countrycode, acc, code.getCaptcha());//sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), remoteIp, from_device);
			SpringMVCHelper.renderJson(response, Response.SUCCESS);
		}catch(BusinessI18nCodeException ex){
			System.out.println("cc:"+countrycode +" acc:"+acc);
			ex.printStackTrace(System.out);
			SpringMVCHelper.renderJson(response, ResponseError.embed(ex.getErrorCode()));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}*/
	}
}
