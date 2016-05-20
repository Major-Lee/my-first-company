package com.bhu.vas.web.console;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.WebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/sessions")
public class LoginConsoleSessionController extends BaseController{
	@Resource
	private IUserRpcService userRpcService;

	/**
	 * 帐号密码登录
	 * 帐号包括：email&mobileno
	 * @param request
	 * @param response
	 * @param acc email或者 mobileno
	 * @param pwd 登录密码
	 * @param lang 区域
	 * @param device 设备 
	 */
	@ResponseBody()
	@RequestMapping(value="/create",method={RequestMethod.POST})
	public void login(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String pwd,
			@RequestParam(required = false, value="d",defaultValue="P") String device) {
		//step 1.手机号正则验证
		ResponseError validateError = ValidateService.validateMobilenoRegx(countrycode, acc);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		String remoteIp = WebHelper.getRemoteAddr(request);
		String from_device = DeviceEnum.getBySName(device).getSname();
		
		//RpcResponseDTO<UserDTO> userLogin = userRpcService.userLogin(countrycode, acc, from_device, remoteIp, captcha);
		RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.userConsoleLogin(countrycode, acc, pwd, from_device, remoteIp);
				//userCreateOrLogin(countrycode, acc, from_device, remoteIp, captcha);
		if(!rpcResult.hasError()){
			UserTokenDTO tokenDto =UserTokenDTO.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken));
			if(!BusinessRuntimeConfiguration.isConsoleUser(tokenDto.getId())){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
				return;
			}
			rpcResult.getPayload().remove(RpcResponseDTOBuilder.Key_UserToken);
			BusinessWebHelper.setCustomizeHeader(response, tokenDto.getAtoken(),tokenDto.getRtoken());
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
		
	}

	/**
	 * token登录验证
	 * @param request
	 * @param response
	 * @param device
	 */
	@ResponseBody()
	@RequestMapping(value="/validates",method={RequestMethod.POST})
	public void validate(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, value="d",defaultValue="P") String device) {
		/*
		 1、获取远端IP
		 2、获取cookie中ip token
		 3、判断两者是否一样，不一样的话清楚远端cookie，并抛出失败response json
		 */
		String aToken = request.getHeader(RuntimeConfiguration.Param_ATokenHeader);
		System.out.println("~~~~~step2 token:"+aToken);
		if(StringUtils.isEmpty(aToken)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_EMPTY));
			return;
		}
		
		String remoteIp = WebHelper.getRemoteAddr(request);
		String from_device = DeviceEnum.getBySName(device).getSname();
		
		//RpcResponseDTO<UserDTO> rpcResult = userRpcService.userValidate(aToken, from_device, remoteIp);
		RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.userValidate(aToken,null, from_device, remoteIp);
		if(!rpcResult.hasError()){
			UserTokenDTO tokenDto =UserTokenDTO.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken));
			rpcResult.getPayload().remove(RpcResponseDTOBuilder.Key_UserToken);
			BusinessWebHelper.setCustomizeHeader(response, tokenDto.getAtoken(),tokenDto.getRtoken());
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
}
