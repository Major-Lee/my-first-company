package com.bhu.vas.api.rpc.task.iservice;


public interface ITaskRpcService {
	public boolean createNewTask(
			String mac,
			String opt,
			String payload,
			String channel,
			String channel_taskid);
	//public boolean taskCompleted(String taskid);
}
