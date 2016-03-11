package com.bhu.vas.rpc.service.user;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserOAuthRpcService;
import com.bhu.vas.rpc.facade.UserOAuthUnitFacadeService;

@Service("userOAuthRpcService")
public class UserOAuthRpcService implements IUserOAuthRpcService{
	private final Logger logger = LoggerFactory.getLogger(UserOAuthRpcService.class);
	@Resource
	private UserOAuthUnitFacadeService userOAuthUnitFacadeService;
	@Override
	public RpcResponseDTO<List<UserOAuthStateDTO>> fetchRegisterIdentifies(
			Integer uid) {
		logger.info(String.format("fetchRegisterIdentifies with uid[%s]",uid));
		return userOAuthUnitFacadeService.fetchRegisterIdentifies(uid);
	}
	@Override
	public RpcResponseDTO<Boolean> removeIdentifies(Integer uid, String identify) {
		logger.info(String.format("removeIdentifies with uid[%s] identify[%s]",uid,identify));
		return userOAuthUnitFacadeService.removeIdentifies(uid, identify);
	}
	@Override
	public RpcResponseDTO<Map<String, Object>> createIdentifies(
			Integer uid,
			String identify,String auid, String nick, String avatar,
			String device,String regIp,String deviceuuid, String ut
			) {
		logger.info(String.format("createIdentifies with identify[%s] auid[%s] nick[%s] avatar[%s]",identify,auid,nick,avatar));
		return userOAuthUnitFacadeService.createOrUpdateIdentifies(uid,identify, auid, nick, avatar, 
				device, regIp, deviceuuid, ut);
	}
}
