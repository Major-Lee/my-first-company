package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.ret.LocationDTO;
import com.bhu.vas.api.dto.ret.QuerySerialReturnDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceFlowDTO;
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
	
	public static <T> List<T> generateDTOListFromMessage(String message, Class<T> clazz){
		if(StringUtils.isEmpty(message)){
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		}
		
		try{
			return parserMessageListByDom4j(message, clazz);
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
	
	public static <T> List<T> parserMessageListByDom4j(String message, Class<T> clazz) throws Exception{
		return parserMessageListByDom4j(parserDocumentByDom4j(message), clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> parserMessageListByDom4j(Document doc, Class<T> clazz) throws Exception{
		List<T> list = new ArrayList<T>();
		List<Element> item_elements = (List<Element>)doc.selectNodes("//ITEM");
		for(Element item_element : item_elements){
			list.add(Dom4jHelper.fromElement(item_element, clazz));
		}
		return list;
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
	 * 解析获取wifi设备地理坐标的xml
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
	public static LocationDTO generateDTOFromQueryDeviceLocationS2(Document doc){
		LocationDTO dto = null;
		try{
			List<Element> elements = doc.selectNodes("//SUB");
			if(elements == null || elements.isEmpty()){
				return null;
			}
	
			dto = new LocationDTO();
			Attribute attr = null;
			for(Element element : elements){
				attr = element.attribute("text");
				if(attr.getValue().startsWith(Match_Lat_Word)){
					dto.setLat(attr.getValue().substring(Match_Lat_Word.length()));
				}else if(attr.getValue().startsWith(Match_Lon_Word)){
					dto.setLon(attr.getValue().substring(Match_Lon_Word.length()));
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
		return dto;
	}
	
	/**
	 * 解析获取wifi设备流量的xml
		<return>
        	<ITEM index="1" cmd="if_stat" status="done" >
                <if_stat_sub>
                        <SUB name="eth0" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" 
                        rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="0" 
                        tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="0" />
                        <SUB name="eth1" rx_pkts="934" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="555" rx_bytes="228108" tx_pkts="431" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="44778" />
                        <SUB name="wlan0" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="447" tx_err_pkts="0" tx_drop_pkts="16" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="93849" />
                        <SUB name="wlan2" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="0" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="0" />
                        <SUB name="wlan3" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="0" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="0" />
                        <SUB name="wlan1" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="0" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="0" />
                        <SUB name="br-lan" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="8" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="576" />
                </if_stat_sub>
        	</ITEM>
		</return>
	 * @param doc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<WifiDeviceFlowDTO> generateDTOFromQueryDeviceFlow(Document doc){
		List<WifiDeviceFlowDTO> dtos = null;
		try{
			List<Element> elements = doc.selectNodes("//SUB");
			if(elements == null || elements.isEmpty()){
				return null;
			}
	
			dtos = new ArrayList<WifiDeviceFlowDTO>();
			for(Element element : elements){
				dtos.add(Dom4jHelper.fromElement(element, WifiDeviceFlowDTO.class));
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
		return dtos;
	}
	
	public static void main(String[] args) {
		String message = "<return><ITEM cmd=\"sysdebug\" status=\"done\" ><text><SUB text=\"OK\" /><SUB text=\"latitude:40.017286\" /><SUB text=\"longitude:116.345614\" /><SUB text=\"address-line:学清路\" /><SUB text=\"city:Beijing\" /><SUB text=\"state:Beijing\" /><SUB text=\"country:China\" /></text></ITEM></return>";
		Document doc = parserMessage(message);

		LocationDTO locationDto = generateDTOFromQueryDeviceLocationS2(doc);
		System.out.println(locationDto.getLat()+","+locationDto.getLon());
		
		message = "<return><ITEM index=\"1\" cmd=\"if_stat\" status=\"done\" ><if_stat_sub><SUB name=\"eth0\" rx_pkts=\"0\" rx_err_pkts=\"0\" rx_drop_pkts=\"0\" rx_over_pkts=\"0\" rx_err_frames=\"0\" rx_multicast_pkts=\"0\" rx_bytes=\"0\" tx_pkts=\"0\" tx_err_pkts=\"0\" tx_drop_pkts=\"0\" tx_over_pkts=\"0\" tx_err_carrier=\"0\" tx_bytes=\"0\" /><SUB name=\"eth1\" rx_pkts=\"934\" rx_err_pkts=\"0\" rx_drop_pkts=\"0\" rx_over_pkts=\"0\" rx_err_frames=\"0\" rx_multicast_pkts=\"555\" rx_bytes=\"228108\" tx_pkts=\"431\" tx_err_pkts=\"0\" tx_drop_pkts=\"0\" tx_over_pkts=\"0\" tx_err_carrier=\"0\" tx_bytes=\"44778\" /></if_stat_sub></ITEM></return>";
		doc = parserMessage(message);
		QuerySerialReturnDTO serialDto = generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
		System.out.println(serialDto.getStatus());
		List<WifiDeviceFlowDTO> dtos = generateDTOFromQueryDeviceFlow(doc);
		for(WifiDeviceFlowDTO dto : dtos){
			System.out.println(dto.getName() + "-" + dto.getRx_bytes());
		}
	}
}
