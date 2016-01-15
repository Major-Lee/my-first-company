package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDetailDTO;
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;
import com.bhu.vas.rpc.facade.TaskUnitFacadeService;

@Service("taskRpcService")
public class TaskRpcService implements ITaskRpcService{

	private final Logger logger = LoggerFactory.getLogger(TaskRpcService.class);
	@Resource
	private TaskUnitFacadeService taskUnitFacadeService;

	@Override
	public RpcResponseDTO<Boolean> createNewTask4Group(Integer uid, int gid,
			boolean dependency,String mac, String opt, String subopt, String extparams,
			String channel, String channel_taskid) {
		logger.info(String.format("createNewTask4Group uid:%s gid:%s dependency:%s mac:%s opt:%s extparams:%s channel:%s channel_taskid:%s", 
				uid,gid,dependency,mac,opt,extparams,channel,channel_taskid));
		return taskUnitFacadeService.taskGroupGenerate(uid, gid, dependency, mac, opt, subopt, extparams, channel, channel_taskid);
	}
	
	/**
	 * 创建任务
	 * 创建任务成功后发送任务创建成功的异步消息
	 * 并返回taskid给客户端
	 */
	@Override
	public RpcResponseDTO<TaskResDTO> createNewTask(Integer uid, String mac, String opt, String subopt, String extparams,
			String channel, String channel_taskid) {
		logger.info(String.format("createNewTask uid:%s mac:%s opt:%s extparams:%s channel:%s channel_taskid:%s", 
				uid,mac,opt,extparams,channel,channel_taskid));
		return taskUnitFacadeService.taskGenerate(uid, mac, opt,subopt,extparams/*, payload*/, channel, channel_taskid);
	}

	/**
	 * 任务状态获取（对内）
	 */
	@Override
	public void taskStatusFetch(long taskid) {
		logger.info(String.format("taskStatusFetch mac:%s", taskid));
	}

	/**
	 * 任务状态获取（对外）
	 */
	@Override
	public RpcResponseDTO<TaskResDTO> taskStatusFetch4ThirdParties(Integer uid, String channel,
			String channel_taskid, Long taskid) {
		logger.info(String.format("taskStatusFetch4ThirdParties uid:%s channel:%s channel_taskid:%s taskid:%s", uid, channel,channel_taskid,taskid));
		return taskUnitFacadeService.taskStatus(uid, channel, channel_taskid, taskid);	
	}

	@Override
	public RpcResponseDTO<TaskResDetailDTO> taskStatusDetailFetch4ThirdParties(
			Integer uid, String channel, String channel_taskid, Long taskid) {
		logger.info(String.format("taskStatusDetailFetch4ThirdParties uid:%s channel:%s channel_taskid:%s taskid:%s", uid, channel,channel_taskid,taskid));
		return taskUnitFacadeService.taskDetailStatus(uid, channel, channel_taskid, taskid);	
	}

}
