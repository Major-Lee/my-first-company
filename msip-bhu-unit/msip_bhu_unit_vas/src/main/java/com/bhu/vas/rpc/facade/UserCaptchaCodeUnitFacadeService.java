package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;
import com.bhu.vas.api.rpc.user.model.UserCaptchaCode;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserCaptchaCodeUnitFacadeService {
	@Resource
	private UserCaptchaCodeService userCaptchaCodeService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	public RpcResponseDTO<UserCaptchaCodeDTO> fetchCaptchaCode(int countrycode,
			String acc) {
		RpcResponseDTO<UserCaptchaCodeDTO> result = new RpcResponseDTO<UserCaptchaCodeDTO>();
		try{
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			UserCaptchaCode code = userCaptchaCodeService.doGenerateCaptchaCode(accWithCountryCode);
			//deliverMessageService.sendUserCaptchaCodeFetchActionMessage(DeliverMessageType.AC.getPrefix(), countrycode, acc, code.getCaptcha());//sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), remoteIp, from_device);
			//SpringMVCHelper.renderJson(response, Response.SUCCESS);
			UserCaptchaCodeDTO payload = new UserCaptchaCodeDTO();
			payload.setAcc(acc);
			payload.setCountrycode(countrycode);
			payload.setCaptcha(code.getCaptcha());
			result.setErrorCode(null);
			result.setPayload(payload);
			deliverMessageService.sendUserCaptchaCodeFetchActionMessage(countrycode, acc, code.getCaptcha());
		}catch(BusinessI18nCodeException ex){
			System.out.println("cc:"+countrycode +" acc:"+acc);
			ex.printStackTrace(System.out);
			result.setErrorCode(ex.getErrorCode());
			//SpringMVCHelper.renderJson(response, ResponseError.embed(ex.getErrorCode()));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			result.setErrorCode(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);
			//SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
		return result;
	}
}
