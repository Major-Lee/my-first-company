package com.bhu.vas.rpc.service.commdity;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.rpc.facade.OrderUnitFacadeService;

@Service("userRpcService")
public class OrderRpcService implements IOrderRpcService{
	private final Logger logger = LoggerFactory.getLogger(OrderRpcService.class);
	@Resource
	private OrderUnitFacadeService orderUnitFacadeService;
	@Override
	public RpcResponseDTO<Map<String, Object>> createNewUser(int countrycode,
			String acc, String nick, String pwd, String captcha, String sex,
			String device, String regIp, String deviceuuid, String ut,
			String org) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Map<String, Object>> userLogin(int countrycode,
			String acc, String pwd, String device, String remoteIp) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token,
			String d_udid) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Boolean> checkNick(String nick) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Map<String, Object>> userConsoleLogin(
			int countrycode, String acc, String pwd, String device,
			String remoteIp) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,
			String d_udid, String device, String remoteIp) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Map<String, Object>> userCreateOrLogin(
			int countrycode, String acc, String captcha, String device,
			String remoteIp, String d_udid) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Map<String, Object>> updateProfile(int uid,
			String nick, String avatar, String sex, String birthday, String org) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Map<String, Object>> profile(int uid) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Map<String, Object>> userResetPwd(int countrycode,
			String acc, String pwd, String device, String resetIp,
			String captcha) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RpcResponseDTO<Boolean> userBBSsignedon(int countrycode, String acc,
			String secretkey) {
		// TODO Auto-generated method stub
		return null;
	}


}
