package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

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
	
	//@Resource
	//private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private TaskFacadeService taskFacadeService;
	//@Resource
	//private WifiDeviceGroupService wifiDeviceGroupService;
	//@Resource
	//private WifiDeviceService wifiDeviceService;
	
	public RpcResponseDTO<Boolean> taskGroupGenerate(Integer uid, int gid,boolean dependency,String mac, String opt, String subopt, String extparams,
			String channel, String channel_taskid){
		logger.info("uid==" + uid + ",gid==" + gid + ",ds_opt==" + opt + ",extparams==" + extparams);
		//发异步消息执行,所有操作在异步环境中执行
		deliverMessageService.sendWifiCmdGenMessage(uid.intValue(), gid,dependency, mac, opt, subopt, extparams, channel, channel_taskid);//sendWifiCmdCommingNotifyMessage(mac,downTask.getId(),opt,downTask.getPayload());
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		/*if(gid == 0)
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST);
		WifiDeviceGroup dgroup = wifiDeviceGroupService.getById(gid);
		if(dgroup == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST);
		}
		Set<String> totalDevices = new HashSet<String>();
		if(!dependency){
			if(dgroup.getInnerModels().isEmpty()){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_CURRENT_DEVICES_EMPTY);
			}else{
				totalDevices.addAll(dgroup.getInnerModels());
			}
		}else{
			List<WifiDeviceGroup> allByPath = wifiDeviceGroupService.fetchAllByPath(dgroup.getPath(), true);
			for(WifiDeviceGroup _dgroup:allByPath){
				totalDevices.addAll(_dgroup.getInnerModels());
			}
			if(totalDevices.isEmpty()){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_DEPENDENCY_DEVICES_EMPTY);
			}
		}
		//只给在线的设备发送指令
		List<String> onlineDevices = wifiDeviceService.filterOnlineIdsWith(new ArrayList<String>(totalDevices), true);
		if(onlineDevices.isEmpty()){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_ONLINE_DEVICES_EMPTY);
		}
		
		for(String wifi_id:onlineDevices){
			
		}*/
		
		//List<TaskResDTO>
		
		/*try{
			WifiDeviceDownTask downTask = taskFacadeService.apiTaskGenerate(uid, mac, opt, subopt, extparams, channel, channel_taskid);

			TaskResDTO dto = new TaskResDTO();
			dto.setChannel(channel);
			dto.setChannel_taskid(channel_taskid);
			dto.setState(downTask.getState());
			dto.setMac(mac);
			dto.setTaskid(downTask.getId());
			
			//发送异步消息到Queue
			deliverMessageService.sendWifiCmdCommingNotifyMessage(mac,downTask.getId(),opt,downTask.getPayload());
			return new RpcResponseDTO<TaskResDTO>(null,dto);
		}catch(BusinessI18nCodeException bex){
			logger.error("TaskGenerate invoke exception : " + bex.getMessage(), bex);
			return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("TaskGenerate invoke exception : " + ex.getMessage(), ex);
			return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}*/
	}
	
	/**
	 * @param uid
	 * @param mac
	 * @param opt
	 * @param subopt
	 * @param extparams //目前是修改配置的opt传递的是json串；//0个或1个值，如果是超过一个值则传递的是json串
	 * @param channel
	 * @param channel_taskid
	 * @return
	 */
	public RpcResponseDTO<TaskResDTO> taskGenerate(Integer uid, String mac, String opt, String subopt, String extparams,
			String channel, String channel_taskid){
		logger.info("uid==" + uid + ",mac==" + mac + ",ds_opt==" + opt + ",extparams==" + extparams);
		try{
			WifiDeviceDownTask downTask = taskFacadeService.apiTaskGenerate(uid, mac, opt, subopt, extparams, channel, channel_taskid);
			logger.info("taskComming ==end==");
			TaskResDTO dto = new TaskResDTO();
			dto.setChannel(channel);
			dto.setChannel_taskid(channel_taskid);
			dto.setState(downTask.getState());
			dto.setMac(mac);
			dto.setTaskid(downTask.getId());
			
			//发送异步消息到Queue
			deliverMessageService.sendWifiCmdCommingNotifyMessage(mac,downTask.getId(),opt,downTask.getPayload());
			return new RpcResponseDTO<TaskResDTO>(null,dto);
		}catch(BusinessI18nCodeException bex){
			logger.error("TaskGenerate invoke exception : " + bex.getMessage(), bex);
			return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("TaskGenerate invoke exception : " + ex.getMessage(), ex);
			return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}
	}
	
	/**
	 * 查询任务的状态
	 * @param uid
	 * @param channel
	 * @param channel_taskid
	 * @param taskid
	 * @return
	 */
	public RpcResponseDTO<TaskResDTO> taskStatus(Integer uid, String channel, String channel_taskid, Long taskid){
		logger.info("uid==" + uid + ",channel==" + channel + ",channel_taskid==" + channel_taskid + ",taskid=="+taskid);
		try{
			WifiDeviceDownTask task = null;
			if(taskid != null){
				task = taskFacadeService.queryTask(taskid);
			}else{
				task = taskFacadeService.queryTask(channel, channel_taskid);
			}
			
			TaskResDTO dto = new TaskResDTO();
			dto.setChannel(channel);
			dto.setChannel_taskid(channel_taskid);
			dto.setState(formatedTaskState(task.getState()));
			dto.setMac(task.getMac());
			dto.setTaskid(task.getId());
			
			return new RpcResponseDTO<TaskResDTO>(null,dto);
		}catch(BusinessI18nCodeException bex){
			logger.error("TaskGenerate invoke exception : " + bex.getMessage(), bex);
			return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("TaskGenerate invoke exception : " + ex.getMessage(), ex);
			return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}
	}
	
	/**
	 * 任务状态查询接口的状态转换
	 * api接口只提供4种状态
	 * State_Done State_Timeout State_Failed State_Pending
	 * @param state
	 * @return
	 */
	protected String formatedTaskState(String state){
		if(StringUtils.isEmpty(state)) return WifiDeviceDownTask.State_Failed;
		if(WifiDeviceDownTask.State_Done.equals(state) || WifiDeviceDownTask.State_Pending.equals(state)
				|| WifiDeviceDownTask.State_Timeout.equals(state)){
			return state;
		}
		return WifiDeviceDownTask.State_Failed;
	}
	
