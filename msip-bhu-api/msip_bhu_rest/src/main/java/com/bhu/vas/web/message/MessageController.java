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
import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
import com.bhu.vas.api.rpc.message.iservice.IMessageUserRpcService;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/message")
public class MessageController extends BaseController{
	@Resource
	private IMessageUserRpcService messageUserRpcService;
	
	private static final String DefaultSecretkey = "PzdfTFJSUEBHG0dcWFcLew==";
	
	private ResponseError validate(String secretKey, HttpServletRequest request){
		if(!DefaultSecretkey.equals(secretKey)){
			return ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID, BusinessWebHelper.getLocale(request));
		}
		return null;
	}
	
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/send/single_msg",method={RequestMethod.POST})
	public void send_single_msg(
			HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true,value="sk") String secretKey,
			@RequestParam(required = false, defaultValue="100") Integer sendChannel,
			@RequestParam(required = true) String toAcc,
			@RequestParam(required = false, defaultValue="200") Integer msgType,
			@RequestParam(required = true) String content
			){
		ResponseError validateError = validate(secretKey, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TimResponseBasicDTO> rpcResult = messageUserRpcService.send_single_msg(sendChannel, toAcc, msgType, content);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/send/push",method={RequestMethod.POST})
	public void send_push(
			HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true,value="sk") String secretKey,
			@RequestParam(required = false, defaultValue="100") Integer sendChannel,
			@RequestParam(required = false) String tags,
			@RequestParam(required = false,defaultValue="0") int msgLifeTime,
			@RequestParam(required = false, defaultValue="200") Integer msgType,
			@RequestParam(required = true) String content
			){
		ResponseError validateError = validate(secretKey, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TimResponseBasicDTO> rpcResult = messageUserRpcService.send_push(sendChannel, tags, msgLifeTime, msgType, content);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
}
		
