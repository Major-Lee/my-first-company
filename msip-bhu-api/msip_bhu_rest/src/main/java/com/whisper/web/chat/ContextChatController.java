package com.whisper.web.chat;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.business.enumtype.UserGuide;
import com.smartwork.msip.cores.fsstorerule.zip.ZipService;
import com.smartwork.msip.jdo.Response;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.whisper.api.user.model.User;
import com.whisper.business.asyn.web.builder.DeliverMessageType;
import com.whisper.business.asyn.web.model.IDTO;
import com.whisper.business.asyn.web.service.DeliverMessageService;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.ValidatePermissionCheckController;

@Controller
@RequestMapping("/contextchat")
public class ContextChatController extends ValidatePermissionCheckController{
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource(name="zipService")
	private ZipService zipService;
	
	/**
	 * http的消息发送接口，一般是在长连接断开的情况下调用
	 * @param request
	 * @param response
	 * @param uid 	from
	 * @param to	to
	 * @param cid 	context chat id
	 * @param dur 	时长
	 */
	@ResponseBody()
	@RequestMapping(value="/send",method={RequestMethod.GET,RequestMethod.POST})
	public void send(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String msg
			/*@RequestParam(required = true) Integer to,
			@RequestParam(required = true) String cid,
			@RequestParam(required = false,defaultValue="") String dur*/) {
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			deliverMessageService.sendUserContextChatActionMessage(DeliverMessageType.AC.getPrefix(), uid,msg, IDTO.ACT_ADD);
			SpringMVCHelper.renderJson(response, Response.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 用户划掉的消息需要调用的接口，用于清楚会话消息的数据以及相关的文件
	 * @param request
	 * @param response
	 * @param uid
	 * @param type
	 */
	@ResponseBody()
	@RequestMapping(value="/clear",method={RequestMethod.GET,RequestMethod.POST})
	public void clear(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String cid,
			@RequestParam(required = false) String to) {
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		System.out.println(String.format("Context clear: uid[%s] cid[%s]", uid,cid));
		try{
			if(cid.indexOf(String.valueOf(uid)) != -1){
				deliverMessageService.sendUserContextChatActionMessage(DeliverMessageType.AC.getPrefix(), uid,cid, IDTO.ACT_DELETE);
				SpringMVCHelper.renderJson(response, Response.SUCCESS);
				System.out.println(String.format("Context clear okay: uid[%s] cid[%s]", uid,cid));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
			}
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
		/*if(StringUtils.isNotEmpty(to)){
			User touser = this.userService.getById(to);
			validateUserNotNull(touser);
		}
		try{
			char type = zipService.getZipRule().ID2BusinessType(cid);
			if(type == AbstractMediaRule.Peer2Peer_Business_MediaChat_Type){
				List<String> businessInfo = zipService.getZipRule().parserBusinessInfo(cid);
				if(businessInfo!= null && !businessInfo.isEmpty() 
						&& businessInfo.contains(String.valueOf(uid))&& businessInfo.contains(String.valueOf(to))){
					deliverMessageService.sendUserContextChatActionMessage(DeliverMessageType.AC.getPrefix(), uid,cid, IDTO.ACT_DELETE);
					SpringMVCHelper.renderJson(response, Response.SUCCESS);
				}else{
					SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
				}
				
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}*/
	}
	
	
	/**
	 * guide消息触发接口，可以指定给客户发送特定的Guide消息
	 * @param request
	 * @param response
	 * @param uid
	 * @param g
	 */
	@ResponseBody()
	@RequestMapping(value="/guide",method={RequestMethod.GET,RequestMethod.POST})
	public void guide(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String g) {
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		UserGuide userGuide = UserGuide.getByIndex(g);
		if(userGuide == null){
			SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
			return;
		}
		try{
			deliverMessageService.sendUserContextChatGuideActionMessage(DeliverMessageType.AC.getPrefix(), uid,userGuide.getIndex());
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
