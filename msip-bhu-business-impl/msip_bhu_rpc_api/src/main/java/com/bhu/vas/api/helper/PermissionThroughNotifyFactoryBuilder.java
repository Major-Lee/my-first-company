package com.bhu.vas.api.helper;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.commdity.internal.portal.PermissionThroughNotifyDTO;
import com.smartwork.msip.cores.helper.JsonHelper;

public class PermissionThroughNotifyFactoryBuilder {
	
	public static PermissionThroughNotifyType determineActionType(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	String prefix = messagejsonHasPrefix.substring(0,3);
    	PermissionThroughNotifyType type = PermissionThroughNotifyType.fromPrefix(prefix);
    	return type;
	}
	public static String determineActionMessage(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	return messagejsonHasPrefix.substring(3);
	}
	public static <T extends PermissionThroughNotifyDTO> T fromJson(String messagejson,Class<T> classz){
		if(StringUtils.isEmpty(messagejson)) return null;
		return JsonHelper.getDTO(messagejson, classz);
	}
	public static String toJsonHasPrefix(PermissionThroughNotifyDTO message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.getPermissionNotifyType()).append(toJson(message));
		return sb.toString();
	}
	public static String toJson(PermissionThroughNotifyDTO message){
		return JsonHelper.getJSONString(message,false);
	}
}
