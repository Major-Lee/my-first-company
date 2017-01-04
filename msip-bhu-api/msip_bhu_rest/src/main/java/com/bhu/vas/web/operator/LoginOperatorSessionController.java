package com.bhu.vas.web.operator;

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
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.WebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/ops/sessions")
public class LoginOperatorSessionController extends BaseController{
	@Resource
	private IUserRpcService userRpcService;
	
	/**
	 * 运营商系统帐号密码登录
	 * 帐号包括：nick&mobileno
	 * @param request
	 * @param response
	 * @param acc mobileno
	 * @param pwd 登录密码
	 * @param lang 区域
	 * @param device 设备 
	 */
	@ResponseBody()
	@RequestMapping(value="/create",method={RequestMethod.POST})
	public void create(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String pwd,
			@RequestParam(required = false, value="d",defaultValue="P") String device) {
		String remoteIp = WebHelper.getRemoteAddr(request);
		String from_device = DeviceEnum.getBySName(device).getSname();
		RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.operatorLogin(countrycode, acc,pwd, from_device, remoteIp);
		if(!rpcResult.hasError()){
			UserTokenDTO tokenDto =UserTokenDTO.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken));
			rpcResult.getPayload().remove(RpcResponseDTOBuilder.Key_UserToken);
			BusinessWebHelper.setCustomizeHeader(response, tokenDto.getAtoken(),tokenDto.getRtoken());
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	
	
}
