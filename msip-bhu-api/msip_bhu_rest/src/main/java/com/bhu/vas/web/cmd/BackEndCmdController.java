/*package com.bhu.vas.web.cmd;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDetailDTO;
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/noauth/cmd")
public class BackEndCmdController extends BaseController{
	@Resource
	private ITaskRpcService taskRpcService;
	
	private static final String DefaultSecretkey = "PzdfTFJSUEBHG0dcWFcLew==";
	private ResponseError validate(String secretKey){
		if(!DefaultSecretkey.equals(secretKey)){
			return ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}
		return null;
	}
	*//**
	 * 创建任务接口
	 * @param request
	 * @param response
	 * @param mac
	 * @param opt
	 * @param channel
	 * @param channel_taskid
	 *//*
	@ResponseBody()
	@RequestMapping(value="/generate",method={RequestMethod.POST})
	public void cmdGenerate(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true,value="sk") String secretKey,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String opt,
			@RequestParam(required = false, defaultValue="00") String subopt,
			@RequestParam(required = false) String extparams,
			@RequestParam(required = false, defaultValue=WifiDeviceDownTask.Task_LOCAL_CHANNEL) String channel,
			@RequestParam(required = false) String channel_taskid) throws  Exception{
		ResponseError validateError = validate(secretKey);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TaskResDTO> rpcResult = taskRpcService.createNewTask(-1, mac.toLowerCase(), opt, subopt, extparams,payload, channel, channel_taskid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	}
	*//**
	 * 查询任务状态接口
	 * @param request
	 * @param response
	 * @param uid
	 * @param channel
	 * @param channel_taskid
	 *//*
	@ResponseBody()
	@RequestMapping(value="/status",method={RequestMethod.POST})
	public void cmdStatus(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true,value="sk") String secretKey,
			//@RequestParam(required = true) Integer uid,
			@RequestParam(required = false, defaultValue=WifiDeviceDownTask.Task_LOCAL_CHANNEL) String channel,
			@RequestParam(required = false) String channel_taskid,
			@RequestParam(required = false) Long taskid) {
		ResponseError validateError = validate(secretKey);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TaskResDTO> rpcResult = taskRpcService.taskStatusFetch4ThirdParties(-1, channel, channel_taskid, taskid);
		
		//System.out.println("~~~~~~~~~~~~~~~~~:"+resp.getResCode());
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	}
	
	*//**
	 * 
	 * @param request
	 * @param response
	 * @param uid
	 * @param channel
	 * @param channel_taskid
	 * @param taskid
	 *//*
	@ResponseBody()
	@RequestMapping(value="/detail",method={RequestMethod.POST})
	public void cmdDetail(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true,value="sk") String secretKey,
			@RequestParam(required = false, defaultValue=WifiDeviceDownTask.Task_LOCAL_CHANNEL) String channel,
			@RequestParam(required = false) String channel_taskid,
			@RequestParam(required = false) Long taskid) {
		ResponseError validateError = validate(secretKey);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TaskResDetailDTO> rpcResult = taskRpcService.taskStatusDetailFetch4ThirdParties(-1, channel, channel_taskid, taskid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	}
}
*/