package com.bhu.vas.rpc.service.device;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.iservice.IAgentUserRpcService;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.rpc.facade.AgentUserUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("agentUserRpcService")
public class AgentUserRpcService implements IAgentUserRpcService {
	private final Logger logger = LoggerFactory.getLogger(AgentUserRpcService.class);
	@Resource
	private AgentUserUnitFacadeService agentUserUnitFacadeService;
	
	@Override
	public RpcResponseDTO<Map<String, Object>> createNewUser(int countrycode, String acc,
			String pwd, String nick, String sex, String device, String regIp) {
		logger.info(String.format("createNewUser with countrycode[%s] acc[%s] pwd[%s] nick[%s] sex[%s] device[%s]",
				countrycode,acc,pwd,nick,sex,device));
		return agentUserUnitFacadeService.createNewUser(countrycode, acc,pwd, nick, sex, device,regIp);
	}

	@Override
	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token) {
		logger.info(String.format("tokenValidate with uid[%s] token[%s]",uidParam,token));
		return agentUserUnitFacadeService.tokenValidate(uidParam, token);
	}

	@Override
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc) {
		logger.info(String.format("checkAcc with countrycode[%s] acc[%s]",countrycode,acc));
		return agentUserUnitFacadeService.checkAcc(countrycode, acc);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,
			String device, String remoteIp) {
		logger.info(String.format("userValidate with aToken[%s] device[%s] remoteIp[%s]",aToken,device,remoteIp));
		return agentUserUnitFacadeService.userValidate(aToken, device, remoteIp);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> userLogin(int countrycode,
			String acc, String pwd, String device, String remoteIp) {
		logger.info(String.format("userLogin with countrycode[%s] acc[%s] pwd[%s] device[%s] remoteIp[%s]",
				countrycode,acc,pwd,device,remoteIp));
		return agentUserUnitFacadeService.userLogin(countrycode, acc, pwd, device, remoteIp);
	}

	@Override
	public RpcResponseDTO<TailPage<UserDTO>> pageAgentUsers(int pageno,int pagesize) {
		logger.info("pageAgentUsers");
		return agentUserUnitFacadeService.pageAgentUsers(pageno,pagesize);
	}

}
