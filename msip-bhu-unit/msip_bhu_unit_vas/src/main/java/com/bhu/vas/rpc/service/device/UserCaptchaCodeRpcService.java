package com.bhu.vas.rpc.service.device;

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
			String acc) {
		logger.info(String.format("fetchCaptchaCode with [%s] [%s]", countrycode,acc));
		return userCaptchaCodeUnitFacadeService.fetchCaptchaCode(countrycode, acc);
	}

}
