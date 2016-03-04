package com.bhu.vas.rpc.service.user;

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
	@Override
	public RpcResponseDTO<Map<String, Object>> createNewUser(int countrycode, String acc,
			String nick,String pwd, String captcha, String sex, String device,String regIp,String deviceuuid,String ut,String org) {
		logger.info(String.format("createNewUser with countrycode[%s] acc[%s] nick[%s] pwd[%s] sex[%s] device[%s] ut[%s] org[%s] captcha[%s]",
				countrycode,acc,nick,pwd,sex,device,ut,org,captcha));
		return userUnitFacadeService.createNewUser(countrycode, acc, nick,pwd, captcha, sex, device,regIp, deviceuuid, ut,org);
	}
	
	@Override
	public RpcResponseDTO<Map<String, Object>> userLogin(int countrycode,
			String acc, String pwd, String device, String remoteIp) {
		logger.info(String.format("userLogin with countrycode[%s] acc[%s] pwd[%s] device[%s] remoteIp[%s]",
				countrycode,acc,pwd,device,remoteIp));
		return userUnitFacadeService.userLogin(countrycode, acc,pwd, device,remoteIp);
	}
	
	@Override
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc){
		logger.info(String.format("checkAcc with countrycode[%s] acc[%s]",countrycode,acc));
		return userUnitFacadeService.checkAcc(countrycode, acc);
	}
	
	@Override
	public RpcResponseDTO<Boolean> checkNick(String nick){
		logger.info(String.format("checkNick with nick[%s]",nick));
		return userUnitFacadeService.checkNick(nick);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> userConsoleLogin(int countrycode, String acc,String pwd,String device,String remoteIp) {
		logger.info(String.format("userLogin with countrycode[%s] acc[%s] device[%s] pwd[%s]",
				countrycode,acc,device,pwd));
		return userUnitFacadeService.userLogin(countrycode, acc,pwd, device, remoteIp);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,String d_udid, String device,
			String remoteIp) {
		logger.info(String.format("userValidate with aToken[%s] udid[%s] device[%s] remoteIp[%s]",aToken,d_udid,device,remoteIp));
		return userUnitFacadeService.userValidate(aToken,d_udid, device, remoteIp);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> userCreateOrLogin(int countrycode,
			String acc, String captcha, String device, String remoteIp,String d_uuid) {
		logger.info(String.format("userCreateOrLogin with countrycode[%s] acc[%s] captcha[%s] uuid[%s] device[%s] ",
				countrycode,acc,captcha,d_uuid,device));
		return userUnitFacadeService.userCreateOrLogin(countrycode, acc, captcha, device, remoteIp,d_uuid);
	}

	@Override
	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token,String d_uuid) {
		logger.info(String.format("tokenValidate with uidParam[%s] token[%s] uuid[%s] ",
				uidParam,token,d_uuid));
		return userUnitFacadeService.tokenValidate(uidParam, token,d_uuid);
	}
	
	@Override
	public RpcResponseDTO<Boolean> userBBSsignedon(int countrycode, String acc, String secretkey) {
		logger.info(String.format("userBBSsignedon with countrycode[%s] acc[%s] sk[%s]", countrycode,acc,secretkey));
		return userUnitFacadeService.userBBSsignedon(countrycode, acc, secretkey);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> updateProfile(int uid,
			String nick, String avatar, String sex, String birthday,String org) {
		logger.info(String.format("updateProfile with uid[%s] nick[%s] avatar[%s] sex[%s] birthday[%s] org[%s]",
				uid,nick,avatar,sex,birthday,org));
		return userUnitFacadeService.updateProfile(uid, nick, avatar, sex, birthday,org);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> profile(int uid) {
		logger.info(String.format("profile with uid[%s]",uid));
		return userUnitFacadeService.profile(uid);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> userResetPwd(int countrycode,
			String acc, String pwd, String device, String resetIp,
			String captcha) {
		logger.info(String.format("userResetPwd with countrycode[%s] acc[%s] pwd[%s] device[%s] resetIp[%s] captcha[%s]",
				countrycode,acc,pwd,device,resetIp,captcha));
		return userUnitFacadeService.userResetPwd(countrycode, acc, pwd, device, resetIp, captcha);
	}

	@Override
	public RpcResponseDTO<Boolean> authentication(int uid,int countrycode, String acc,String captcha) {
		logger.info(String.format("authentication with countrycode[%s] acc[%s] captcha[%s]",countrycode,acc,captcha));
		return userUnitFacadeService.authentication(uid,countrycode, acc, captcha);
	}
}
