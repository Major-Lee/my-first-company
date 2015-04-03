package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseCodeConst;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;

/**
 * task RPC组件的业务service
 * @author Edmond Lee
 *
 */
@Service
public class TaskUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(TaskUnitFacadeService.class);

	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private TaskFacadeService taskFacadeService;
	
	
	public RpcResponseDTO<TaskResDTO> taskGenerate(String mac, String opt, String payload,
			String channel, String channel_taskid){
		WifiDeviceDownTask downTask = new WifiDeviceDownTask();
		downTask.setChannel(channel);
		downTask.setChannel_taskid(channel_taskid);
		downTask.setPayload(payload);
		downTask.setOpt(opt);
		downTask.setMac(mac);
		int ret = taskFacadeService.taskComming(downTask);
		if(ret == RpcResponseCodeConst.Task_Startup_OK){
			//发送异步消息到Queue
			deliverMessageService.sendWifiCmdCommingNotifyMessage(mac,downTask.getId(),opt,payload);
			TaskResDTO dto = new TaskResDTO();
			dto.setChannel(channel);
			dto.setChannel_taskid(channel_taskid);
			dto.setState(downTask.getState());
			dto.setMac(mac);
			dto.setTaskid(downTask.getId());
			
			return new RpcResponseDTO<TaskResDTO>(RpcResponseCodeConst.Task_Startup_OK,dto);
		}else{
			return new RpcResponseDTO<TaskResDTO>(ret,null);
		}
	}
}
