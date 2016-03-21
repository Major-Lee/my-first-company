package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.ret.param.ParamVasModuleDTO;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDetailDTO;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.task.notify.ITaskProcessNotifyCallback;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
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
	public RpcResponseDTO<TaskResDTO> taskGenerate(Integer uid, final String mac, String opt, String subopt, final String extparams,
			String channel, String channel_taskid){
		try{
			WifiDeviceDownTask downTask = taskFacadeService.apiTaskGenerate(uid, mac, opt, subopt, extparams, channel, channel_taskid,
					new ITaskProcessNotifyCallback(){
						@Override
						public void notify(int uid, OperationCMD opt_cmd,
								OperationDS ods_cmd, String dmac, Object payload) {
							if(ods_cmd != null && uid>0){
								switch(ods_cmd){
									case DS_Http_VapModuleCMD_Start:
										ParamVasModuleDTO param_dto =(ParamVasModuleDTO) payload;//JsonHelper.getDTO(extparams, ParamVasModuleDTO.class);
										if(param_dto != null && StringUtils.isNotEmpty(param_dto.getStyle()))
											deliverMessageService.sendDevicesModuleStyleChangedNotifyMessage(uid,param_dto.getStyle(),dmac);
										break;
									case DS_Http_VapModuleCMD_Stop:
										deliverMessageService.sendDevicesModuleStyleChangedNotifyMessage(uid,StringUtils.EMPTY,dmac);
										break;
									case DS_SharedNetworkWifi_Limit:
										{
											ParamSharedNetworkDTO shared_dto = (ParamSharedNetworkDTO) payload;
											List<String> dmacs = new ArrayList<>();
											dmacs.add(dmac);
											deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,shared_dto.getNtype(), dmacs,true,IDTO.ACT_UPDATE);
										}
										break;
									case DS_SharedNetworkWifi_Start:
										{
											ParamSharedNetworkDTO shared_dto = (ParamSharedNetworkDTO) payload;
											List<String> dmacs = new ArrayList<>();
											dmacs.add(dmac);
											deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,shared_dto.getNtype(), dmacs,true,IDTO.ACT_UPDATE);
										}
										break;
									case DS_SharedNetworkWifi_Stop:
										{
											List<String> dmacs = new ArrayList<>();
											dmacs.add(dmac);
											deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,null, dmacs,true,IDTO.ACT_DELETE);
										}
										break;	
									default:
										break;
								}
							}
						}
				
			});
			TaskResDTO dto = new TaskResDTO();
			dto.setChannel(channel);
			dto.setChannel_taskid(channel_taskid);
			dto.setState(downTask.getState());
			dto.setMac(mac);
			dto.setTaskid(downTask.getId());
			//发送异步消息到Queue
			deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac,/*downTask.getId(),opt,*/downTask.getPayload());
			return new RpcResponseDTO<TaskResDTO>(null,dto);
		}catch(BusinessI18nCodeException bex){
			//logger.error("TaskGenerate invoke exception : " + bex.getMessage(), bex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
			//return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("TaskGenerate invoke exception : " + ex.getMessage(), ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			//return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}finally{
			//在设如果是需要持久化增值指令则需要进行,x需要更新索引
			{
				/*OperationDS ods_cmd = OperationDS.getOperationDSFromNo(subopt);
				if(ods_cmd != null){
					switch(ods_cmd){
						case DS_Http_VapModuleCMD_Start:
							ParamVasModuleDTO param_dto = JsonHelper.getDTO(extparams, ParamVasModuleDTO.class);
							if(param_dto != null && StringUtils.isNotEmpty(param_dto.getStyle()))
								deliverMessageService.sendDevicesModuleStyleChangedNotifyMessage(uid,param_dto.getStyle(),mac);
							break;
						case DS_Http_VapModuleCMD_Stop:
							deliverMessageService.sendDevicesModuleStyleChangedNotifyMessage(uid,StringUtils.EMPTY,mac);
							break;
						case DS_SharedNetworkWifi_Limit:
							deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(), dmacs,false,IDTO.ACT_UPDATE);
							break;
						case DS_SharedNetworkWifi_Start:
							deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(), dmacs,false,IDTO.ACT_UPDATE);
							break;
						case DS_SharedNetworkWifi_Stop:
							deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(), dmacs,false,IDTO.ACT_DELETE);
							break;	
						default:
							break;
					}
				}*/
				/*if(OperationDS.DS_Http_VapModuleCMD_Start == ods_cmd){//开启增值
					ParamVasModuleDTO param_dto = JsonHelper.getDTO(extparams, ParamVasModuleDTO.class);
					if(param_dto != null && StringUtils.isNotEmpty(param_dto.getStyle()))
						deliverMessageService.sendDevicesModuleStyleChangedNotifyMessage(uid,param_dto.getStyle(),mac);
				}
				if(OperationDS.DS_Http_VapModuleCMD_Stop == ods_cmd){//关闭增值
					deliverMessageService.sendDevicesModuleStyleChangedNotifyMessage(uid,StringUtils.EMPTY,mac);
				}*/
			}
		}
