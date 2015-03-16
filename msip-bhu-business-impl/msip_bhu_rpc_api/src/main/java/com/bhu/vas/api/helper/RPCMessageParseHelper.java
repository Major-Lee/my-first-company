package com.bhu.vas.api.helper;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.XStreamHelper;
import com.smartwork.msip.cores.helper.dom4j.Dom4jHelper;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class RPCMessageParseHelper {
	
	public static <T> T generateDTOFromMessage(String message, Class<T> clazz){
		if(StringUtils.isEmpty(message)){
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		}
		
		try{
			return parserMessageByDom4j(message, clazz);
		}catch(Exception ex){
			//ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
	}
	
	public static <T> T parserMessageByXStream(String message, Class<T> clazz) throws Exception{
		return XStreamHelper.fromXML(message, clazz);
	}
	
	public static <T> T parserMessageByDom4j(String message, Class<T> clazz) throws Exception{
		Document doc = Dom4jHelper.parseText(message);
		Element item_element = (Element)doc.selectSingleNode("//ITEM");
		return Dom4jHelper.fromElement(item_element, clazz);
	}
}
