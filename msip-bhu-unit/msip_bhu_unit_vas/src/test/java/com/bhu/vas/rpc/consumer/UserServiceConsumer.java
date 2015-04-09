package com.bhu.vas.rpc.consumer;

import java.util.UUID;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserCaptchaCodeRpcService;
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;

public class UserServiceConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUUserRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();
		//申请手机验证码并注册
		/*IUserCaptchaCodeRpcService userCaptchaCodeRpcService = (IUserCaptchaCodeRpcService)context.getBean("userCaptchaCodeRpcService");
		
		RpcResponseDTO<UserCaptchaCodeDTO> rpcResult = userCaptchaCodeRpcService.fetchCaptchaCode(86, "18612272825");
		
		if(rpcResult.getErrorCode() == null){
			UserCaptchaCodeDTO dto = rpcResult.getPayload();
			System.out.println(dto.getCountrycode());
			System.out.println(dto.getAcc());
			System.out.println(dto.getCaptcha());
			IUserRpcService userRpcService = (IUserRpcService)context.getBean("userRpcService");
			
			RpcResponseDTO<UserDTO> createNewUser = userRpcService.createNewUser(86, "18612272825", "edmond", "男", "O", "192.168.66.8", UUID.randomUUID().toString(), dto.getCaptcha());
			if(createNewUser.getErrorCode() == null){
				UserDTO retdto = createNewUser.getPayload();
				System.out.println(retdto.getId());
				System.out.println(retdto.getAtoken());
				System.out.println(retdto.getRtoken());
				System.out.println(retdto.getMobileno());
				System.out.println(retdto.getCountrycode());
				System.out.println(retdto.getNick());
			}
		}else{
			System.out.println(rpcResult.getErrorCode());
		}*/
		
		//验证手机号是否已经注册
		/*IUserRpcService userRpcService = (IUserRpcService)context.getBean("userRpcService");
		RpcResponseDTO<Boolean> checkacc = userRpcService.checkAcc(86, "18617432728");//createNewUser(86, "18612272825", "edmond", "男", "O", "192.168.66.8", UUID.randomUUID().toString(), dto.getCaptcha());
		if(checkacc.getErrorCode() == null){
			Boolean ret = checkacc.getPayload();
			System.out.println(ret);
		}else{
			System.out.println(checkacc.getErrorCode());
		}*/
		
		
		
		//申请手机验证码并登录
		/*IUserCaptchaCodeRpcService userCaptchaCodeRpcService = (IUserCaptchaCodeRpcService)context.getBean("userCaptchaCodeRpcService");
		
		RpcResponseDTO<UserCaptchaCodeDTO> rpcResult = userCaptchaCodeRpcService.fetchCaptchaCode(86, "18612272825");
		
		if(rpcResult.getErrorCode() == null){
			UserCaptchaCodeDTO dto = rpcResult.getPayload();
			System.out.println(dto.getCountrycode());
			System.out.println(dto.getAcc());
			System.out.println(dto.getCaptcha());
			IUserRpcService userRpcService = (IUserRpcService)context.getBean("userRpcService");
			
			RpcResponseDTO<UserDTO> createNewUser = userRpcService.userLogin(86, "18612272825", "O", "192.168.66.8", dto.getCaptcha());//(86, "18612272825", "edmond", "男", "O", "192.168.66.8", UUID.randomUUID().toString(), dto.getCaptcha());
			if(createNewUser.getErrorCode() == null){
				UserDTO retdto = createNewUser.getPayload();
				System.out.println(retdto.getId());
				System.out.println(retdto.getAtoken());
				System.out.println(retdto.getRtoken());
				System.out.println(retdto.getMobileno());
				System.out.println(retdto.getCountrycode());
				System.out.println(retdto.getNick());
			}
		}else{
			System.out.println(rpcResult.getErrorCode());
		}*/
		//validate登录
		IUserRpcService userRpcService = (IUserRpcService)context.getBean("userRpcService");
		
		RpcResponseDTO<UserDTO> createNewUser = userRpcService.userValidate("JzZfUlNWVEcQFxALCF1WIkw=", "R", "192.168.66.8");//(86, "18612272825", "O", "192.168.66.8", dto.getCaptcha());//(86, "18612272825", "edmond", "男", "O", "192.168.66.8", UUID.randomUUID().toString(), dto.getCaptcha());
		if(createNewUser.getErrorCode() == null){
			UserDTO retdto = createNewUser.getPayload();
			System.out.println(retdto.getId());
			System.out.println(retdto.getAtoken());
			System.out.println(retdto.getRtoken());
			System.out.println(retdto.getMobileno());
			System.out.println(retdto.getCountrycode());
			System.out.println(retdto.getNick());
		}
		
		Thread.currentThread().join();
	}
}