//	public RpcResponseDTO<TaskResDTO> taskGenerate(String mac, String opt, String subopt, String extparams,
//			String channel, String channel_taskid){
//		WifiDeviceDownTask downTask = new WifiDeviceDownTask();
//		downTask.setChannel(channel);
//		downTask.setChannel_taskid(channel_taskid);
//		//downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, taskid));
//		downTask.setOpt(opt);
//		downTask.setMac(mac);
//		int ret = taskFacadeService.taskComming(downTask);
//		if(ret == RpcResponseCodeConst.Task_Startup_OK){
//			if(OperationCMD.ModifyDeviceSetting.getNo().equals(opt)){
//				String payload = deviceFacadeService.generateDeviceSetting(mac, subopt, extparams);
//				downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, downTask.getId(),payload));
//			}else{
//				downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, downTask.getId(),extparams));
//			}
//			//deviceFacadeService.generateDeviceSettingAd(mac,)
//			//DeviceSettingBuilderDTO dto1 = null;
//			
//			//WifiDeviceSettingAdDTO dto1 = new WifiDeviceSettingAdDTO();
//			/*deviceFacadeService.generateDeviceSettingAd(mac,);*/
//			
//			TaskResDTO dto = new TaskResDTO();
//			dto.setChannel(channel);
//			dto.setChannel_taskid(channel_taskid);
//			dto.setState(downTask.getState());
//			dto.setMac(mac);
//			dto.setTaskid(downTask.getId());
//			taskFacadeService.taskUpdate(downTask);
//			//发送异步消息到Queue
//			deliverMessageService.sendWifiCmdCommingNotifyMessage(mac,downTask.getId(),opt,downTask.getPayload());
//			return new RpcResponseDTO<TaskResDTO>(null,dto);
//		}else{
//			ResponseErrorCode errorcode = null;
//			switch(ret){
//				case RpcResponseCodeConst.Task_Illegal:
//					errorcode = ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL;
//					//ex = new BusinessException(ResponseStatus.BadRequest,ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
//					break;
//				case RpcResponseCodeConst.Task_Already_Exist:
//					errorcode = ResponseErrorCode.COMMON_DATA_ALREADYEXIST;
//					break;
//				case RpcResponseCodeConst.Task_Already_Completed:
//					errorcode = ResponseErrorCode.COMMON_DATA_ALREADYDONE;
//					break;
//				default:
//					errorcode = ResponseErrorCode.COMMON_BUSINESS_ERROR;
//					break;
//			}
//			return new RpcResponseDTO<TaskResDTO>(errorcode,null);
//		}
//	}
}
