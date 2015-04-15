package com.bhu.vas.api.rpc.user.iservice;

import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;

public interface IUserRpcService {
	/**
	 * 创建用户
	 * @param countrycode
	 * @param acc
	 * @param nick
	 * @param sex
	 * @param device
	 * @param regIp
	 * @param deviceuuid
	 * @param captcha
	 * @return
	 */
	/*public RpcResponseDTO<UserDTO> createNewUser(
			int countrycode,
			String acc,
			String nick,
			String sex,
			String device,
			String regIp,String deviceuuid,
			String captcha
			);*/
	
	/**
	 * 检测acc是否已经存在
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc);
	
	
	/**
	 * 验证码的用户登录
	 * @param countrycode
	 * @param acc
	 * @param device
	 * @param remoteIp
	 * @param captcha
	 * @return
	 */
	//public RpcResponseDTO<UserDTO> userLogin(int countrycode, String acc,String device,String remoteIp,String captcha);
	
	/**
	 * 用户通过token进行登录
	 * @param aToken
	 * @param device
	 * @param remoteIp
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,String device,String remoteIp);
	
	/**
	 * 用户登录或者注册
	 * acc存在的情况下为登录
	 * acc不存在的情况下未注册
	 * @param countrycode
	 * @param acc
	 * @param device
	 * @param remoteIp
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userCreateOrLogin(int countrycode, String acc,String device,String remoteIp,String captcha);
	/*public RpcResponseDTO<TaskResDTO> createNewTask(
			String mac,
			String opt,
			//String payload,
			String channel,
			String channel_taskid);
	
	public void taskStatusFetch(
			int taskid
			);
	
	public void taskStatusFetch4ThirdParties(
			String channel,
			String channel_taskid
			);*/
	//public boolean taskCompleted(String taskid);
}
