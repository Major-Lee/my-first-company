package com.bhu.vas.msip.web;

import com.bhu.vas.api.user.model.User;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.exception.BusinessException;
import com.smartwork.msip.jdo.ResponseErrorCode;

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
