package com.bhu.vas.rpc.service.captcha;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserCaptchaCodeRpcService;
import com.bhu.vas.rpc.facade.UserCaptchaCodeUnitFacadeService;

@Service("userCaptchaCodeRpcService")
public class UserCaptchaCodeRpcService implements IUserCaptchaCodeRpcService{
	private final Logger logger = LoggerFactory.getLogger(UserCaptchaCodeRpcService.class);
	@Resource
	private UserCaptchaCodeUnitFacadeService userCaptchaCodeUnitFacadeService;
	@Override
	public RpcResponseDTO<UserCaptchaCodeDTO> fetchCaptchaCode(int countrycode,
			String acc,String act) {
		logger.info(String.format("fetchCaptchaCode with countrycode[%s] acc[%s] act[%s]", countrycode,acc,act));
		return userCaptchaCodeUnitFacadeService.fetchCaptchaCode(countrycode, acc,act);
	}
	
	@Override
	public RpcResponseDTO<Boolean> validateCaptchaCode(int countrycode,
			String acc, String captcha,String act) {
		logger.info(String.format("validateCaptchaCode with countrycode[%s] acc[%s] captcha[%s] act[%s]", countrycode,acc,captcha,act));
		return userCaptchaCodeUnitFacadeService.validateCaptchaCode(countrycode, acc,captcha,act);
	}
	
	@Override
	public RpcResponseDTO<UserCaptchaCodeDTO> identityAuth(int countrycode,
			String acc,String hdmac) {
		logger.info(String.format("identityAuth with countrycode[%s] acc[%s] hdmac[%s]", countrycode,acc,hdmac));
		return userCaptchaCodeUnitFacadeService.identityAuth(countrycode, acc, hdmac);
	}
	
	@Override
	public RpcResponseDTO<Boolean> validateIdentity(int countrycode,
			String acc,String hdmac) {
		logger.info(String.format("validateIdentity countrycode[%s] acc[%s] hdmac[%s]", countrycode,acc,hdmac));
		return userCaptchaCodeUnitFacadeService.validateIdentity(countrycode, acc, hdmac);
	}
}
