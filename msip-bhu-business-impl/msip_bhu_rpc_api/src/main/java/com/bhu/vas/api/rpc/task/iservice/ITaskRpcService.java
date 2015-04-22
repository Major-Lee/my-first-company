package com.bhu.vas.api.rpc.task.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;


public interface ITaskRpcService {
	public RpcResponseDTO<TaskResDTO> createNewTask(
			String mac,
			String opt,
			String extparams,
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
