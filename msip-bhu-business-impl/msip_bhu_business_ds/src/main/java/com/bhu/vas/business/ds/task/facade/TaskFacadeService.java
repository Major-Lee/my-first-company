package com.bhu.vas.business.ds.task.facade;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.dto.ret.param.ParamCmdWifiTimerStartDTO;
import com.bhu.vas.api.dto.ret.param.ParamVapVistorLimitWifiDTO;
import com.bhu.vas.api.dto.ret.param.ParamVapVistorWifiDTO;
import com.bhu.vas.api.dto.ret.param.ParamVasModuleDTO;
import com.bhu.vas.api.dto.ret.param.ParamVasSwitchWorkmodeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingUserDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceUpgradeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceVisitorKickoffDTO;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.task.dto.TaskResDetailDTO;
import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTaskCompleted;
import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.api.rpc.user.dto.UserVistorWifiSettingDTO;
import com.bhu.vas.api.rpc.user.dto.UserWifiTimerSettingDTO;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceVisitorService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.task.service.VasModuleCmdDefinedService;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskCompletedService;
import com.bhu.vas.business.ds.task.service.WifiDeviceDownTaskService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
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
	private WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	
	@Resource
	private VasModuleCmdDefinedService vasModuleCmdDefinedService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private WifiDeviceModuleService wifiDeviceModuleService;
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;

	/**
	 * 任务执行callback通知
	 * @param taskid
	 */
	public WifiDeviceDownTaskCompleted taskExecuteCallback(long taskid,String state,String response){
		WifiDeviceDownTask downtask = wifiDeviceDownTaskService.getById(taskid);
		if(downtask == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_UNDEFINED,new String[]{String.valueOf(taskid)});
		}
		if(WifiDeviceDownTask.State_Done.equals(state) || WifiDeviceDownTask.State_Ok.equals(state) || WifiDeviceDownTask.State_Failed.equals(state) || WifiDeviceDownTask.State_Error.equals(state)){
			WifiDeviceDownTaskCompleted completed = WifiDeviceDownTaskCompleted.fromWifiDeviceDownTask(downtask, state, response);
			WifiDeviceDownTaskCompleted result = wifiDeviceDownTaskCompletedService.insert(completed);
			wifiDeviceDownTaskService.deleteById(taskid);
			return result;
		}else{
			downtask.setState(state);
			wifiDeviceDownTaskService.update(downtask);
			return null;
		}
		/*WifiDeviceDownTaskCompleted completed = WifiDeviceDownTaskCompleted.fromWifiDeviceDownTask(downtask, state, response);
		WifiDeviceDownTaskCompleted result = wifiDeviceDownTaskCompletedService.insert(completed);
		wifiDeviceDownTaskService.deleteById(taskid);
		return result;*/
	}
	
	public void taskComming(WifiDeviceDownTask downtask){
		//if(downtask == null || StringUtils.isEmpty(downtask.getMac())/* || StringUtils.isEmpty(downtask.getPayload())*/) return RpcResponseCodeConst.Task_Illegal;
		if(downtask == null || StringUtils.isEmpty(downtask.getMac())  || StringUtils.isEmpty(downtask.getPayload()))
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
		if(WifiDeviceHelper.isAutoCompletedTask(downtask.getOpt())){
			Date current = new Date();
			downtask.setCreated_at(current);
			downtask.setUpdated_at(current);
			WifiDeviceDownTaskCompleted completed = WifiDeviceDownTaskCompleted.fromWifiDeviceDownTask(downtask, WifiDeviceDownTask.State_Done, "System AutoCompleted");
			wifiDeviceDownTaskCompletedService.insert(completed);
		}else{
			downtask = wifiDeviceDownTaskService.insert(downtask);
		}
	}
	
	
	/*public void taskUpdate(WifiDeviceDownTask downtask){
		//if(downtask == null || StringUtils.isEmpty(downtask.getMac()) || StringUtils.isEmpty(downtask.getPayload())) return RpcResponseCodeConst.Task_Illegal;
		if(downtask == null || StringUtils.isEmpty(downtask.getMac()) || StringUtils.isEmpty(downtask.getPayload())) 
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_VALIDATE_ILEGAL);
		downtask = wifiDeviceDownTaskService.update(downtask);
	}*/
	
	/**
	 * 把设备的所有下发的未完成的任务全部设置成失败
	 * @param mac
	 */
	public void taskStateFailByDevice(String mac){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("mac", mac);
		List<Long> taskids = wifiDeviceDownTaskService.findIdsByModelCriteria(mc);
		if(!taskids.isEmpty()){
			for(Long taskid : taskids){
				taskExecuteCallback(taskid, WifiDeviceDownTask.State_Failed, null);
			}
		}
	}
	
	/**
	 * 查询任务的状态 会对timeout的任务进行标记
	 * 1：已完成任务中查询
	 * 2：pending任务中查询
	 * @param taskid
	 * @return
	 */
	public WifiDeviceDownTask queryTask(Long taskid){
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
	 * 任务状态查询接口的状态转换
	 * api接口只提供4种状态
	 * State_Done State_Timeout State_Failed State_Pending
	 * @param state
	 * @return
	 */
	public static String formatedTaskState(String state){
		if(StringUtils.isEmpty(state)) return WifiDeviceDownTask.State_Failed;
		if(WifiDeviceDownTask.State_Done.equals(state) 
				|| WifiDeviceDownTask.State_Ok.equals(state) 
				|| WifiDeviceDownTask.State_Pending.equals(state)
				|| WifiDeviceDownTask.State_Timeout.equals(state)){
			return state;
		}
		return WifiDeviceDownTask.State_Failed;
	}
	
	public TaskResDetailDTO queryTaskDetail(Long taskid){
		TaskResDetailDTO detail = null;
		if(taskid != null){
			//从已完成任务中获取
			WifiDeviceDownTaskCompleted taskCompleted = wifiDeviceDownTaskCompletedService.getById(taskid);
			if(taskCompleted != null){
				detail = new TaskResDetailDTO();
				detail.setChannel(taskCompleted.getChannel());
				detail.setChannel_taskid(taskCompleted.getChannel_taskid());
				detail.setState(formatedTaskState(taskCompleted.getState()));
				detail.setMac(taskCompleted.getMac());
				detail.setTaskid(taskCompleted.getId());
				detail.setResponse(taskCompleted.getResponse());
				return detail;
			}
			WifiDeviceDownTask pending_task = wifiDeviceDownTaskService.getById(taskid);
			//如果获取的是pending任务 判断时间是否timeout
			if(pending_task != null){
				validateTaskTimeout(pending_task);
				detail = new TaskResDetailDTO();
				detail.setChannel(pending_task.getChannel());
				detail.setChannel_taskid(pending_task.getChannel_taskid());
				detail.setState(formatedTaskState(pending_task.getState()));
				detail.setMac(pending_task.getMac());
				detail.setTaskid(pending_task.getId());
				return detail;
			}
		}
		throw new BusinessI18nCodeException(ResponseErrorCode.TASK_NOT_EXIST);
	}
	
	
	public TaskResDetailDTO queryTaskDetail(String channel, String channel_taskid){
		TaskResDetailDTO detail = null;
		WifiDeviceDownTaskCompleted taskCompleted = null;
		//从已完成任务中获取
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("channel_taskid", channel_taskid).andColumnEqualTo("channel", channel);
		List<WifiDeviceDownTaskCompleted> taskCompleteds = wifiDeviceDownTaskCompletedService.findModelByModelCriteria(mc);
		if(!taskCompleteds.isEmpty()){
			taskCompleted = taskCompleteds.get(0);
			detail = new TaskResDetailDTO();
			detail.setChannel(taskCompleted.getChannel());
			detail.setChannel_taskid(taskCompleted.getChannel_taskid());
			detail.setState(formatedTaskState(taskCompleted.getState()));
			detail.setMac(taskCompleted.getMac());
			detail.setTaskid(taskCompleted.getId());
			detail.setResponse(taskCompleted.getResponse());
			return detail;
		}
		List<WifiDeviceDownTask> taskDowns = wifiDeviceDownTaskService.findModelByModelCriteria(mc);
		if(!taskDowns.isEmpty()){
			//如果获取的是pending任务 判断时间是否timeout
			WifiDeviceDownTask pending_task = taskDowns.get(0);
			validateTaskTimeout(pending_task);
			detail = new TaskResDetailDTO();
			detail.setChannel(pending_task.getChannel());
			detail.setChannel_taskid(pending_task.getChannel_taskid());
			detail.setState(formatedTaskState(pending_task.getState()));
			detail.setMac(pending_task.getMac());
			detail.setTaskid(pending_task.getId());
			return detail;
		}
		throw new BusinessI18nCodeException(ResponseErrorCode.TASK_NOT_EXIST);
	}
	
	
	public WifiDeviceDownTask findWifiDeviceDownTaskById(Long taskid){
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
				> BusinessRuntimeConfiguration.TaskTimeoutMilliSecs){
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
		
		OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(opt);
		OperationDS ods_cmd = OperationDS.getOperationDSFromNo(subopt);
		if(opt_cmd == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		}

		//如果是管理员用户 不进行用户所属设备的验证
		WifiDevice wifiDevice = null;
		if(BusinessRuntimeConfiguration.isConsoleUser(uid)){
			wifiDevice = deviceFacadeService.validateDeviceIgoneOffline(mac);
		}else{
			if(WifiDeviceDownTask.Task_LOCAL_CHANNEL.equals(channel))
				wifiDevice = deviceFacadeService.validateUserDevice(uid, mac);
			else{
				wifiDevice = deviceFacadeService.validateUserCWifiDevice(uid,mac);
			}
		}
		
		/*if(OperationCMD.DeviceCmdPassThrough == opt_cmd){//远端透传指令，直接下发，无需生成任务
			WifiDeviceDownTask simulateTask = new WifiDeviceDownTask();
			simulateTask.setId(-1l);
			simulateTask.setChannel(channel);
			simulateTask.setChannel_taskid(channel_taskid);
			simulateTask.setPayload(CMDBuilder.builderDeviceCmdPassThrough(mac,extparams));
			simulateTask.setMac(mac);
			return simulateTask;
		}*/
		
		
		if (OperationCMD.ModifyDeviceSetting.getNo().equals(opt)) {
			if(ods_cmd == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
			}
			if (OperationDS.DS_VapPassword.getNo().equals(subopt)) {
				WifiDeviceSettingVapDTO wifiDeviceSettingVapDTO =
						JsonHelper.getDTO(extparams, WifiDeviceSettingVapDTO.class);

				if (wifiDeviceSettingVapDTO == null) {
					//非法格式
					throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
				} else {
					String ssid = wifiDeviceSettingVapDTO.getSsid();
					if (ssid == null) {
						//非法格式
						throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
					}
					if (ssid.getBytes("utf-8").length < 1 || ssid.getBytes("utf-8").length > 32 ) {
						//非法长度
						throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_LENGTH_ILEGAL);
					}

					String auth = wifiDeviceSettingVapDTO.getAuth();
					//TODO(bluesand):此处auth：  WPA/WPA2-PSK / open

					if (!"WPA/WPA2-PSK".equals(auth) && !"open".equals(auth) && !"WPA2-PSK".equals(auth)) {
						throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
					} else {
						if ("WPA/WPA2-PSK".equals(auth) || "WPA2-PSK".equals(auth)) {
							String auth_key = wifiDeviceSettingVapDTO.getAuth_key();
							if (auth_key == null) {
								throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
							}
							if(auth_key.getBytes("utf-8").length < 8  || auth_key.getBytes("utf-8").length > 32) {
								//非法长度
								throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_LENGTH_ILEGAL);
							}
						}
						if ("open".equals(auth)) {
							//nothing
						}
					}
				}

			}
			if (OperationDS.DS_AdminPassword.getNo().equals(subopt)) {
				WifiDeviceSettingUserDTO wifiDeviceSettingUserDTO =
						JsonHelper.getDTO(extparams, WifiDeviceSettingUserDTO.class);
				if (wifiDeviceSettingUserDTO == null) {
					//非法格式
					throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
				} else {
					String password = wifiDeviceSettingUserDTO.getPassword();
					if (password == null) {
						//密码为空
						throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
					}
					if(password.getBytes("utf-8").length < 4  || password.getBytes("utf-8").length > 31) {
						//非法长度
						throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_LENGTH_ILEGAL);
					}
				}
			}
		}
		//如果设备升级的话，高版本的考虑不升级
		if (OperationCMD.DeviceUpgrade == opt_cmd) {
			WifiDeviceUpgradeDTO dto = JsonHelper.getDTO(extparams, WifiDeviceUpgradeDTO.class);
			if (dto.isCtrl_version()) { //需要考虑高版本强制升级 true:考虑升级 false:默认都升级
				String url = dto.getUrl();
				if (url != null) {
					String deviceVersion = url.substring(url.lastIndexOf("/") + 1);
					int ret = DeviceVersion.compareVersions(wifiDevice.getOrig_swver(), deviceVersion);
					if (ret >= 0) {
						// 设备版本高于需要升级的版本，不升级
						throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VERSION_TOO_HIGH);
					} else {
						//升级
					}
				} else  {
					throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
				}

			} else {
				//升级
			}
		}
		//需要实体化存储的参数存入数据库中，以设备重新上线后继续发送指令
		wifiDevicePersistenceCMDStateService.filterPersistenceCMD(mac,opt_cmd,ods_cmd,extparams);
		//验证设备是否在线
		if(!wifiDevice.isOnline()){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE,new String[]{mac});
		}
		
		if(OperationCMD.DeviceModuleUpgrade == opt_cmd){//判定设备版本是否兼容
			WifiDeviceModule wifiDeviceModule = wifiDeviceModuleService.getById(mac);
			if(wifiDeviceModule == null  
					|| !WifiDeviceHelper.isDeviceVapModuleSupported(wifiDeviceModule.getOrig_vap_module())
					|| !WifiDeviceHelper.isDeviceVapModuleOnline(wifiDeviceModule.isOnline(),wifiDeviceModule.isModule_online())){
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VAP_MODULE_NOT_ONLINE,new String[]{mac});
			}
			
		}
		//判定是定义出的特殊指令 广告注入、增值指令、访客网络
		if(WifiDeviceHelper.isDeviceSpecialCmd(opt_cmd, ods_cmd)){
			if(!VapModeDefined.supported(wifiDevice.getWork_mode())){//验证设备的工作模式是否支持增值指令
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VAP_WORKMODE_NOT_SUPPORTED);
			}
		}
		
		//如果是增值指令 404或redirect，则还需要判定是否module是否在线
		if(WifiDeviceHelper.isVapCmdModuleSupported(opt_cmd,ods_cmd) ){
			if(WifiDeviceHelper.isVapCmdExceptDevice(mac)){//如果是黑名单列表里存在的mac
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VAP_MODULE_EXCEPT);
			}else{
				WifiDeviceModule wifiDeviceModule = wifiDeviceModuleService.getById(mac);
				if(wifiDeviceModule == null || !WifiDeviceHelper.isDeviceVapModuleOnline(wifiDeviceModule.isOnline(),wifiDeviceModule.isModule_online())){
					throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VAP_MODULE_NOT_ONLINE);
				}
			}
		}

		Long taskid = SequenceService.getInstance().getNextId(WifiDeviceDownTask.class.getName());
		
		WifiDeviceDownTask downTask = new WifiDeviceDownTask();
		downTask.setId(taskid);
		downTask.setUid(uid);
		downTask.setChannel(channel);
		downTask.setChannel_taskid(channel_taskid);
		
		downTask.setContext_var(extparams);
		//downTask.setPayload(CMDBuilder.builderCMD4Opt(opt, mac, taskid));
		downTask.setSubopt(subopt);
		downTask.setOpt(opt);
		downTask.setMac(mac);
		if(OperationCMD.ModifyDeviceSetting == opt_cmd){
			if(ods_cmd.hasRef()){
				switch(ods_cmd){
					case DS_Http_VapModuleCMD_Start:
						ParamVasModuleDTO param_dto = JsonHelper.getDTO(extparams, ParamVasModuleDTO.class);
						if(param_dto == null || StringUtils.isEmpty(param_dto.getStyle()))
							throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
						VasModuleCmdDefined cmdDefined = vasModuleCmdDefinedService.getById(new VasModuleCmdPK(ods_cmd.getRef(),param_dto.getStyle()));
						if(cmdDefined == null || StringUtils.isEmpty(cmdDefined.getTemplate())){
							throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VAP_MODULE_CMD_NOT_DEFINED,new String[]{ods_cmd.getRef(),param_dto.getStyle()});
						}
						downTask.setPayload(CMDBuilder.autoBuilderVapFullCMD4Opt(mac, downTask.getId(),cmdDefined.getTemplate()));
						break;
					case DS_Http_VapModuleCMD_Stop:
						String stopTemplate = vasModuleCmdDefinedService.fetchCommonStopTemplate();
						downTask.setPayload(CMDBuilder.autoBuilderVapFullCMD4Opt(mac, downTask.getId(), stopTemplate));
						break;	
					default:
						break;	
				}
			}else{
				switch(ods_cmd){
					case DS_VistorWifi_Limit:
						//需要判定访客网络是否开启
						UserSettingState user_setting_entity = userSettingStateService.getById(mac);
						if(user_setting_entity != null){
							UserVistorWifiSettingDTO uvw_dto = user_setting_entity.getUserSetting(UserVistorWifiSettingDTO.
									Setting_Key, UserVistorWifiSettingDTO.class);
							//如果开启则把limit的限速值写入到访客网络plugin配置里
							if(uvw_dto != null && uvw_dto.isOn() && uvw_dto.getVw() != null){//访客网络是开启
								ParamVapVistorLimitWifiDTO ad_dto = JsonHelper.getDTO(extparams, ParamVapVistorLimitWifiDTO.class);
								ad_dto = ParamVapVistorLimitWifiDTO.fufillWithDefault(ad_dto);
								uvw_dto.getVw().setUsers_rx_rate(ad_dto.getUsers_rx_rate());
								uvw_dto.getVw().setUsers_tx_rate(ad_dto.getUsers_tx_rate());
								userSettingStateService.updateUserSetting(mac, UserVistorWifiSettingDTO.Setting_Key, JsonHelper.getJSONString(uvw_dto));
								downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd,ods_cmd, mac, downTask.getId(),extparams,deviceFacadeService));
								break;
							}
						}
						//如果未开启，则重新构建访客网络开启指令则不break，直接走DS_VistorWifi_Start开启访客网络
						ods_cmd = OperationDS.DS_VistorWifi_Start;
					case DS_VistorWifi_Start:
						{
							ParamVapVistorWifiDTO vistor_dto = JsonHelper.getDTO(extparams, ParamVapVistorWifiDTO.class);
							vistor_dto = ParamVapVistorWifiDTO.fufillWithDefault(vistor_dto,WifiDeviceHelper.isWorkModeRouter(wifiDevice.getWork_mode()));
							downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd,ods_cmd, mac, downTask.getId(),JsonHelper.getJSONString(vistor_dto),deviceFacadeService));
							UserVistorWifiSettingDTO vistorwifi = new UserVistorWifiSettingDTO();
							vistorwifi.setOn(true);
							vistorwifi.setDs(false);
							//置为空 是根据设备当前的工作模式来决定是什么值,就是参数在过程中进行初始化
							vistor_dto.setBlock_mode(null);
							vistorwifi.setVw(vistor_dto);
							userSettingStateService.updateUserSetting(mac, UserVistorWifiSettingDTO.Setting_Key, JsonHelper.getJSONString(vistorwifi));
							
						}
						break;
					case DS_VistorWifi_Stop:
						{
							UserSettingState settingState = userSettingStateService.getById(mac);
							if(settingState != null){
								UserVistorWifiSettingDTO vistorwifi = settingState.getUserSetting(UserVistorWifiSettingDTO.Setting_Key, UserVistorWifiSettingDTO.class);
								if(vistorwifi != null && vistorwifi.isOn()){
									vistorwifi.setOn(false);
									vistorwifi.setDs(false);
									vistorwifi.setVw(null);
									userSettingStateService.updateUserSetting(mac, UserVistorWifiSettingDTO.Setting_Key, JsonHelper.getJSONString(vistorwifi));
								}
							}
							downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd,ods_cmd, mac, downTask.getId(),extparams,deviceFacadeService));
						}
						break;
					case DS_Switch_WorkMode:
						WifiDeviceSettingDTO setting_dto = deviceFacadeService.validateDeviceSettingReturnDTO(mac);
						//需要判定是否可以进行切换
						ParamVasSwitchWorkmodeDTO param_dto = JsonHelper.getDTO(extparams, ParamVasSwitchWorkmodeDTO.class);
						WifiDeviceHelper.deviceWorkModeNeedChanged(wifiDevice.getWork_mode(),param_dto.getWmode(), 
								DeviceHelper.getLinkModeValue(setting_dto));
						downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd,ods_cmd, mac, downTask.getId(),extparams,deviceFacadeService));
						break;
					default:
						downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd,ods_cmd, mac, downTask.getId(),extparams/*,wifiDevice.getOrig_swver()*/,deviceFacadeService));
						break;
				}
			}
		}else{
			switch (opt_cmd) {
				case DeviceWifiTimerStart:
					ParamCmdWifiTimerStartDTO param_dto = JsonHelper.getDTO(extparams, ParamCmdWifiTimerStartDTO.class);
					UserWifiTimerSettingDTO innerDTO = new UserWifiTimerSettingDTO();
					innerDTO.setOn(true);
					innerDTO.setDs(false);
					innerDTO.setTimeslot(param_dto.getTimeslot());
					innerDTO.setDays(param_dto.getDays());
					userSettingStateService.updateUserSetting(mac, UserWifiTimerSettingDTO.Setting_Key, JsonHelper.getJSONString(innerDTO));
					downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd, mac, downTask.getId(), extparams));
					break;
				case KickOffVisitorDeviceWifiHandset:
					WifiDeviceVisitorKickoffDTO dto = JsonHelper.getDTO(extparams, WifiDeviceVisitorKickoffDTO.class);
					String macs = dto.getHd_mac();
					String[] handsetIds = null;
					if (macs != null) {
						handsetIds = macs.split(",");
					}
					WifiDeviceVisitorService.getInstance().removePresents(mac, handsetIds);

					downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd, mac, downTask.getId(), extparams));
					break;
				default:
					downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd, mac, downTask.getId(), extparams));
					break;

			}
		}
		this.taskComming(downTask);
		return downTask;
	}
	
	public WifiDeviceDownTask systemTaskGenerate(int uid, String mac, String opt, String subopt, String extparams) throws Exception{
		OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(opt);
		if(opt_cmd == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		}
		
		OperationDS ods_cmd = OperationDS.getOperationDSFromNo(subopt);
		
		//WifiDevice wifiDevice = null;
		//如果是管理员用户 不进行用户所属设备的验证
		if(BusinessRuntimeConfiguration.isConsoleUser(uid)){
			deviceFacadeService.validateDevice(mac);
		}else{
			deviceFacadeService.validateUserDevice(uid, mac);
		}
		Long taskid = SequenceService.getInstance().getNextId(WifiDeviceDownTask.class.getName());
		WifiDeviceDownTask downTask = new WifiDeviceDownTask();
		downTask.setId(taskid);
		downTask.setUid(uid);
		downTask.setChannel(WifiDeviceDownTask.Task_LOCAL_CHANNEL);
		downTask.setChannel_taskid(null);
		downTask.setContext_var(extparams);
		downTask.setSubopt(subopt);
		downTask.setOpt(opt);
		downTask.setMac(mac);
		
		downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd,ods_cmd, mac, downTask.getId(),extparams/*,wifiDevice.getOrig_swver()*/,deviceFacadeService));
