package com.bhu.vas.api.rpc.user.iservice;

import com.bhu.vas.api.dto.user.UserIdentityAuthVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserCaptchaCodeDTO;


public interface IUserCaptchaCodeRpcService {
	public RpcResponseDTO<UserCaptchaCodeDTO> fetchCaptchaCode(int countrycode,String acc,String action);
	public RpcResponseDTO<Boolean> validateCaptchaCode(int countrycode,String acc,String captcha,String act);
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
	public RpcResponseDTO<UserIdentityAuthVTO> validateIdentity(String hdmac,String remateIp);
	public RpcResponseDTO<Boolean> validateIdentityCode(int countrycode, String acc,
			String hdmac, String captcha);
}
