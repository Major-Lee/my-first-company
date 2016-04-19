package com.bhu.vas.business.ds.user.facade;

import java.util.List;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class UserValidateServiceHelper {
	public static User validateUser(int uid,UserService userService){
		if(uid <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		User user = userService.getById(uid);
		if(user == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		return user;
	}
	
	/**
	 * console user 直接可以访问
	 * @param uid
	 * @param dmacs
	 * @param userDeviceService
	 */
	public static void validateUserDevices(int uid,List<String> dmacs,UserDeviceService userDeviceService){
		if(!BusinessRuntimeConfiguration.isConsoleUser(uid)){
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnIn("mac", dmacs);
			int count = userDeviceService.countByModelCriteria(mc);
			if(dmacs.size() != count){
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_NOT_YOURBINDED,new String[]{dmacs.toString()});
			}
		}
	}
	
	/**
	 * console user 直接可以访问
	 * @param uid
	 * @param dmac
	 * @param userDeviceService
	 * @return
	 */
	public static UserDevice validateUserDevice(int uid,String dmac,UserDeviceService userDeviceService){
		if(uid <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		UserDevice userdevice_entity = null;
		if(!BusinessRuntimeConfiguration.isConsoleUser(uid)){
			//验证用户是否管理设备
			userdevice_entity = userDeviceService.getById(new UserDevicePK(dmac, uid));
			if(userdevice_entity == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_NOT_YOURBINDED,new String[]{dmac});
			}
		}
		return userdevice_entity;
	}
	
	//public abstract UserService getUserService();
}
