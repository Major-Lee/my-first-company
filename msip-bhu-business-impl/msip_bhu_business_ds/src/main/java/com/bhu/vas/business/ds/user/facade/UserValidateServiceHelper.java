package com.bhu.vas.business.ds.user.facade;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.user.service.UserService;
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
	
	//public abstract UserService getUserService();
}
