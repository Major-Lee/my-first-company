package com.whisper.web.captcha;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.Response;
import com.smartwork.msip.jdo.ResponseError;
import com.whisper.api.captcha.model.CaptchaCode;
import com.whisper.business.asyn.web.builder.DeliverMessageType;
import com.whisper.business.asyn.web.service.DeliverMessageService;
import com.whisper.business.captcha.service.CaptchaCodeService;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.ValidateUserCheckController;
import com.whisper.validate.ValidateService;

@Controller
@RequestMapping("/captcha")
public class CaptchaCodeController extends ValidateUserCheckController{
	
	@Resource
	private CaptchaCodeService captchaCodeService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@ResponseBody()
	@RequestMapping(value="/fetch_captcha",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_captcha(
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc) {
		/*//TODO:特殊判断，兼容安卓 没有传递countrycode参数，
		if(countrycode == 86 && acc.length() ==10){
			countrycode = 1;
		}*/
		/*int charlen = acc.length();
		if(charlen < 6 || charlen > 16){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_LENGTH,new String[]{"6","16"});//renderHtml(response, html, headers)
		}
		
		if(!StringHelper.isValidMobilenoCharacter(mobileno)){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_FORMAT);//renderHtml(response, html, headers)
		}*/
		
		//System.out.println("~~~~~~~~~~~~~~~~~cc:"+countrycode);
		
		ResponseError validateError = ValidateService.validateMobilenoRegx(countrycode,acc);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		try{
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
		}
	}
}
