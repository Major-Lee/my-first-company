package com.whisper.web.device;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.user.model.DeviceEnum;
import com.whisper.api.user.model.User;
import com.whisper.api.user.model.UserDeviceState;
import com.whisper.business.asyn.web.builder.DeliverMessageType;
import com.whisper.business.asyn.web.service.DeliverMessageService;
import com.whisper.business.ucenter.service.UserDeviceService;
import com.whisper.business.ucenter.service.UserDeviceStateService;
import com.whisper.business.ucenter.service.UserService;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.ValidateUserCheckController;


@Controller
@RequestMapping("/device")
public class DeviceController extends ValidateUserCheckController{

	@Resource
	private UserService userService;
	
	@Resource
	private UserDeviceStateService userDeviceStateService;
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	/**
	 * 注册用户目前登录的设备及设备token
	 * @param response
	 * @param request
	 * @param uid
	 * @param dt
	 * @param dtk
	 */
	@ResponseBody()
	@RequestMapping(value="/register",method={RequestMethod.GET,RequestMethod.POST})
	public void register(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(required = true) int uid,
			//device type
			@RequestParam(required = false, value="d",defaultValue="R") String device,
			//device token 对于ios设备 用于iospush使用 由于客户端设备获取dt需要用户授权，可能获取不到
			@RequestParam(required = false) String dt,
			//device mac 地址 mac地址也不一定能获取到
			@RequestParam(required = false) String dm,
			//client 系统版本号
			@RequestParam(required = true) String cv,
			//client production 版本号
			@RequestParam(required = true) String pv,
			//设备型号unit type
			@RequestParam(required = true) String ut,
			//push type
			@RequestParam(required = false) String pt,
			@RequestParam(required = false,defaultValue = "") String dn,
			@RequestParam(required = false,defaultValue = "") String duuid
			){
		DeviceEnum de = DeviceEnum.getBySName(device);
		if(de == null || !DeviceEnum.isHandsetDevice(de)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.DEVICE_TYPE_NOT_SUPPORTED));
			return;
		}
		
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			userDeviceService.deviceRegister(uid, dt, device, pt);
			deliverMessageService.sendUserDeviceRegisterActionMessage(DeliverMessageType.AC.getPrefix(), uid, dt, device, cv, pv, ut, dn, dm, duuid, pt);
			//userDeviceStateService.userNewDeviceRegisterOrReplace(uid, de, dn, StringHelper.formatMacAddress(dm), dt, duuid,cv,pv,ut);
			//DevicesTokenHashService.getInstance().userDeviceTokenRegister(uid, de,dtk);
			//DevicesTokenHashService.getInstance().userDeviceLastRegister(uid, de);
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 注销用户目前登录的设备及设备token
	 * 如果用户在iphone或者Android上登出后，则修改其标记DeviceLastRegister为PC
	 * 因为手机客户端登录的用户需要发push通知
	 * @param response
	 * @param request
	 * @param uid
	 */
	@ResponseBody()
	@RequestMapping(value="/destory",method={RequestMethod.GET,RequestMethod.POST})
	public void destory(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(required = true) int uid,
			//device type
			@RequestParam(required = false, value="d",defaultValue="P") String device,
			@RequestParam(required = false) String dt){
		
		DeviceEnum de = DeviceEnum.getBySName(device);
		if(de == null || !DeviceEnum.isHandsetDevice(de)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.DEVICE_TYPE_NOT_SUPPORTED));
			return;
		}
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			userDeviceService.destoryRegister(uid, dt);
			deliverMessageService.sendUserDeviceDestoryActionMessage(DeliverMessageType.AC.getPrefix(), uid, dt, device);
			//userDeviceStateService.userDeviceSignedOff(uid, dt, de);
			//DevicesTokenHashService.getInstance().userDeviceTokenRemove(uid, de);
			//DevicesTokenHashService.getInstance().userDeviceLastRegister(uid, de);
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 获取用户目前登录的设备
	 * @param response
	 * @param request
	 * @param uid
	 */
	@ResponseBody()
	@RequestMapping(value="/current",method={RequestMethod.GET,RequestMethod.POST})
	public void current(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(required = true) int uid){
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(user.getLastlogindevice()));
	}
	
	/**
	 * 获取用户绑定的设备
	 * @param response
	 * @param request
	 * @param uid
	 */
	@ResponseBody()
	@RequestMapping(value="/userdevices",method={RequestMethod.GET,RequestMethod.POST})
	public void userdevices(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(required = true) int uid){
		
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			Set<String> userDevices = null;
			UserDeviceState userDeviceState = userDeviceStateService.getById(uid);
			if(userDeviceState == null){
				userDevices = new HashSet<String>();
			}else{
				userDevices = userDeviceState.keySet();
			}
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(userDevices));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
