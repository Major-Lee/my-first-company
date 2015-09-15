package com.bhu.vas.api.rpc.agent.iservice;

import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.vto.AgentUserDetailVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * 
 * @author Edmond
 *
 */
public interface IAgentUserRpcService {
	/**
	 * 创建用户
	 * @param countrycode
	 * @param acc
	 * @param acc
	 * @param nick
	 * @param sex
	 * @param device
	 * @param regIp
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> createNewUser(
			int countrycode,
			String acc,
			String pwd,
			String nick,
			String sex,
			
			String org,
			String addr1,
			String addr2,
			String memo,
			
			String device,
			String regIp
			);

	/**
	 * 检测token是否合法
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token);

	/**
	 * 检测acc是否已经存在
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc);
	
	/**
	 * 用户通过token进行登录
	 * @param aToken
	 * @param device
	 * @param remoteIp
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,String device,String remoteIp);
	
	/**
	 * 用户登录
	 * @param countrycode
	 * @param acc
	 * @param device
	 * @param remoteIp
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userLogin(int countrycode, String acc,String pwd,String device,String remoteIp);

	
	public RpcResponseDTO<TailPage<AgentUserDetailVTO>> pageAgentUsers(int uid,int pageno,int pagesize);
}
