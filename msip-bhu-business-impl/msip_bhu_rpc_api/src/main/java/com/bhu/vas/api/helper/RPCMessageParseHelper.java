package com.bhu.vas.api.helper;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.XStreamHelper;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class RPCMessageParseHelper {
	
	public static <T> T generateDTOFromMessage(String message, Class<T> clazz){
		if(StringUtils.isEmpty(message)){
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY.code());
		}
		
		try{
			return XStreamHelper.fromXML(message, clazz);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL.code());
		}
	}
}
