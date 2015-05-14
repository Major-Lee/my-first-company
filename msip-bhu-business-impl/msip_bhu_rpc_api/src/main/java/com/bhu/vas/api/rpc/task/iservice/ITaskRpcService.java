package com.bhu.vas.api.rpc.task.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;


public interface ITaskRpcService {
	public RpcResponseDTO<TaskResDTO> createNewTask(
			Integer uid,
			String mac,
			String opt,
			String subopt,
			String extparams,
			//String payload,
			String channel,
			String channel_taskid);
	
	public void taskStatusFetch(
			int taskid
			);
	
	public RpcResponseDTO<TaskResDTO> taskStatusFetch4ThirdParties(
			Integer uid,
			String channel,
			String channel_taskid,
			Integer taskid
			);
	//public boolean taskCompleted(String taskid);
}
