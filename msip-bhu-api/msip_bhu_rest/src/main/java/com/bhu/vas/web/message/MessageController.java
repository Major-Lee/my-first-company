package com.bhu.vas.web.message;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.message.dto.MessageUserSigDTO;
import com.bhu.vas.api.rpc.message.iservice.IMessageUserRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/message")
public class MessageController extends BaseController{
	@Resource
	private IMessageUserRpcService messageUserRpcService;

	@ResponseBody()
	@RequestMapping(value="/user/fetch_usersig",method={RequestMethod.GET,RequestMethod.POST})
	public void user_fetch_usersig(
			HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue="0") Integer channel
			){
		
		RpcResponseDTO<MessageUserSigDTO> rpcResult = messageUserRpcService.fetch_usersig(uid, channel);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/visitor/fetch_usersig",method={RequestMethod.GET,RequestMethod.POST})
	public void visitor_fetch_usersig(
			HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String user,
			@RequestParam(required = false,defaultValue="0") Integer channel
			){
		
		RpcResponseDTO<MessageUserSigDTO> rpcResult = messageUserRpcService.fetch_visitor_usersig(user, channel);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
}
		
