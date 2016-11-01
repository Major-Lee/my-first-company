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
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/ops")
public class OperatorController extends BaseController{
	@Resource
	private IUserRpcService userRpcService;
	
	
	private static final String DefaultSecretkey = "P45zdf2TFJSU6EBHG90dc21FcLew==";
	
	private ResponseError validate(String secretKey){
		if(!DefaultSecretkey.equals(secretKey)){
			return ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}
		return null;
	}
	
	/**
	 * 普通用户升级成分销商
	 * @param request
	 * @param response
	 * @param uid
	 * @param org
	 * @param sk
	 */
	@ResponseBody()
	@RequestMapping(value="/upgrade/operator",method={RequestMethod.POST})
	public void upgradeOperator(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = false) String org,
			@RequestParam(required = false,value="ic",defaultValue="false") boolean ischannel,
			@RequestParam(required = true,value="sk") String secretKey){
		
		ResponseError validateError = validate(secretKey);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		
		RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.upgradeOperator(uid, org,ischannel);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	/**
	 * 查看用户信息
	 * @param request
	 * @param response
	 * @param uid
	 * @param org
	 * @param sk
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch/user",method={RequestMethod.POST})
	public void operatorfetchUser(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true,value="sk") String secretKey){
		
		ResponseError validateError = validate(secretKey);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		
		RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.operatorfetchUser(uid, countrycode,acc);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
}
