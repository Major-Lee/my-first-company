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
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.bhu.vas.business.helper.BusinessWebHelper;
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
	
	@Resource
	private IDeviceRestRpcService deviceRestRpcService;
	
	
	private static final String DefaultSecretkey = "P45zdf2TFJSU6EBHG90dc21FcLew==";
	
	private ResponseError validate(String secretKey, HttpServletRequest request){
		if(!DefaultSecretkey.equals(secretKey)){
			return ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID, BusinessWebHelper.getLocale(request));
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
			@RequestParam(required = false,value="ic",defaultValue="channel") String userType,
			@RequestParam(required = true,value="sk") String secretKey){
		
		ResponseError validateError = validate(secretKey, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		
		RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.upgradeOperator(uid, org,userType);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
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
		
		ResponseError validateError = validate(secretKey, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		
		RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.operatorfetchUser(uid, countrycode,acc);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}
	

	/**
	 * 修改设备行业信息和商户信息
	 * @param request
	 * @param response
	 * @param uid
	 * @param org
	 * @param sk
	 */
//	@ResponseBody()
//	@RequestMapping(value="/devinfo/update",method={RequestMethod.POST})
//	public void deviceInfoUpdate(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@RequestParam(required = true) Integer uid,
//			@RequestParam(required = true) String macs,
//			@RequestParam(required = true) String industry,
//			@RequestParam(required = true) String merchant_name,
//			@RequestParam(required = true,value="sk") String secretKey){
//		
//		ResponseError validateError = validate(secretKey);
//		if(validateError != null){
//			SpringMVCHelper.renderJson(response, validateError);
//			return;
//		}
//		
//    	if(StringUtils.isEmpty(industry))
//			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"industry"});
//    	if(StringUtils.isEmpty(merchant_name))
//			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"merchant_name"});
//    	if(StringUtils.isEmpty(macs))
//			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"macs"});
//
//    	String[] macarry = macs.toLowerCase().split(StringHelper.COMMA_STRING_GAP);
//		
//		RpcResponseDTO<Boolean> rpcResult = deviceRestRpcService.deviceInfoUpdate(Arrays.asList(macarry), industry, merchant_name);
//		
//		if(!rpcResult.hasError()){
//			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
//		}else{
//			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
//		}
//	}
}
