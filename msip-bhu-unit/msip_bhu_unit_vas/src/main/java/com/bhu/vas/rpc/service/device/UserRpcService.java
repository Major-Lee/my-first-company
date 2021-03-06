package com.bhu.vas.rpc.service.device;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.bhu.vas.rpc.facade.UserUnitFacadeService;

@Service("userRpcService")
public class UserRpcService implements IUserRpcService{
	private final Logger logger = LoggerFactory.getLogger(UserRpcService.class);
	@Resource
	private UserUnitFacadeService userUnitFacadeService;
	/*@Override
	public RpcResponseDTO<UserDTO> createNewUser(int countrycode, String acc,
			String nick, String sex, String device,String regIp,String deviceuuid, String captcha) {
		logger.info(String.format("createNewUser with countrycode[%s] acc[%s] nick[%s] sex[%s] device[%s] captcha[%s]",
				countrycode,acc,nick,sex,device,captcha));
		return userUnitFacadeService.createNewUser(countrycode, acc, nick, sex, device,regIp, deviceuuid, captcha);
	}*/
	
	@Override
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc){
		logger.info(String.format("checkAcc with countrycode[%s] acc[%s]",countrycode,acc));
		return userUnitFacadeService.checkAcc(countrycode, acc);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> userConsoleLogin(int countrycode, String acc,String pwd,String device,String remoteIp) {
		logger.info(String.format("userLogin with countrycode[%s] acc[%s] device[%s] pwd[%s]",
				countrycode,acc,device,pwd));
		return userUnitFacadeService.userConsoleLogin(countrycode, acc,pwd, device, remoteIp);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken, String device,
			String remoteIp) {
		logger.info(String.format("userValidate with aToken[%s] device[%s] remoteIp[%s]",aToken,device,remoteIp));
		return userUnitFacadeService.userValidate(aToken, device, remoteIp);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> userCreateOrLogin(int countrycode,
			String acc, String device, String remoteIp, String captcha) {
		logger.info(String.format("userCreateOrLogin with countrycode[%s] acc[%s] device[%s] captcha[%s]",
				countrycode,acc,device,captcha));
		return userUnitFacadeService.userCreateOrLogin(countrycode, acc, device, remoteIp, captcha);
	}

	@Override
	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token) {
		// TODO Auto-generated method stub
		return userUnitFacadeService.tokenValidate(uidParam, token);
	}
	
	@Override
	public RpcResponseDTO<Boolean> userBBSsignedon(int countrycode, String acc, String secretkey) {
		logger.info(String.format("userBBSsignedon with countrycode[%s] acc[%s] sk[%s]", countrycode,acc,secretkey));
		return userUnitFacadeService.userBBSsignedon(countrycode, acc, secretkey);
	}
}
