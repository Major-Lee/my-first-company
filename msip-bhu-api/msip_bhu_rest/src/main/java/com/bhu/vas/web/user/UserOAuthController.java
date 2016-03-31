package com.bhu.vas.web.user;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserOAuthRpcService;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.WebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping(value = "/account/oauth")
public class UserOAuthController extends BaseController{
	
	@Resource
	private IUserOAuthRpcService userOAuthRpcService;

	@ResponseBody()
	@RequestMapping(value="/fetch_identifies", method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_identifies(HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=false, defaultValue="false") boolean payment){
		try{
			RpcResponseDTO<List<UserOAuthStateDTO>> rpcResult = userOAuthRpcService.fetchRegisterIdentifies(uid,payment);
			if(!rpcResult.hasError()){
				//UserTokenDTO tokenDto =UserTokenDTO.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken));
				//String bbspwd = String.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken_BBS));
				//rpcResult.getPayload().remove(RpcResponseDTOBuilder.Key_UserToken);
				//BusinessWebHelper.setCustomizeHeader(response, tokenDto.getAtoken(),tokenDto.getRtoken());
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/remove", method={RequestMethod.GET,RequestMethod.POST})
	public void removebind(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=true) String identify){
		try{
			RpcResponseDTO<Boolean> rpcResult = userOAuthRpcService.removeIdentifies(uid, identify);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/create", method={RequestMethod.GET,RequestMethod.POST})
	public void create(
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam(required = false, value="du") String deviceuuid,
			@RequestParam(required=false) Integer uid,
			@RequestParam(required=true) String identify,
			@RequestParam(required=true) String auid,
			@RequestParam(required = false) String nick,
			@RequestParam(required = false) String avatar,
			@RequestParam(required = false) String openid,
			@RequestParam(required = false,defaultValue="N") String ut,//用户类型标识 UserType
			@RequestParam(required = false, value="d",defaultValue="R") String device
			) throws Exception{
		try{
			String remoteIp = WebHelper.getRemoteAddr(request);
			String from_device = DeviceEnum.getBySName(device).getSname();
			RpcResponseDTO<Map<String,Object>> rpcResult = userOAuthRpcService.createIdentifies(uid,identify, auid,openid, nick, avatar,
					from_device, remoteIp, deviceuuid, ut);
			if(!rpcResult.hasError()){
				UserTokenDTO tokenDto =UserTokenDTO.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken));
				rpcResult.getPayload().remove(RpcResponseDTOBuilder.Key_UserToken);
				BusinessWebHelper.setCustomizeHeader(response, tokenDto.getAtoken(),tokenDto.getRtoken());
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
			//SpringMVCHelper.renderJson(response, ResponseSuccess.embed(Boolean.TRUE));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}

}
