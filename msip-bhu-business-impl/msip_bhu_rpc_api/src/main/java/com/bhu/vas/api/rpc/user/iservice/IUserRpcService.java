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
	public RpcResponseDTO<Map<String, Object>> createNewUser(
			int countrycode,
			String acc,
			String nick,
			String pwd,
			String captcha,
			String sex,
			String device,
			String regIp,
			String deviceuuid,
			String ut,
			String org
			);
	
	/**
	 * 帐号密码登录 帐号可以是手机号和昵称
	 * @param countrycode
	 * @param acc
	 * @param pwd
	 * @param device
	 * @param remoteIp
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userLogin(int countrycode, String acc,String pwd, String device,String remoteIp);
	/**
	 * 检测token是否合法
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token,String d_udid);
	
	/**
	 * 检测acc是否已经存在
	 * @param countrycode
	 * @param acc
	 * @return
	 */
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc);
	public RpcResponseDTO<Boolean> checkNick(String nick);
	
	/**
	 * 验证码的用户登录
	 * @param countrycode
	 * @param acc
	 * @param device
	 * @param remoteIp
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userConsoleLogin(int countrycode, String acc,String pwd,String device,String remoteIp);
	
	/**
	 * 用户通过token进行登录
	 * @param aToken
	 * @param device
	 * @param remoteIp
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,String d_udid,String device,String remoteIp);
	
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
	public RpcResponseDTO<Map<String, Object>> userCreateOrLogin(int countrycode, String acc,String captcha,String device,String remoteIp,String d_udid);
	RpcResponseDTO<Map<String, Object>> updateProfile(int uid,String nick, String avatar, String sex, String birthday,String org,String memo);
	RpcResponseDTO<Map<String, Object>> profile(int uid);
	/*public RpcResponseDTO<TaskResDTO> createNewTask(
			String mac,
			String opt,
			//String payload,
			String channel,
			String channel_taskid);
	
	public void taskStatusFetch(
			long taskid
			);
	
	public void taskStatusFetch4ThirdParties(
			String channel,
			String channel_taskid
			);*/
	//public boolean taskCompleted(String taskid);
	
	/**
	 * 根据验证码进行密码重置操作
	 * @param countrycode
	 * @param acc
	 * @param pwd
	 * @param device
	 * @param resetIp
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> userResetPwd(
			int countrycode,
			String acc,
			String pwd,
			String device,
			String resetIp,
			String captcha
			);
	
	public RpcResponseDTO<Map<String, Object>> userChangedPwd(
			int uid,
			String pwd,
			String npwd
			);
	/**
	 * 对于oauth注册的用户提供手机号码认证绑定的过程
	 * 1、如果手机号码已经存在则提示错误码
	 * 2、需要验证码验证
	 * 3、如果此账户已经有绑定手机号，则移除前手机号的唯一存储，替换成新的手机号
	 * 4、成功后，此手机号可以进行登录
	 * @param countrycode
	 * @param acc
	 * @param captcha
	 * @return
	 */
	public RpcResponseDTO<Boolean> authentication(int uid,int countrycode, String acc,String captcha);
	public RpcResponseDTO<Boolean> userBBSsignedon(int countrycode, String acc, String secretkey);
}
