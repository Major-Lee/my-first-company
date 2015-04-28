package com.bhu.vas.web.cmd;

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
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.msip.exception.BusinessException;
import com.smartwork.msip.jdo.ResponseStatus;
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
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String opt,
			@RequestParam(required = true) String subopt,
			@RequestParam(required = false) String extparams,
			@RequestParam(required = false, defaultValue=WifiDeviceDownTask.Task_LOCAL_CHANNEL) String channel,
			@RequestParam(required = false) String channel_taskid) {
		
		RpcResponseDTO<TaskResDTO> resp = taskRpcService.createNewTask(mac.toLowerCase(), opt , subopt, extparams,/*payload,*/ channel, channel_taskid);
		
		//System.out.println("~~~~~~~~~~~~~~~~~:"+resp.getResCode());
		if(resp.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(resp.getPayload()));
			return;
		}else{
			throw new BusinessException(ResponseStatus.BadRequest,resp.getErrorCode());
		}
	}
	
	
	/*@ResponseBody()
	@RequestMapping(value="/htmlingenerate",method={RequestMethod.GET,RequestMethod.POST})
	public void htmlInjectionGenerate(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String opt,
			@RequestParam(required = true) String enable,
			@RequestParam(required = true) String adurl,
			@RequestParam(required = true) String adid,
			
			@RequestParam(required = false, defaultValue=WifiDeviceDownTask.Task_LOCAL_CHANNEL) String channel,
			@RequestParam(required = false) String channel_taskid) {
		
		RpcResponseDTO<TaskResDTO> resp = taskRpcService.createNewTask(mac, opt, payload, channel, channel_taskid);
		
		//System.out.println("~~~~~~~~~~~~~~~~~:"+resp.getResCode());
		if(resp.getErrorCode() == null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(resp.getPayload()));
			return;
		}else{
			throw new BusinessException(ResponseStatus.BadRequest,resp.getErrorCode());
		}
	}*/
}
