package com.bhu.vas.api.rpc.task.stub;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class TaskServiceStub implements ITaskRpcService{
	
	private final ITaskRpcService taskRpcService;
	
    // 构造函数传入真正的远程代理对象
    public TaskServiceStub(ITaskRpcService taskRpcService) {
        this.taskRpcService = taskRpcService;
    }

	/*@Override
	public boolean taskCompleted(String taskid) {
		if(StringUtils.isEmpty(taskid)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return taskRpcService.taskCompleted(taskid);
	}*/

	@Override
	public RpcResponseDTO<TaskResDTO> createNewTask(String mac, String opt, String subopt, String extparams,/*String payload,*/
			String channel, String channel_taskid) {
		if(StringUtils.isEmpty(mac)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return taskRpcService.createNewTask(mac, opt, subopt, extparams,/*payload,*/
				channel, channel_taskid);
	}

	@Override
	public void taskStatusFetch(int taskid) {
		taskRpcService.taskStatusFetch(taskid);
	}

	@Override
	public void taskStatusFetch4ThirdParties(String channel,
			String channel_taskid) {
		taskRpcService.taskStatusFetch4ThirdParties(channel, channel_taskid);
	}

}
