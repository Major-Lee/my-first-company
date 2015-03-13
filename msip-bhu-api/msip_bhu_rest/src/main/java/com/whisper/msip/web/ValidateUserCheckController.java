package com.whisper.msip.web;

import com.smartwork.msip.jdo.ResponseErrorCode;
import com.whisper.api.user.model.User;
import com.whisper.msip.cores.web.mvc.spring.BaseController;
import com.whisper.msip.exception.BusinessException;

public abstract class ValidateUserCheckController extends BaseController{
	public static void validateUserNotNull(User user){
		if(user == null)
			throw new BusinessException(ResponseErrorCode.USER_DATA_NOT_EXIST);
    }
	public static void validateNotNull(Object obj){
		if(obj == null)
			throw new BusinessException(ResponseErrorCode.COMMON_DATA_NOTEXIST);
    }

}
