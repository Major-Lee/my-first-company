package com.bhu.vas.api.rpc.user.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;

public interface IUserRpcService {
	public RpcResponseDTO<UserDTO> createNewUser(
			int countrycode,
			String acc,
			String nick,
			String sex,
			String device,
			String regIp,String deviceuuid,
			String captcha
			);
	
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc);
	
	public RpcResponseDTO<UserDTO> userLogin(int countrycode, String acc,String device,String remoteIp,String captcha);
	public RpcResponseDTO<UserDTO> userValidate(String aToken,String device,String remoteIp);
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
