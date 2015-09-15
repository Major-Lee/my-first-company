package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.rpc.user.model.User;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class UserTypeValidateService {
	public static boolean validConsoleUser(int uid){
		if(uid>0){
			if(RuntimeConfiguration.isConsoleUser(uid)){//管理员账户直接通过验证
				return true;
			}else{
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_WASNOT_CONSOLE);
			}
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		
	}
	
	public static boolean validConsoleOrAgentUser(User user){
		if(user != null){
			if(RuntimeConfiguration.isConsoleUser(user.getId()) || User.Agent_User == user.getUtype()){//管理员账户或代理商用户直接通过验证
				return true;
			}else{
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_WASNOT_CONSOLEORAGENT);
			}
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		
	}
	
	public static boolean validAgentUser(User user){
		if(user != null){
			if(User.Agent_User == user.getUtype()){//管理员账户或代理商用户直接通过验证
				return true;
			}else{
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_WASNOT_AGENT);
			}
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
	}
}
