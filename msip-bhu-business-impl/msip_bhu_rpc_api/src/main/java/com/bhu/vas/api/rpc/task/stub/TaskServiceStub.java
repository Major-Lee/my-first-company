package com.bhu.vas.api.rpc.task.stub;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDetailDTO;
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
	public RpcResponseDTO<TaskResDTO> createNewTask(Integer uid, String mac, String opt, String subopt, String extparams,/*String payload,*/
			String channel, String channel_taskid) {
		if(uid == null || StringUtils.isEmpty(mac)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return taskRpcService.createNewTask(uid, mac, opt, subopt, extparams,/*payload,*/
				channel, channel_taskid);
	}

	@Override
	public void taskStatusFetch(long taskid) {
		taskRpcService.taskStatusFetch(taskid);
	}

	@Override
	public RpcResponseDTO<TaskResDTO> taskStatusFetch4ThirdParties(Integer uid, String channel,
			String channel_taskid, Long taskid) {
		if(uid == null)
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		if(StringUtils.isEmpty(channel_taskid) && taskid == null){
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
		
		return taskRpcService.taskStatusFetch4ThirdParties(uid, channel, channel_taskid, taskid);
	}

	@Override
	public RpcResponseDTO<Boolean> createNewTask4Group(Integer uid, int gid,
			boolean dependency, String mac, String opt, String subopt,
			String extparams, String channel, String channel_taskid) {
		if(uid == null || (gid == 0 && StringUtils.isEmpty(mac))) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return taskRpcService.createNewTask4Group(uid, gid, dependency, mac, opt, subopt, extparams, channel, channel_taskid);
	}

	@Override
	public RpcResponseDTO<TaskResDetailDTO> taskStatusDetailFetch4ThirdParties(
			Integer uid, String channel, String channel_taskid, Long taskid) {
		if(uid == null)
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		
		if(StringUtils.isEmpty(channel_taskid) && taskid == null){
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
		
		return taskRpcService.taskStatusDetailFetch4ThirdParties(uid, channel, channel_taskid, taskid);
	}

}
