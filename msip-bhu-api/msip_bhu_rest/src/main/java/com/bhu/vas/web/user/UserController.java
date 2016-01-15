package com.bhu.vas.web.user;

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

import com.bhu.vas.api.dto.UserType;
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
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.Response;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/account")
public class UserController extends BaseController{
	@Resource
	private IUserRpcService userRpcService;

	/**
	 * 通用用户账号创建
	 * 实现方式：deviceuuid 通过此方式 直接缺省新注册个帐号，不考虑以前是否此设备曾经登录使用过
	 * 前置条件 手机号必须采用真实的手机号 验证码必须验证
	 * 1、支持用户快速直接注册
	 * 2、支持mobileno 和验证码注册
	 * 3、支持nick和密码注册 nick可以为空 密码不可以为空
	 * 4、支持注册是填写 sex，lang，region
	 * 备注：此接口合并到LoginSessionController create 接口里
	 * @param request
	 * @param response
	 * @param deviceuuid 设备uuid
	 * @param acc 登录帐号mobileno 不能为空 后续支持手机号变更
	 * @param nick 昵称
	 * @param pwd 密码 在acc不为空的情况下 pwd可以为空，如果密码为空则采用缺省密码
	 * @param lang 语言
	 * @param region 区域
	 * @param device 设备类型
	 * @param itoken 注册邀请码
	 * @param token  渠道邀请码
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/create",method={RequestMethod.GET,RequestMethod.POST})
	public void create(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = false, value="du") String deviceuuid,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String captcha,
			@RequestParam(required = false) String nick,
			@RequestParam(required = false) String pwd,
			@RequestParam(required = false,defaultValue="男") String sex,
			@RequestParam(required = false,defaultValue="N") String ut,//用户类型标识 UserType
			@RequestParam(required = false, value="d",defaultValue="R") String device//,
			) {
		//step 1.deviceuuid 验证
		ResponseError validateError = null;
		if(StringUtils.isNotEmpty(deviceuuid)){
			validateError = ValidateService.validateDeviceUUID(deviceuuid);//, userService);
			if(validateError != null){
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}
		}
		String remoteIp = WebHelper.getRemoteAddr(request);
		String from_device = DeviceEnum.getBySName(device).getSname();
		try{
			UserType userType = UserType.getBySName(ut);
			ValidateService.validateUserTypeApiGen(userType);
			//step 2.手机号正则验证及手机是否存在验证
			validateError = ValidateService.validateMobileno(countrycode,acc);
			if(validateError != null){
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}
			if(StringUtils.isNotEmpty(nick)){
				validateError = ValidateService.validateNick(nick);
				if(validateError != null){
					SpringMVCHelper.renderJson(response, validateError);
					return;
				}
			}
			
			RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.createNewUser(countrycode, acc, nick,pwd, sex, from_device, remoteIp, deviceuuid,ut, captcha);
			if(!rpcResult.hasError()){
				UserTokenDTO tokenDto =UserTokenDTO.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken));
				//String bbspwd = String.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken_BBS));
				rpcResult.getPayload().remove(RpcResponseDTOBuilder.Key_UserToken);
				BusinessWebHelper.setCustomizeHeader(response, tokenDto.getAtoken(),tokenDto.getRtoken());
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
	/**
	 * 用户信息修改
	 * @param request
	 * @param response
	 * @param uid 用户uid
	 * @param nickname 昵称
	 * @param birthday 生日
	 * @param sex 性别
	 * @param memo 信息描述
	 * @param lang 语言
	 * @param region 地域
	 * @param isreg 是否是注册步骤
	 */
	@ResponseBody()
	@RequestMapping(value="/upd_profile",method={RequestMethod.GET,RequestMethod.POST})
	public void upd_profile(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String nick,
			@RequestParam(required = false) String avatar,
			@RequestParam(required = false,value="bday") String birthday,
			@RequestParam(required = false) String sex,
			//@RequestParam(required = false,defaultValue="") String lang,
			//@RequestParam(required = false,defaultValue="") String region,
			@RequestParam(required = false) String memo
			) {
			if(StringUtils.isEmpty(nick) && StringUtils.isEmpty(birthday) && StringUtils.isEmpty(sex) 
					&& StringUtils.isEmpty(avatar) && StringUtils.isEmpty(memo)){
				SpringMVCHelper.renderJson(response, Response.SUCCESS);
				return;
			}
			RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.updateProfile(uid, nick, avatar, sex, birthday);
			if(!rpcResult.hasError())
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			else
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	}
	
