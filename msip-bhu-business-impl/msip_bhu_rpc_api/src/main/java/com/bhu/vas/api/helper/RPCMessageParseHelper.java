package com.bhu.vas.api.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.ret.LocationDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceFlowDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingAclDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingInterfaceDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRateControlDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingUserDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.StringHelper;
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
	
	/**
	 * 解析设备配置查询xml
	 * 因为只获取其中少部分属性,所以没有采用反射机制注入
	 * @param message
	 * @return
	 */
	public static WifiDeviceSettingDTO generateDTOFromQueryDeviceSetting(String message){
		Document doc = parserMessage(message);
		WifiDeviceSettingDTO dto = new WifiDeviceSettingDTO();
		try{
			//解析 radio
			Element radio_item = Dom4jHelper.select(doc, "dev/wifi/radio/ITEM");
			if(radio_item != null){
				dto.setPower(radio_item.attributeValue("power"));
			}
			//解析 wan
			Element wan_item = Dom4jHelper.select(doc, "dev/mod/basic/wan/ITEM");
			if(wan_item != null){
				dto.setMode(wan_item.attributeValue("mode"));
			}
			//解析 vaps
			List<Element> vap_items = Dom4jHelper.selectElements(doc, "dev/wifi/vap/ITEM");
			if(vap_items != null && !vap_items.isEmpty()){
				List<WifiDeviceSettingVapDTO> vap_dtos = new ArrayList<WifiDeviceSettingVapDTO>();
				for(Element vap_item : vap_items){
					WifiDeviceSettingVapDTO vap_dto = new WifiDeviceSettingVapDTO();
					vap_dto.setName(vap_item.attributeValue("name"));
					vap_dto.setSsid(vap_item.attributeValue("ssid"));
					vap_dto.setAuth(vap_item.attributeValue("auth"));
					vap_dto.setEnable(vap_item.attributeValue("enable"));
					vap_dto.setAcl_name(vap_item.attributeValue("acl_name"));
					vap_dto.setAcl_type(vap_item.attributeValue("acl_type"));
					vap_dtos.add(vap_dto);
				}
				dto.setVaps(vap_dtos);
			}
			//解析黑白名单
			List<Element> acl_items = Dom4jHelper.selectElements(doc, "dev/wifi/acllist/ITEM");
			if(acl_items != null && !acl_items.isEmpty()){
				List<WifiDeviceSettingAclDTO> acl_dtos = new ArrayList<WifiDeviceSettingAclDTO>();
				for(Element acl_item : acl_items){
					WifiDeviceSettingAclDTO acl_dto = new WifiDeviceSettingAclDTO();
					acl_dto.setName(acl_item.attributeValue("name"));
					String acl_macs = acl_item.attributeValue("macs");
					if(!StringUtils.isEmpty(acl_macs)){
						String[] acl_mac_array = acl_macs.split(StringHelper.COMMA_STRING_GAP);
						acl_dto.setMacs(ArrayHelper.toList(acl_mac_array));
					}
					acl_dtos.add(acl_dto);
				}
				dto.setAcls(acl_dtos);
			}
			//解析接口速率控制
			List<Element> interface_items = Dom4jHelper.selectElements(doc, "dev/net/interface/ITEM");
			if(interface_items != null && !interface_items.isEmpty()){
				List<WifiDeviceSettingInterfaceDTO> interface_dtos = new ArrayList<WifiDeviceSettingInterfaceDTO>();
				for(Element interface_item : interface_items){
					WifiDeviceSettingInterfaceDTO interface_dto = new WifiDeviceSettingInterfaceDTO();
					interface_dto.setName(interface_item.attributeValue("name"));
					interface_dto.setEnable(interface_item.attributeValue("enable"));
					interface_dto.setIf_tx_rate(interface_item.attributeValue("if_tx_rate"));
					interface_dto.setIf_rx_rate(interface_item.attributeValue("if_rx_rate"));
					interface_dto.setUsers_tx_rate(interface_item.attributeValue("users_tx_rate"));
					interface_dto.setUsers_rx_rate(interface_item.attributeValue("users_rx_rate"));
					interface_dtos.add(interface_dto);
				}
				dto.setInterfaces(interface_dtos);
			}
			//解析终端速率控制
			List<Element> ratecontrol_items = Dom4jHelper.selectElements(doc, "dev/net/rate_control/ITEM");
			if(ratecontrol_items != null && !ratecontrol_items.isEmpty()){
				List<WifiDeviceSettingRateControlDTO> ratecontrol_dtos = new ArrayList<WifiDeviceSettingRateControlDTO>();
				for(Element ratecontrol_item : ratecontrol_items){
					WifiDeviceSettingRateControlDTO ratecontrol_dto = new WifiDeviceSettingRateControlDTO();
					ratecontrol_dto.setMac(ratecontrol_item.attributeValue("mac"));
					ratecontrol_dto.setTx(ratecontrol_item.attributeValue("tx"));
					ratecontrol_dto.setRx(ratecontrol_item.attributeValue("rx"));
					ratecontrol_dtos.add(ratecontrol_dto);
				}
				dto.setRatecontrols(ratecontrol_dtos);
			}
			//解析管理员用户列表
			List<Element> user_items = Dom4jHelper.selectElements(doc, "dev/sys/users/ITEM");
			if(user_items != null && !user_items.isEmpty()){
				List<WifiDeviceSettingUserDTO> user_dtos = new ArrayList<WifiDeviceSettingUserDTO>();
				for(Element user_item : user_items){
					WifiDeviceSettingUserDTO user_dto = new WifiDeviceSettingUserDTO();
					user_dto.setName(user_item.attributeValue("name"));
					user_dto.setAuth(user_item.attributeValue("auth"));
					user_dto.setPassword_enc(user_item.attributeValue("password_enc"));
					user_dtos.add(user_dto);
				}
				dto.setUsers(user_dtos);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
		return dto;
	}
	/**
	 * 解析VAP下的终端列表
	 * <stations>
          <SUB mac="88:32:9b:32:41:10" aid="1" phy_tx_rate="72M" phy_rx_rate="25.5M" current_phy_tx_rate="72M" 
          current_phy_rx_rate="6M" data_tx_rate="16" data_rx_rate="288" phy_tx_errors="0" phy_rx_errors="0" 
          phy_tx_drops="0" phy_rx_drops="749" phy_rate="72M" tx_power="3dBm" rx_chain_num="2" rssi="-31dBm" 
          antenna_rssi0="-36dBm" antenna_rssi1="-34dBm" antenna_rssi2="-95dBm" snr="64dB" antenna_snr0="59dB" 
          antenna_snr1="61dB" antenna_snr2="0dB" idle="0" state="27" uptime="1361" wds_list="" rx_pkts="1394" 
          rx_bytes="118503" tx_pkts="277" tx_bytes="45745" rx_unicast="0" tx_assoc="1" />
       </stations>
	 * @param doc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<WifiDeviceTerminalDTO> generateDTOFromQueryDeviceTerminals(Document doc){
		try{
			List<Element> elements = doc.selectNodes("//SUB");
			if(elements != null && !elements.isEmpty()){
				List<WifiDeviceTerminalDTO> dtos = new ArrayList<WifiDeviceTerminalDTO>();
				for(Element element : elements){
					WifiDeviceTerminalDTO dto = new WifiDeviceTerminalDTO();
					dto.setMac(element.attributeValue("mac"));
					dto.setData_tx_rate(element.attributeValue("data_tx_rate"));
					dto.setData_rx_rate(element.attributeValue("data_rx_rate"));
					dtos.add(dto);
				}
				return dtos;
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
		return Collections.emptyList();
	}
	
	public static void main(String[] args) throws Exception{
//		String message = "<return><ITEM cmd=\"sysdebug\" status=\"done\" ><text><SUB text=\"OK\" /><SUB text=\"latitude:40.017286\" /><SUB text=\"longitude:116.345614\" /><SUB text=\"address-line:学清路\" /><SUB text=\"city:Beijing\" /><SUB text=\"state:Beijing\" /><SUB text=\"country:China\" /></text></ITEM></return>";
//		Document doc = parserMessage(message);
//
//		LocationDTO locationDto = generateDTOFromQueryDeviceLocationS2(doc);
//		System.out.println(locationDto.getLat()+","+locationDto.getLon());
//		
//		message = "<return><ITEM index=\"1\" cmd=\"if_stat\" status=\"done\" ><if_stat_sub><SUB name=\"eth0\" rx_pkts=\"0\" rx_err_pkts=\"0\" rx_drop_pkts=\"0\" rx_over_pkts=\"0\" rx_err_frames=\"0\" rx_multicast_pkts=\"0\" rx_bytes=\"0\" tx_pkts=\"0\" tx_err_pkts=\"0\" tx_drop_pkts=\"0\" tx_over_pkts=\"0\" tx_err_carrier=\"0\" tx_bytes=\"0\" /><SUB name=\"eth1\" rx_pkts=\"934\" rx_err_pkts=\"0\" rx_drop_pkts=\"0\" rx_over_pkts=\"0\" rx_err_frames=\"0\" rx_multicast_pkts=\"555\" rx_bytes=\"228108\" tx_pkts=\"431\" tx_err_pkts=\"0\" tx_drop_pkts=\"0\" tx_over_pkts=\"0\" tx_err_carrier=\"0\" tx_bytes=\"44778\" /></if_stat_sub></ITEM></return>";
//		doc = parserMessage(message);
//		QuerySerialReturnDTO serialDto = generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
//		System.out.println(serialDto.getStatus());
//		List<WifiDeviceFlowDTO> dtos = generateDTOFromQueryDeviceFlow(doc);
//		for(WifiDeviceFlowDTO dto : dtos){
//			System.out.println(dto.getName() + "-" + dto.getRx_bytes());
//		}
		BufferedReader in = new BufferedReader(new FileReader(new File("/Users/tangzichao/work/document/device_setting.xml")));
        String str;
        StringBuffer content = new StringBuffer();
        while ((str = in.readLine()) != null) 
        {
        	content.append(str);
        }
        in.close();
        
        System.out.println(content.toString());
		WifiDeviceSettingDTO dto = generateDTOFromQueryDeviceSetting(content.toString());
		System.out.println(dto.getPower());
	}
}
