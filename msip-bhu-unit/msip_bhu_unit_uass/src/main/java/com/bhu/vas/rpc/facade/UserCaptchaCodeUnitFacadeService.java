package com.bhu.vas.rpc.facade;

import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import sun.nio.cs.ext.MacHebrew;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.helper.BusinessEnumType.CaptchaCodeActType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;
import com.bhu.vas.api.rpc.user.model.UserCaptchaCode;
import com.bhu.vas.api.rpc.user.model.UserIdentityAuth;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.bhu.vas.business.ds.user.service.UserIdentityAuthService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

@Service
public class UserCaptchaCodeUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(UserCaptchaCodeUnitFacadeService.class);
	@Resource
	private UserCaptchaCodeService userCaptchaCodeService;
	@Resource
	private UserIdentityAuthService userIdentityAuthService;
	
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	
	//内部线程池，用于调用sms接口
	private ExecutorService exec_send = null;//Executors.newFixedThreadPool(30);
	@PostConstruct
	public void initialize() {
		logger.info("UserCaptchaCodeUnitFacadeService initialize...");
		//exec_remote_portalexchange = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"AsyncOrderPaymentNotify processes消息处理",ProcessesThreadCount);
		exec_send = ExecObserverManager.buildExecutorService(this.getClass(),"SMS消息发送处理",30);
	}
	//newSingleThreadExecutor
	//private ExecutorService exec_aftervalidate = Executors.newFixedThreadPool(30);
	//@Resource
	//private DeliverMessageService deliverMessageService;
	/*private static final String FetchCaptchaCode_RegisterOrLogin_Act = "R";
	private static final String FetchCaptchaCode_PwdReset_Act = "P";
	private static final String FetchCaptchaCode_SnkAuth_Act = "S";*/
	
	public RpcResponseDTO<UserCaptchaCodeDTO> fetchCaptchaCode(int countrycode,
			final String acc,final String act) {
		//RpcResponseDTO<UserCaptchaCodeDTO> result = new RpcResponseDTO<UserCaptchaCodeDTO>();
		try{
			boolean specialCaptchaCode = false;
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			if(act.equals("I")){
				specialCaptchaCode = true;
			}
			UserCaptchaCode code = userCaptchaCodeService.doGenerateCaptchaCode(accWithCountryCode,specialCaptchaCode);
			//deliverMessageService.sendUserCaptchaCodeFetchActionMessage(DeliverMessageType.AC.getPrefix(), countrycode, acc, code.getCaptcha());//sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), remoteIp, from_device);
			//SpringMVCHelper.renderJson(response, Response.SUCCESS);
			final UserCaptchaCodeDTO payload = new UserCaptchaCodeDTO();
			payload.setAcc(acc);
			payload.setCountrycode(countrycode);
			payload.setCaptcha(code.getCaptcha());
			/*result.setErrorCode(null);
			result.setPayload(payload);*/
			if(!RuntimeConfiguration.SecretInnerTest){
				if(!BusinessRuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
					if(countrycode == PhoneHelper.Default_CountryCode_Int){
						exec_send.submit((new Runnable() {
							@Override
							public void run() {
								String smsg = null;
								CaptchaCodeActType fromType = CaptchaCodeActType.fromType(act);
								switch(fromType){
									case RegisterOrLogin:
										smsg = String.format(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Template, payload.getCaptcha());
										break;
									case PwdReset:
										smsg = String.format(BusinessRuntimeConfiguration.InternalCaptchaCodePwdResetSMS_Template, payload.getCaptcha());
										break;
									case SnkAuth:
										smsg = String.format(BusinessRuntimeConfiguration.InternalCaptchaCodeSnkAuthSMS_Template, payload.getCaptcha());
										break;
									case Portal:
										smsg = String.format(BusinessRuntimeConfiguration.Internal_Portal_Template, payload.getCaptcha());
										break;
									default:
										smsg = String.format(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Template, payload.getCaptcha());
										break;
								}
								/*if(FetchCaptchaCode_RegisterOrLogin_Act.equals(act))
									smsg = String.format(BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Template, payload.getCaptcha());
								if(FetchCaptchaCode_PwdReset_Act.equals(act))
									smsg = String.format(BusinessRuntimeConfiguration.InternalCaptchaCodePwdResetSMS_Template, payload.getCaptcha());
								if(FetchCaptchaCode_SnkAuth_Act.equals(act))
									smsg = String.format(BusinessRuntimeConfiguration.InternalCaptchaCodeSnkAuthSMS_Template, payload.getCaptcha());*/
								if(StringUtils.isNotEmpty(smsg)){
									String response = SmsSenderFactory.buildSender(
										BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, acc);
									logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",acc,smsg,response));
								}
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
	
	
	public RpcResponseDTO<Boolean> validateCaptchaCode(final int countrycode,
			final String acc, final String captcha,final String act) {
		try{
			if(!RuntimeConfiguration.SecretInnerTest){
				String accWithCountryCode = PhoneHelper.format(countrycode, acc);
				if(!BusinessRuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
					ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
					if(errorCode != null){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
					}
				}
			}
			/*final CaptchaCodeActType fromType = CaptchaCodeActType.fromType(act);
			if(fromType == CaptchaCodeActType.SnkAuth){
				exec_aftervalidate.execute((new Runnable() {
					@Override
					public void run() {
						//设备绑定用户扣款
						//设备放行
					}
				}));
			}*/
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException ex){
			System.out.println("cc:"+countrycode +" acc:"+acc);
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ex.getErrorCode(), ex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);
		}
		//logger.info(String.format("validateCaptchaCode with countrycode[%s] acc[%s] captcha[%s]", countrycode,acc,captcha));
		//return userCaptchaCodeUnitFacadeService.validateCaptchaCode(countrycode, acc,captcha);
	}
	
	
	public RpcResponseDTO<Boolean> validateIdentityCode(int countrycode,
			 String acc,String hdmac,String captcha){
		try {
			//验证mac
			if(StringUtils.isEmpty(hdmac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(hdmac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
			
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
			if(errorCode != null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
			}
			userIdentityAuthService.generateIdentityAuth(countrycode, acc, hdmac);
			asyncDeliverMessageService.sendUserIdentityRepariActionMessage(hdmac,acc);
			return  RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ex.getErrorCode(), ex.getPayload());
		} 
	}
	
	
	public RpcResponseDTO<UserIdentityAuth> validateIdentity(String hdmac){
		try {
			UserIdentityAuth auth = userIdentityAuthService.validateIdentity(hdmac);
			return  RpcResponseDTOBuilder.builderSuccessRpcResponse(auth);
		} catch (BusinessI18nCodeException ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ex.getErrorCode(), ex.getPayload());
		} 
	}
	
	
	/*@PreDestroy
	public void destory(){
		String simplename = this.getClass().getSimpleName();
		if(exec_send != null){
			System.out.println(simplename+" exec正在shutdown");
			exec_send.shutdown();
			System.out.println(simplename+" exec正在shutdown成功");
			while(true){
				System.out.println(simplename+" 正在判断exec是否执行完毕");
				if(exec_send.isTerminated()){
					System.out.println(simplename+" exec是否执行完毕,终止exec...");
					exec_send.shutdownNow();
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
	}*/
}