/*=======
		downTask.setPayload(CMDBuilder.autoBuilderCMD4Opt(opt_cmd, ods_cmd, mac, downTask.getId(), extparams, wifiDevice.getOrig_swver(), deviceFacadeService));
>>>>>>> bacd3bab901df86e48761ff076d84a2471f98f3f*/
		this.taskComming(downTask);
		//this.taskUpdate(downTask);
		return downTask;
	}

	/*public static void main(String[] args){

			String extparams = "{\"url\":\"http://7xl3iu.dl1.z0.glb.clouddn.com/device/build/AP106P06V1.3.0Build8328\",\"upgrade_begin\":\"\",\"upgrade_end\":\"\",\"ctrl_version\":true}";

			WifiDeviceUpgradeDTO dto = JsonHelper.getDTO(extparams, WifiDeviceUpgradeDTO.class);
			if (dto.isCtrl_version()) { //需要考虑高版本强制升级 true:考虑升级 false:默认都升级

				String url = dto.getUrl();
				if (url != null) {
					String deviceVersion = url.substring(url.lastIndexOf("/")+1);
					int ret = DeviceVersion.compareVersions("AP106P06V1.2.15Build8105", deviceVersion);
					System.out.println(ret);
					if (ret >= 0) {
						// 设备版本高于需要升级的版本，不升级
						throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VERSION_TOO_HIGH);
					} else {
						//升级
					}

				} else  {
					throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
				}

			} else {
				//升级
			}
		}*/

}
