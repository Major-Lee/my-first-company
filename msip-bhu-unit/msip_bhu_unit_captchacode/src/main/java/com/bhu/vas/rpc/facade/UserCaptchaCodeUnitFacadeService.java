package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;
import com.bhu.vas.api.rpc.user.model.UserCaptchaCode;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserCaptchaCodeUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(UserCaptchaCodeUnitFacadeService.class);
	@Resource
	private UserCaptchaCodeService userCaptchaCodeService;
	
	//@Resource
	//private DeliverMessageService deliverMessageService;
	
	public RpcResponseDTO<UserCaptchaCodeDTO> fetchCaptchaCode(int countrycode,
			String acc) {
		RpcResponseDTO<UserCaptchaCodeDTO> result = new RpcResponseDTO<UserCaptchaCodeDTO>();
		try{
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			UserCaptchaCode code = userCaptchaCodeService.doGenerateCaptchaCode(accWithCountryCode);
			//deliverMessageService.sendUserCaptchaCodeFetchActionMessage(DeliverMessageType.AC.getPrefix(), countrycode, acc, code.getCaptcha());//sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), remoteIp, from_device);
			//SpringMVCHelper.renderJson(response, Response.SUCCESS);
			/*UserCaptchaCodeDTO payload = new UserCaptchaCodeDTO();
			payload.setAcc(acc);
			payload.setCountrycode(countrycode);
			payload.setCaptcha(code.getCaptcha());
			result.setErrorCode(null);
			result.setPayload(payload);*/
			
			if(!RuntimeConfiguration.SecretInnerTest){
				//logger.info("step 1");
				//String mobileWithCountryCode = PhoneHelper.format(countrycode, acc);
				//logger.info("step 2");
				if(!RuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
					//logger.info("step 3");
					if(countrycode == PhoneHelper.Default_CountryCode_Int){
						//logger.info("step 4 -1");
						String smsg = String.format(RuntimeConfiguration.InternalCaptchaCodeSMS_Template, code.getCaptcha());
						String response = SmsSenderFactory.buildSender(
								RuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
						//String response = ChanzorSMSHelper.postSendMsg(smsg, dto.getAcc());
						//String response = WangjianSMSHelper.postSendMsg(smsg, new String[]{dto.getAcc()});
						//logger.info("CaptchaCodeNotifyActHandler Guodu msg:"+message);
						logger.info("sendCaptchaCodeNotifyHandle new Chanzor res:"+response+" msg:"+smsg);
					}else{
						//logger.info("step 4 -2");
						logger.info("sendCaptchaCodeNotifyHandle not supported foreign sms res");
						/*if(dto.getCountrycode() == NexmoSMSHelper.UsAndCanada_CountryCode_Int){
							String response = NexmoSMSHelper.send(NexmoSMSHelper.Default_UsANDCanada_SMS_FROM,mobileWithCountryCode, String.format(RuntimeConfiguration.ForeignCaptchaCodeSMS_Template,dto.getCaptcha()));//.postSendMsg(String.format(RuntimeConfiguration.InternalCaptchaCodeSMS_Template, dto.getCaptcha()), new String[]{dto.getAcc()});
							logger.info("to US and Canada CaptchaCodeNotifyActHandler Nexmo res:"+response);
						}else{
							String response = NexmoSMSHelper.send(mobileWithCountryCode, String.format(RuntimeConfiguration.ForeignCaptchaCodeSMS_Template,dto.getCaptcha()));//.postSendMsg(String.format(RuntimeConfiguration.InternalCaptchaCodeSMS_Template, dto.getCaptcha()), new String[]{dto.getAcc()});
							logger.info("to Other CaptchaCodeNotifyActHandler Nexmo res:"+response);
						}
						//logger.info("CaptchaCodeNotifyActHandler Nexmo msg:"+message);
	*/				}
				}
			}
			
			//deliverMessageService.sendUserCaptchaCodeFetchActionMessage(countrycode, acc, code.getCaptcha());
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
