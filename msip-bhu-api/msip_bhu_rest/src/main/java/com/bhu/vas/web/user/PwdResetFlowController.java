package com.bhu.vas.web.user;

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
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
/**
 * 密码重置流程
 * 1、用户输入注册手机号，获取验证码，点击密码重置
 * 2、用户收到验证码，点击链接，ajax进行token验证，单纯返回验证成功与否，此步骤需要进行验证码刷新
 * 3、如果成功，客户端页面把token带入到密码重置表单页面
 * @author new
 *
 */
@Controller
@RequestMapping("/account")
public class PwdResetFlowController extends BaseController{
	@Resource
	private IUserRpcService userRpcService;
	
	/*@Resource
	private UserService userService;
	
	@Resource
	private UserResetPwdService userResetPwdService;
	
	@Resource
	private DeliverMessageService deliverMessageService;*/
	/**
	 * 密码重置第一步骤
	 * @param request
	 * @param response
	 * @param email
	 * @param jsonpcallback
	 *//*
	@ResponseBody()
	@RequestMapping(value="/forgot_password",method={RequestMethod.GET,RequestMethod.POST})
	public void forgot_password(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String acc) {
		try{
			Integer uid = null;
			if(StringHelper.isValidEmailCharacter(acc)){
				uid = UniqueFacadeService.fetchUidByEmail(acc);
			}else{
				uid = UniqueFacadeService.fetchUidByMobileno(acc);
			}
			if(!StringHelper.isValidEmailCharacter(acc)){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_EMAIL_INVALID_FORMAT, new String[]{email}));
				return;
			}
			Integer uid = UniqueFacadeService.fetchUidByEmail(acc);
			if(uid == null || uid.intValue() == 0){
				SpringMVCHelper.renderJson(response, Response.SUCCESS);
				return;
			}
			
			User user = this.userService.getById(uid);
			//User user = userService.findUserByEmail(email);
			//不管后台库中是否存在此email,都给前台返回成功信息，防止前台用户穷举
			if(user == null){
				SpringMVCHelper.renderJson(response, Response.SUCCESS);
				return;
			}
			UserResetPwd reset = userResetPwdService.getById(user.getId());
			if(reset==null){
				reset = userResetPwdService.insert(new UserResetPwd(user.getId(),acc));
			}else{
				//20s内不进行新的token生成
				if((System.currentTimeMillis()-reset.getUpdated_at().getTime())/1000 < 30){//20s内不进行新的token生成
					SpringMVCHelper.renderJson(response, Response.SUCCESS);
					return;
				}
				userResetPwdService.update(reset);
			}
			deliverMessageService.sendUserResetPwdActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), reset.getToken(),acc,WebHelper.getRemoteAddr(request));
        	SpringMVCHelper.renderJson(response, Response.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	*//**
	 * 密码重置第二步骤
	 * @param request
	 * @param response
	 * @param token
	 * @param jsonpcallback
	 *//*
	@ResponseBody()
	@RequestMapping(value="/verify_forgot_token",method={RequestMethod.GET,RequestMethod.POST})
	public void verify_forgot_token(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String token) {
		try{
			Integer uid = null;
			if(StringHelper.isValidEmailCharacter(acc)){
				uid = UniqueFacadeService.fetchUidByEmail(acc);
			}else{
				uid = UniqueFacadeService.fetchUidByMobileno(acc);
			}
			UserResetPwd reset = userResetPwdService.getById(uid);
			if(reset == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));//renderHtml(response, html, headers)
				return;
			}else{
				boolean expired = System.currentTimeMillis()>reset.getUpdated_at().getTime();
				if(expired){
					userResetPwdService.deleteById(uid);
					SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_TIMEOUT));
					return;
				}else{
					if(token.equals(reset.getToken())){
			        	SpringMVCHelper.renderJson(response, Response.SUCCESS);//.embed(email));
			        	return;
					}else{
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));//renderHtml(response, html, headers)
						return;
					}
				}
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}*/
	
	/**
	 * 密码重置第三步骤
	 * token会被重置
	 * @param request
	 * @param response
	 * @param acc 手机号码
	 * @param captcha
	 * @param pwd
	 * @param jsonpcallback
	 */
	@ResponseBody()
	@RequestMapping(value="/reset_password",method={RequestMethod.GET,RequestMethod.POST})
	public void reset_password(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String captcha,
			@RequestParam(required = true) String pwd,
			@RequestParam(required = false, value="d",defaultValue="R") String device
			) {
		String remoteIp = WebHelper.getRemoteAddr(request);
		String from_device = DeviceEnum.getBySName(device).getSname();
		try{
			ResponseError validateError = ValidateService.validateMobilenoRegx(countrycode,acc);
			if(validateError != null){
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}
			RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.userResetPwd(countrycode, acc, pwd, from_device, remoteIp, captcha);
			if(!rpcResult.hasError()){
				UserTokenDTO tokenDto =UserTokenDTO.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken));
				rpcResult.getPayload().remove(RpcResponseDTOBuilder.Key_UserToken);
				BusinessWebHelper.setCustomizeHeader(response, tokenDto.getAtoken(),tokenDto.getRtoken());
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 修改密码操作
	 * token会被重置
	 * @param request
	 * @param response
	 * @param uid
	 * @param pwd
	 * @param npwd
	 */
	@ResponseBody()
	@RequestMapping(value="/change_pwd",method={RequestMethod.GET,RequestMethod.POST})
	public void change_password(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true, value = "uid") int uid,
			@RequestParam(required = true) String pwd,
			@RequestParam(required = true) String npwd
			) {
		try{
			RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.userChangedPwd(uid, pwd, npwd);
			if(!rpcResult.hasError()){
				UserTokenDTO tokenDto =UserTokenDTO.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken));
				rpcResult.getPayload().remove(RpcResponseDTOBuilder.Key_UserToken);
				BusinessWebHelper.setCustomizeHeader(response, tokenDto.getAtoken(),tokenDto.getRtoken());
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