	@ResponseBody()
	@RequestMapping(value="/profile",method={RequestMethod.GET,RequestMethod.POST})
	public void profile(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String jsonpcallback
			) {
			RpcResponseDTO<Map<String, Object>> rpcResult = userRpcService.profile(uid);
			if(!rpcResult.hasError()){
				if(StringUtils.isEmpty(jsonpcallback))
					SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
				else
					SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				if(StringUtils.isEmpty(jsonpcallback))
					SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
				else
					SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseError.embed(rpcResult));
			}
				
	}
	
	@ResponseBody()
	@RequestMapping(value="/check_mobileno",method={RequestMethod.POST})
	public void check_unique(
			HttpServletResponse response,
			//@RequestParam(required = true,value="t") int type,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
            @RequestParam(required = false) String oldacc) {
		try{
			if (acc == null || acc.equals(oldacc)) {
				SpringMVCHelper.renderJson(response, Response.SUCCESS);//renderHtml(response, html, headers)
				return;
			}
			ResponseError validateError = ValidateService.validateMobileno(countrycode,acc);
			if(validateError != null){//本地正则验证
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}else{
				RpcResponseDTO<Boolean> checkAcc = userRpcService.checkAcc(countrycode, oldacc);
				if(checkAcc.getErrorCode() == null)
					SpringMVCHelper.renderJson(response, Response.SUCCESS);
				else
					SpringMVCHelper.renderJson(response, ResponseError.embed(checkAcc));
				return;
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/check_nick",method={RequestMethod.POST})
	public void check_nick(
			HttpServletResponse response,
			@RequestParam(required = true) String nick,
            @RequestParam(required = false) String oldacc) {
		try{
			if (nick == null || nick.equals(oldacc)) {
				SpringMVCHelper.renderJson(response, Response.SUCCESS);//renderHtml(response, html, headers)
				return;
			}
			ResponseError validateError = ValidateService.validateNick(nick);
			if(validateError != null){//本地正则验证
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}else{
				RpcResponseDTO<Boolean> checkAcc = userRpcService.checkNick(nick);
				if(checkAcc.getErrorCode() == null)
					SpringMVCHelper.renderJson(response, Response.SUCCESS);
				else
					SpringMVCHelper.renderJson(response, ResponseError.embed(checkAcc));
				return;
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/check_device_binded",method={RequestMethod.POST})
	public void check_device_binded(
			HttpServletResponse response,
			@RequestParam(required = true,value="dmac") String device_mac) {
		SpringMVCHelper.renderJson(response, Response.SUCCESS);
		/*try{
			if (acc == null || acc.equals(oldacc)) {
				SpringMVCHelper.renderJson(response, Response.SUCCESS);//renderHtml(response, html, headers)
				return;
			}
			ResponseError validateError = ValidateService.validateMobileno(countrycode,acc);
			if(validateError != null){//本地正则验证
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}else{
				RpcResponseDTO<Boolean> checkAcc = userRpcService.checkAcc(countrycode, oldacc);
				if(checkAcc.getErrorCode() == null)
					SpringMVCHelper.renderJson(response, Response.SUCCESS);
				else
					SpringMVCHelper.renderJson(response, ResponseError.embed(checkAcc.getErrorCode()));
				return;
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}*/
	}
}
