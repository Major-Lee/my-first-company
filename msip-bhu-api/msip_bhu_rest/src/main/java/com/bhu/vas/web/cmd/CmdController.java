package com.bhu.vas.web.cmd;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingUserDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/cmd")
public class CmdController extends BaseController{
	@Resource
	private ITaskRpcService taskRpcService;
	
	
	/**
	 * 创建任务接口
	 * @param request
	 * @param response
	 * @param mac
	 * @param opt
	 * @param channel
	 * @param channel_taskid
	 */
	@ResponseBody()
	@RequestMapping(value="/generate",method={RequestMethod.POST})
	public void cmdGenerate(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String opt,
			@RequestParam(required = false, defaultValue="00") String subopt,
			@RequestParam(required = false) String extparams,
			@RequestParam(required = false, defaultValue=WifiDeviceDownTask.Task_LOCAL_CHANNEL) String channel,
			@RequestParam(required = false) String channel_taskid) throws  Exception{


		if (OperationCMD.ModifyDeviceSetting.getNo().equals(opt)) {
			if (OperationDS.DS_VapPassword.getNo().equals(subopt)) {
				WifiDeviceSettingVapDTO wifiDeviceSettingVapDTO =
						JsonHelper.getDTO(extparams, WifiDeviceSettingVapDTO.class);

				if (wifiDeviceSettingVapDTO == null) {
					//非法格式
					SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
					return;
				} else {
					String ssid = wifiDeviceSettingVapDTO.getSsid();
					if (ssid == null) {
						//非法格式
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
						return;
					}
					if (ssid.getBytes("utf-8").length < 1 || ssid.getBytes("utf-8").length > 32 ) {
						//非法长度
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_LENGTH_ILEGAL));
						return;
					}

					String auth = wifiDeviceSettingVapDTO.getAuth();
					//TODO(bluesand):此处auth：  WPA/WPA2-PSK / open

					if (!"WPA/WPA2-PSK".equals(auth) && !"open".equals(auth)) {
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
						return;
					} else {
						if ("WPA/WPA2-PSK".equals(auth)) {
							String auth_key = wifiDeviceSettingVapDTO.getAuth_key();
							if (auth_key == null) {
								SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
								return;
							}
							if(auth_key.getBytes("utf-8").length < 8  || auth_key.getBytes("utf-8").length > 32) {
								//非法长度
								SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_LENGTH_ILEGAL));
								return;
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
					SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
					return;
				} else {
					String password = wifiDeviceSettingUserDTO.getPassword();
					if (password == null) {
						//密码为空
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
						return;
					}
					if(password.getBytes("utf-8").length < 4  || password.getBytes("utf-8").length > 31) {
						//非法长度
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_LENGTH_ILEGAL));
						return;
					}
				}
			}
		}
		
		RpcResponseDTO<TaskResDTO> resp = taskRpcService.createNewTask(uid, mac.toLowerCase(), opt, subopt, extparams,/*payload,*/ channel, channel_taskid);
		
		//System.out.println("~~~~~~~~~~~~~~~~~:"+resp.getResCode());
		if(resp.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(resp.getPayload()));
			return;
		}
		SpringMVCHelper.renderJson(response, ResponseError.embed(resp.getErrorCode()));
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param uid
	 * @param gid
	 * @param dependency 支持子节点
	 * @param mac
	 * @param opt
	 * @param subopt
	 * @param extparams
	 * @param channel
	 * @param channel_taskid
	 */
	@ResponseBody()
	@RequestMapping(value="/generate4group",method={RequestMethod.POST})
	public void cmdGenerate4Group(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) int gid,
			@RequestParam(required = false) boolean dependency,
			@RequestParam(required = false) String mac,
			
			@RequestParam(required = true) String opt,
			@RequestParam(required = false, defaultValue="00") String subopt,
			@RequestParam(required = false) String extparams,
			@RequestParam(required = false, defaultValue=WifiDeviceDownTask.Task_LOCAL_CHANNEL) String channel,
			@RequestParam(required = false) String channel_taskid) {
		RpcResponseDTO<Boolean> resp = taskRpcService.createNewTask4Group(uid, gid, dependency, mac, opt, subopt, extparams, channel, channel_taskid);
		if(resp.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(resp.getPayload()));
			return;
		}
		SpringMVCHelper.renderJson(response, ResponseError.embed(resp.getErrorCode()));
	}
	
/*	@ResponseBody()
	@RequestMapping(value="/wifiSniffer",method={RequestMethod.POST})
	public void cmdWifiSniffer(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String opt,
			@RequestParam(required = false, defaultValue="00") String subopt,
			@RequestParam(required = false) String extparams) {
		
		RpcResponseDTO<TaskResDTO> resp = taskRpcService.taskStatusFetch4ThirdParties(uid, channel, channel_taskid, taskid);
		
		//System.out.println("~~~~~~~~~~~~~~~~~:"+resp.getResCode());
		if(resp.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(resp.getPayload()));
			return;
		}
		
		SpringMVCHelper.renderJson(response, ResponseError.embed(resp.getErrorCode()));
	}*/
	/**
	 * 查询任务状态接口
	 * @param request
	 * @param response
	 * @param uid
	 * @param channel
	 * @param channel_taskid
	 */
	@ResponseBody()
	@RequestMapping(value="/status",method={RequestMethod.POST})
	public void cmdStatus(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false, defaultValue=WifiDeviceDownTask.Task_LOCAL_CHANNEL) String channel,
			@RequestParam(required = false) String channel_taskid,
			@RequestParam(required = false) Integer taskid) {
		
		RpcResponseDTO<TaskResDTO> resp = taskRpcService.taskStatusFetch4ThirdParties(uid, channel, channel_taskid, taskid);
		
		//System.out.println("~~~~~~~~~~~~~~~~~:"+resp.getResCode());
		if(resp.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(resp.getPayload()));
			return;
		}
		
		SpringMVCHelper.renderJson(response, ResponseError.embed(resp.getErrorCode()));
	}
	
}
