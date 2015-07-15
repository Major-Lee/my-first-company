package com.bhu.vas.business.ds.task.facade;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.dto.ret.param.ParamCmdWifiTimerStartDTO;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTaskCompleted;
import com.bhu.vas.api.rpc.user.dto.UserWifiTimerSettingDTO;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskCompletedService;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class TaskFacadeService {
	@Resource
	private WifiDeviceDownTaskService wifiDeviceDownTaskService;
	
	@Resource
	private WifiDeviceDownTaskCompletedService wifiDeviceDownTaskCompletedService;
	
	@Resource
	private UserSettingStateService userSettingStateService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	/**
	 * 任务执行callback通知
	 * @param taskid
	 * @param status
	 */
	public WifiDeviceDownTaskCompleted taskExecuteCallback(int taskid,String state,String response){
		WifiDeviceDownTask downtask = wifiDeviceDownTaskService.getById(taskid);
		if(downtask == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_UNDEFINED);
		}
		/*if(WifiDeviceDownTask.State_Done.equals(state) || WifiDeviceDownTask.State_Failed.equals(state)){
			WifiDeviceDownTaskCompleted completed = WifiDeviceDownTaskCompleted.fromWifiDeviceDownTask(downtask, state, response);
			WifiDeviceDownTaskCompleted result = wifiDeviceDownTaskCompletedService.insert(completed);
			wifiDeviceDownTaskService.deleteById(taskid);
			return result;
		}else{
			downtask.setState(state);
			wifiDeviceDownTaskService.update(downtask);
			return null;
		}*/
		WifiDeviceDownTaskCompleted completed = WifiDeviceDownTaskCompleted.fromWifiDeviceDownTask(downtask, state, response);
		WifiDeviceDownTaskCompleted result = wifiDeviceDownTaskCompletedService.insert(completed);
		wifiDeviceDownTaskService.deleteById(taskid);
		return result;
	}
	
	public void taskComming(WifiDeviceDownTask downtask){
		//if(downtask == null || StringUtils.isEmpty(downtask.getMac())/* || StringUtils.isEmpty(downtask.getPayload())*/) return RpcResponseCodeConst.Task_Illegal;
		if(downtask == null || StringUtils.isEmpty(downtask.getMac()))
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_VALIDATE_ILEGAL);
		
		if(!WifiDeviceDownTask.Task_LOCAL_CHANNEL.equals(downtask.getChannel())){//如果不是本地taskid
			if(StringUtils.isNotEmpty(downtask.getChannel_taskid())){  //Channel_taskid不能为空
				ModelCriteria mc = new ModelCriteria();
				mc.createCriteria().andColumnEqualTo("channel_taskid", downtask.getChannel_taskid()).andColumnEqualTo("channel", downtask.getChannel());
				int count  = wifiDeviceDownTaskService.countByModelCriteria(mc);
				if(count > 0)
					throw new BusinessI18nCodeException(ResponseErrorCode.TASK_ALREADY_EXIST);
				count  = wifiDeviceDownTaskCompletedService.countByModelCriteria(mc);
				if(count > 0) 
					throw new BusinessI18nCodeException(ResponseErrorCode.TASK_ALREADY_COMPLETED);
			}else{//抛出channelid空异常
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_CHANNELTASKID_ILLEGAL);
			}
		}
		
		/*if(StringUtils.isNotEmpty(downtask.getChannel_taskid()) && StringUtils.isNotEmpty(downtask.getChannel()) ){//外部应用触发任务
			//看看WifiDeviceDownTaskService是否存在此任务
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("channel_taskid", downtask.getChannel_taskid()).andColumnEqualTo("channel", downtask.getChannel());
			int count  = wifiDeviceDownTaskService.countByModelCriteria(mc);
			//if(count > 0) return RpcResponseCodeConst.Task_Already_Exist;
			if(count > 0)
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_ALREADY_EXIST);
			count  = wifiDeviceDownTaskCompletedService.countByModelCriteria(mc);
			//if(count > 0) return RpcResponseCodeConst.Task_Already_Completed;
			if(count > 0) 
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_ALREADY_COMPLETED);
		}*/
		downtask = wifiDeviceDownTaskService.insert(downtask);
	}
	
	
	public void taskUpdate(WifiDeviceDownTask downtask){
		//if(downtask == null || StringUtils.isEmpty(downtask.getMac()) || StringUtils.isEmpty(downtask.getPayload())) return RpcResponseCodeConst.Task_Illegal;
		if(downtask == null || StringUtils.isEmpty(downtask.getMac()) || StringUtils.isEmpty(downtask.getPayload())) 
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_VALIDATE_ILEGAL);
		downtask = wifiDeviceDownTaskService.update(downtask);
	}
	
	/**
	 * 把设备的所有下发的未完成的任务全部设置成失败
	 * @param mac
	 */
	public void taskStateFailByDevice(String mac){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("mac", mac);
		List<Integer> taskids = wifiDeviceDownTaskService.findIdsByModelCriteria(mc);
		if(!taskids.isEmpty()){
			for(Integer taskid : taskids){
				taskExecuteCallback(taskid, WifiDeviceDownTask.State_Failed, null);
			}
		}
	}
	/**
	 * 查询任务的状态 会对timeout的任务进行标记
	 * 1：已完成任务中查询
	 * 2：pending任务中查询
	 * @param channel
	 * @param channel_taskid
	 * @return
	 */
	public WifiDeviceDownTask queryTask(String channel, String channel_taskid){
		//从已完成任务中获取
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("channel_taskid", channel_taskid).andColumnEqualTo("channel", channel);
		List<WifiDeviceDownTaskCompleted> taskCompleteds = wifiDeviceDownTaskCompletedService.findModelByModelCriteria(mc);
		if(!taskCompleteds.isEmpty()){
			return taskCompleteds.get(0);
		}
		List<WifiDeviceDownTask> taskDowns = wifiDeviceDownTaskService.findModelByModelCriteria(mc);
		if(!taskDowns.isEmpty()){
			//如果获取的是pending任务 判断时间是否timeout
			WifiDeviceDownTask pending_task = taskDowns.get(0);
			validateTaskTimeout(pending_task);
//			WifiDeviceDownTask pending_task = taskDowns.get(0);
//			if((System.currentTimeMillis() - pending_task.getCreated_at().getTime()) 
//					> RuntimeConfiguration.TaskTimeoutMilliSecs){
//				//设置任务状态为timeout 此处不算任务完成
//				pending_task.setState(WifiDeviceDownTask.State_Timeout);
//				pending_task = wifiDeviceDownTaskService.update(pending_task);
//			}
			return pending_task;
		}
		throw new BusinessI18nCodeException(ResponseErrorCode.TASK_NOT_EXIST);
	}
	
	/**
	 * 查询任务的状态 会对timeout的任务进行标记
	 * 1：已完成任务中查询
	 * 2：pending任务中查询
	 * @param taskid
	 * @return
	 */
	public WifiDeviceDownTask queryTask(Integer taskid){
		if(taskid != null){
			//从已完成任务中获取
			WifiDeviceDownTaskCompleted taskCompleted = wifiDeviceDownTaskCompletedService.getById(taskid);
			if(taskCompleted != null)
				return taskCompleted;
			
			WifiDeviceDownTask pending_task = wifiDeviceDownTaskService.getById(taskid);
			//如果获取的是pending任务 判断时间是否timeout
			if(pending_task != null){
				validateTaskTimeout(pending_task);
				return pending_task;
			}
		}
		throw new BusinessI18nCodeException(ResponseErrorCode.TASK_NOT_EXIST);
	}
	
	public WifiDeviceDownTask findWifiDeviceDownTaskById(Integer taskid){
		if(taskid != null){
			WifiDeviceDownTask pending_task = wifiDeviceDownTaskService.getById(taskid);
			if(pending_task == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_NOT_EXIST);
			}else
				return pending_task;
		}
		throw new BusinessI18nCodeException(ResponseErrorCode.TASK_NOT_EXIST);
	}
	/**
	 * 验证任务是否超时 如果超时则修改任务状态
	 * 下发任务的时候有可能出现2种情况会造成timeout
	 * 1：下发指令发送到设备 设备没有上报回应
	 * 2：下发指令未发送到设备 
	 * @param pending_task
	 */
	public void validateTaskTimeout(WifiDeviceDownTask pending_task){
		if(pending_task == null || !WifiDeviceDownTask.State_Pending.equals(pending_task.getState())) return;
		if((System.currentTimeMillis() - pending_task.getCreated_at().getTime()) 
				> RuntimeConfiguration.TaskTimeoutMilliSecs){
			//设置任务状态为timeout 此处不算任务完成
			pending_task.setState(WifiDeviceDownTask.State_Timeout);
			//this.taskUpdate(pending_task);
			pending_task = wifiDeviceDownTaskService.update(pending_task);
		}
	}
	
	/*public boolean cancelTask(int taskid){
		WifiDeviceDownTask downtask = wifiDeviceDownTaskService.getById(taskid);
		if(downtask != null) 
	}*/
	
	
	public WifiDeviceDownTask apiTaskGenerate(int uid, String mac, String opt, String subopt, String extparams,
			String channel, String channel_taskid) throws Exception{
		//如果是管理员用户 不进行用户所属设备的验证
		WifiDevice wifiDevice = null;
		if(RuntimeConfiguration.isConsoleUser(uid)){
			wifiDevice = deviceFacadeService.validateDevice(mac);
		}else{
			wifiDevice = deviceFacadeService.validateUserDevice(uid, mac);
		}
		if(	OperationDS.DS_Http_404_Start.getNo().equals(subopt) 
				|| OperationDS.DS_Http_404_Stop.getNo().equals(subopt)
				|| OperationDS.DS_Http_Ad_Start.getNo().equals(subopt) 
				|| OperationDS.DS_Http_Ad_Stop.getNo().equals(subopt)
				|| OperationDS.DS_Http_Redirect_Start.getNo().equals(subopt) 
				|| OperationDS.DS_Http_Redirect_Stop.getNo().equals(subopt)
				|| OperationDS.DS_Http_Portal_Start.getNo().equals(subopt) 
				|| OperationDS.DS_Http_Portal_Stop.getNo().equals(subopt) 
				){
			if(!VapModeDefined.supported(wifiDevice.getWork_mode())){//验证设备的工作模式是否支持增值指令
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VAP_WORKMODE_NOT_SUPPORTED);
			}
		}
		
		
		WifiDeviceDownTask downTask = new WifiDeviceDownTask();
		downTask.setUid(uid);
		downTask.setChannel(channel);
		downTask.setChannel_taskid(channel_taskid);
		
		downTask.setContext_var(extparams);
		//downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, taskid));
		downTask.setSubopt(subopt);
		downTask.setOpt(opt);
		downTask.setMac(mac);
		this.taskComming(downTask);
		if(OperationCMD.ModifyDeviceSetting.getNo().equals(opt)){
			/*if(OperationDS.DS_Http_404.getNo().equals(subopt)){
				//404和portal指令需要先发送cmd resource update指令给设备，等收到设备反馈后再继续发送配置指令
				downTask.setPayload(CMDBuilder.builderCMD4Http404ResourceUpdate(mac, downTask.getId(), extparams));
			}else */
			if(OperationDS.DS_Http_Portal_Start.getNo().equals(subopt)){
				//portal指令需要先发送cmd resource update指令给设备，等收到设备反馈后再继续发送配置指令
				downTask.setPayload(CMDBuilder.builderCMD4HttpPortalResourceUpdate(mac, downTask.getId(), extparams));
			}else{
				String payload = deviceFacadeService.generateDeviceSetting(mac, subopt, extparams);
				downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, downTask.getId(),payload));
			}
		}else{
			if(OperationCMD.DeviceWifiTimerStart.getNo().equals(opt)){//需要先增加到个人配置表中
				ParamCmdWifiTimerStartDTO param_dto = JsonHelper.getDTO(extparams, ParamCmdWifiTimerStartDTO.class);
				UserWifiTimerSettingDTO innerDTO = new UserWifiTimerSettingDTO();
				innerDTO.setOn(true);
				innerDTO.setDs(false);
				innerDTO.setTimeslot(param_dto.getTimeslot());
				userSettingStateService.updateUserSetting(mac, UserWifiTimerSettingDTO.Setting_Key, JsonHelper.getJSONString(innerDTO));
			}
			downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, downTask.getId(),extparams));
		}
		this.taskUpdate(downTask);
		return downTask;
	}
	
	public WifiDeviceDownTask systemTaskGenerate(int uid, String mac, String opt, String subopt, String extparams) throws Exception{
		//如果是管理员用户 不进行用户所属设备的验证
		if(RuntimeConfiguration.isConsoleUser(uid)){
			deviceFacadeService.validateDevice(mac);
		}else{
			deviceFacadeService.validateUserDevice(uid, mac);
		}
		
		WifiDeviceDownTask downTask = new WifiDeviceDownTask();
		downTask.setUid(uid);
		downTask.setChannel(WifiDeviceDownTask.Task_LOCAL_CHANNEL);
		downTask.setChannel_taskid(null);
		downTask.setContext_var(extparams);
		downTask.setSubopt(subopt);
		downTask.setOpt(opt);
		downTask.setMac(mac);
		this.taskComming(downTask);
		if(OperationCMD.ModifyDeviceSetting.getNo().equals(opt)){
			String payload = deviceFacadeService.generateDeviceSetting(mac, subopt, extparams);
			downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, downTask.getId(),payload));
		}else{
			downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, downTask.getId(),extparams));
		}
		this.taskUpdate(downTask);
		return downTask;
	}
}
