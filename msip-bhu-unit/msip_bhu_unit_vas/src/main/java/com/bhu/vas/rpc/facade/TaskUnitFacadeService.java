package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.rpc.RpcResponseCodeConst;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * task RPC组件的业务service
 * @author Edmond Lee
 *
 */
@Service
public class TaskUnitFacadeService {
	//private final Logger logger = LoggerFactory.getLogger(TaskUnitFacadeService.class);

	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private TaskFacadeService taskFacadeService;

	
	/**
	 * 
	 * @param mac
	 * @param opt
	 * @param subopt
	 * @param extparams //目前是修改配置的opt传递的是json串；//0个或1个值，如果是超过一个值则传递的是json串
	 * @param channel
	 * @param channel_taskid
	 * @return
	 */
	public RpcResponseDTO<TaskResDTO> taskGenerate(String mac, String opt, String subopt, String extparams,
			String channel, String channel_taskid){
		WifiDeviceDownTask downTask = new WifiDeviceDownTask();
		downTask.setChannel(channel);
		downTask.setChannel_taskid(channel_taskid);
		//downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, taskid));
		downTask.setOpt(opt);
		downTask.setMac(mac);
		int ret = taskFacadeService.taskComming(downTask);
		if(ret == RpcResponseCodeConst.Task_Startup_OK){
			String payload = null;
			if(OperationCMD.ModifyDeviceSetting.getNo().equals(opt)){
				payload = deviceFacadeService.generateDeviceSetting(mac, subopt, extparams);
			}else{
				payload = extparams;
			}
			//deviceFacadeService.generateDeviceSettingAd(mac,)
			//DeviceSettingBuilderDTO dto1 = null;
			
			//WifiDeviceSettingAdDTO dto1 = new WifiDeviceSettingAdDTO();
			/*deviceFacadeService.generateDeviceSettingAd(mac,);*/
			downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, downTask.getId(),payload));
			TaskResDTO dto = new TaskResDTO();
			dto.setChannel(channel);
			dto.setChannel_taskid(channel_taskid);
			dto.setState(downTask.getState());
			dto.setMac(mac);
			dto.setTaskid(downTask.getId());
			taskFacadeService.taskUpdate(downTask);
			//发送异步消息到Queue
			deliverMessageService.sendWifiCmdCommingNotifyMessage(mac,downTask.getId(),opt,downTask.getPayload());
			return new RpcResponseDTO<TaskResDTO>(null,dto);
		}else{
			ResponseErrorCode errorcode = null;
			switch(ret){
				case RpcResponseCodeConst.Task_Illegal:
					errorcode = ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL;
					//ex = new BusinessException(ResponseStatus.BadRequest,ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
					break;
				case RpcResponseCodeConst.Task_Already_Exist:
					errorcode = ResponseErrorCode.COMMON_DATA_ALREADYEXIST;
					break;
				case RpcResponseCodeConst.Task_Already_Completed:
					errorcode = ResponseErrorCode.COMMON_DATA_ALREADYDONE;
					break;
				default:
					errorcode = ResponseErrorCode.COMMON_BUSINESS_ERROR;
					break;
			}
			return new RpcResponseDTO<TaskResDTO>(errorcode,null);
		}
	}
}
