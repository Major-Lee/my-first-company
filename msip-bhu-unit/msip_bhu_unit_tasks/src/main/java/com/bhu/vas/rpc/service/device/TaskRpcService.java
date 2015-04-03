package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;
import com.bhu.vas.rpc.facade.TaskUnitFacadeService;

@Service("taskRpcService")
public class TaskRpcService implements ITaskRpcService{

	@Resource
	private TaskUnitFacadeService taskUnitFacadeService;

	/**
	 * 创建任务
	 * 创建任务成功后发送任务创建成功的异步消息
	 * 并返回taskid给客户端
	 */
	@Override
	public RpcResponseDTO<TaskResDTO> createNewTask(String mac, String opt, String payload,
			String channel, String channel_taskid) {
		System.out.println(String.format("createNewTask mac:%s", mac));
		return taskUnitFacadeService.taskGenerate(mac, opt, payload, channel, channel_taskid);
	}

	/**
	 * 任务状态获取（对内）
	 */
	@Override
	public void taskStatusFetch(int taskid) {
		System.out.println(String.format("taskStatusFetch mac:%s", taskid));
	}

	/**
	 * 任务状态获取（对外）
	 */
	@Override
	public void taskStatusFetch4ThirdParties(String channel,
			String channel_taskid) {
		System.out.println(String.format("taskStatusFetch4ThirdParties channel:%s channel_taskid:%s", channel,channel_taskid));
	}

}
