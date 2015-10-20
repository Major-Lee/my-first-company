package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.model.User;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class UserTypeValidateService {
	
	public static boolean validUserType(User user,String ut){
		if(user == null){//存在不干净的数据，需要清理数据
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		UserType bySName = UserType.getBySName(ut);
		if(user.getUtype() != bySName.getIndex()){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_NOTMATCHED,new String[]{ut});
		}
		return true;
	}
	
	public static boolean validNotNormalUser(User user){
		if(user == null){//存在不干净的数据，需要清理数据
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		
		if(user.getUtype() == UserType.Normal.getIndex()){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_WASNOT_NORMAL);
		}
		return true;
	}
	
	/*public static boolean validConsoleUser(int uid){
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
	}*/
}
