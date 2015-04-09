package com.bhu.vas.api.rpc.user.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;


public interface IUserRpcService {
	public RpcResponseDTO<TaskResDTO> createNewTask(
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
			);
	//public boolean taskCompleted(String taskid);
}
