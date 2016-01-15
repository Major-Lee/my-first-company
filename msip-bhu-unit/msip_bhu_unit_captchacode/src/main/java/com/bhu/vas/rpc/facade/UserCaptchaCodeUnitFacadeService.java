package com.bhu.vas.rpc.facade;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;
import com.bhu.vas.api.rpc.user.model.UserCaptchaCode;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
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
	
	//内部线程池，用于调用sms接口
	private ExecutorService exec = Executors.newFixedThreadPool(30);
	//@Resource
	//private DeliverMessageService deliverMessageService;
	
	public RpcResponseDTO<UserCaptchaCodeDTO> fetchCaptchaCode(int countrycode,
			final String acc) {
		//RpcResponseDTO<UserCaptchaCodeDTO> result = new RpcResponseDTO<UserCaptchaCodeDTO>();
		try{
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			UserCaptchaCode code = userCaptchaCodeService.doGenerateCaptchaCode(accWithCountryCode);
			//deliverMessageService.sendUserCaptchaCodeFetchActionMessage(DeliverMessageType.AC.getPrefix(), countrycode, acc, code.getCaptcha());//sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), remoteIp, from_device);
			//SpringMVCHelper.renderJson(response, Response.SUCCESS);
			UserCaptchaCodeDTO payload = new UserCaptchaCodeDTO();
			payload.setAcc(acc);
			payload.setCountrycode(countrycode);
			payload.setCaptcha(code.getCaptcha());
			/*result.setErrorCode(null);
			result.setPayload(payload);*/
			if(!RuntimeConfiguration.SecretInnerTest){
				if(!BusinessRuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
					if(countrycode == PhoneHelper.Default_CountryCode_Int){
						final String smsg = String.format(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Template, code.getCaptcha());
						exec.submit((new Runnable() {
							@Override
							public void run() {
								String response = SmsSenderFactory.buildSender(
										BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
								logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,response));
							}
						}));
					}else{
						logger.info("sendCaptchaCodeNotifyHandle not supported foreign sms res");
						throw new BusinessI18nCodeException(ResponseErrorCode.SMS_COUNTRYCODE_NOTSUPPORTED,new String[]{String.valueOf(countrycode)});//
					}
				}
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
		}catch(BusinessI18nCodeException ex){
			System.out.println("cc:"+countrycode +" acc:"+acc);
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ex.getErrorCode(), ex.getPayload());
			//result.setErrorCode(ex.getErrorCode());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);
			//result.setErrorCode(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);
		}
		//return result;
	}
	
	@PreDestroy
	public void destory(){
		String simplename = this.getClass().getSimpleName();
		if(exec != null){
			System.out.println(simplename+" exec正在shutdown");
			exec.shutdown();
			System.out.println(simplename+" exec正在shutdown成功");
			while(true){
				System.out.println(simplename+" 正在判断exec是否执行完毕");
				if(exec.isTerminated()){
					System.out.println(simplename+" exec是否执行完毕,终止exec...");
					exec.shutdownNow();
					System.out.println(simplename+" exec是否执行完毕,终止exec成功");
					break;
				}else{
					System.out.println(simplename+" exec未执行完毕...");
					try {
						Thread.sleep(2*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
