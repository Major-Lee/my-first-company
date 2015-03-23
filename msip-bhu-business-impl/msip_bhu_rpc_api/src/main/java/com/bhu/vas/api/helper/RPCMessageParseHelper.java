package com.bhu.vas.api.helper;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.ret.LocationDTO;
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
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
	}
	
	public static <T> T generateDTOFromMessage(Document doc, Class<T> clazz){
		if(doc == null){
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		}
		
		try{
			return parserMessageByDom4j(doc, clazz);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
	}
	
	public static <T> T parserMessageByXStream(String message, Class<T> clazz) throws Exception{
		return XStreamHelper.fromXML(message, clazz);
	}
	
	public static <T> T parserMessageByDom4j(String message, Class<T> clazz) throws Exception{
		return parserMessageByDom4j(parserDocumentByDom4j(message), clazz);
	}
	
	public static <T> T parserMessageByDom4j(Document doc, Class<T> clazz) throws Exception{
		Element item_element = (Element)doc.selectSingleNode("//ITEM");
		return Dom4jHelper.fromElement(item_element, clazz);
	}
	
	public static Document parserMessage(String message) {
		try{
			return parserDocumentByDom4j(message);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
	}
	
	public static Document parserDocumentByDom4j(String message) throws DocumentException{
		return Dom4jHelper.parseText(message);
	}
	
	
	public static final String Match_Lat_Word = "latitude:";
	public static final String Match_Lon_Word = "longitude:";
	/**
	 * 解析获取wifi设备地理坐标的xml解析
	 * <return>
        	<ITEM cmd="sysdebug" status="done" >
                <text>
                        <SUB text="OK" />
                        <SUB text="latitude:40.017286" />
                        <SUB text="longitude:116.345614" />
                        <SUB text="address-line:学清路" />
                        <SUB text="city:Beijing" />
                        <SUB text="state:Beijing" />
                        <SUB text="country:China" />
                </text>
        	</ITEM>
		</return>
	 * @param doc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static LocationDTO generateQueryDeviceLocationS2(Document doc){
		List<Object> nodes = doc.selectNodes("//SUB");
		if(nodes == null || nodes.isEmpty()){
			return null;
		}

		LocationDTO dto = new LocationDTO();
		Attribute attr = null;
		for(Object node : nodes){
			attr = ((Element)node).attribute("text");
			if(attr.getValue().startsWith(Match_Lat_Word)){
				dto.setLat(attr.getValue().substring(Match_Lat_Word.length()));
			}else if(attr.getValue().startsWith(Match_Lon_Word)){
				dto.setLon(attr.getValue().substring(Match_Lon_Word.length()));
			}
		}
		return dto;
	}
	
	public static void main(String[] args) {
		String message = "<return><ITEM cmd=\"sysdebug\" status=\"done\" ><text><SUB text=\"OK\" /><SUB text=\"latitude:40.017286\" /><SUB text=\"longitude:116.345614\" /><SUB text=\"address-line:学清路\" /><SUB text=\"city:Beijing\" /><SUB text=\"state:Beijing\" /><SUB text=\"country:China\" /></text></ITEM></return>";
		Document doc = parserMessage(message);
		LocationDTO dto = generateQueryDeviceLocationS2(doc);
		System.out.println(dto.getLat()+","+dto.getLon());
	}
}