/*		try{
			WifiDeviceDownTask downTask = taskFacadeService.apiTaskGenerate(uid, mac, opt, subopt, extparams, channel, channel_taskid);
			TaskResDTO dto = new TaskResDTO();
			dto.setChannel(channel);
			dto.setChannel_taskid(channel_taskid);
			dto.setState(downTask.getState());
			dto.setMac(mac);
			dto.setTaskid(downTask.getId());
			
			//发送异步消息到Queue
			deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac,downTask.getId(),opt,downTask.getPayload());
			{
				OperationDS ods_cmd = OperationDS.getOperationDSFromNo(subopt);
				if(OperationDS.DS_Http_VapModuleCMD_Start == ods_cmd){//开启增值
					ParamVasModuleDTO param_dto = JsonHelper.getDTO(extparams, ParamVasModuleDTO.class);
					if(param_dto != null && StringUtils.isNotEmpty(param_dto.getStyle()))
						deliverMessageService.sendDevicesModuleStyleChangedNotifyMessage(uid,param_dto.getStyle(),mac);
				}
				if(OperationDS.DS_Http_VapModuleCMD_Stop == ods_cmd){//关闭增值
					deliverMessageService.sendDevicesModuleStyleChangedNotifyMessage(uid,StringUtils.EMPTY,mac);
				}
			}
			
			return new RpcResponseDTO<TaskResDTO>(null,dto);
		}catch(BusinessI18nCodeException bex){
			//logger.error("TaskGenerate invoke exception : " + bex.getMessage(), bex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
			//return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("TaskGenerate invoke exception : " + ex.getMessage(), ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			//return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}*/
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
			dto.setState(TaskFacadeService.formatedTaskState(task.getState()));
			dto.setMac(task.getMac());
			dto.setTaskid(task.getId());
			return new RpcResponseDTO<TaskResDTO>(null,dto);
		}catch(BusinessI18nCodeException bex){
			logger.error("TaskGenerate invoke exception : " + bex.getMessage(), bex);
			//return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("TaskGenerate invoke exception : " + ex.getMessage(), ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			//return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}
	}
	
	public RpcResponseDTO<TaskResDetailDTO> taskDetailStatus(Integer uid, String channel, String channel_taskid, Long taskid){
		logger.info("uid==" + uid + ",channel==" + channel + ",channel_taskid==" + channel_taskid + ",taskid=="+taskid);
		try{
			
			TaskResDetailDTO detail = null;
			if(taskid != null){
				detail = taskFacadeService.queryTaskDetail(taskid);
			}else{
				detail = taskFacadeService.queryTaskDetail(channel, channel_taskid);
			}
			return new RpcResponseDTO<TaskResDetailDTO>(null,detail);
		}catch(BusinessI18nCodeException bex){
			logger.error("TaskGenerate invoke exception : " + bex.getMessage(), bex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("TaskGenerate invoke exception : " + ex.getMessage(), ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
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
