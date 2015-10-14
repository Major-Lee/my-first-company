package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.ScoreDTO;
import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.api.dto.redis.element.DailyUsedStatisticsDTO;
import com.bhu.vas.api.dto.redis.element.HourUsedStatisticsDTO;
import com.bhu.vas.api.dto.ret.LocationDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceFlowDTO;
import com.bhu.vas.api.dto.ret.WifiDevicePeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceRateDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceRxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceVapReturnDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingAclDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingInterfaceDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingLinkModeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingMMDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRadioDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRateControlDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingUserDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapAdDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.dto.vap.Http404ModuleDTO;
import com.bhu.vas.api.dto.vap.HttpRedirectModuleDTO;
import com.bhu.vas.api.dto.vap.ModuleDTO;
import com.bhu.vas.api.dto.vap.RegisterDTO;
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
	
	public static WifiDeviceVapReturnDTO generateVapDTOFromMessage(Document doc){
		if(doc == null){
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		}
		try{
			return parserVapDTOByDom4j(doc);
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
	
	public static WifiDeviceVapReturnDTO parserVapDTOByDom4j(Document doc) throws Exception{
		WifiDeviceVapReturnDTO result = new WifiDeviceVapReturnDTO();
		Element item_element = (Element)doc.selectSingleNode("//login/ITEM");
		if(item_element != null)
			result.setRegister(Dom4jHelper.fromElement(item_element, RegisterDTO.class));
		Element http404_item_element = (Element)doc.selectSingleNode("//bhu_module/http404/ITEM");
		if(http404_item_element != null){
			if(result.getModules() == null) result.setModules(new ArrayList<ModuleDTO>());
			result.getModules().add(Dom4jHelper.fromElement(http404_item_element, Http404ModuleDTO.class));
		}
		
		Element httpredirect_item_element = (Element)doc.selectSingleNode("//bhu_module/redirect/ITEM");
		if(httpredirect_item_element != null){
			if(result.getModules() == null) result.setModules(new ArrayList<ModuleDTO>());
			result.getModules().add(Dom4jHelper.fromElement(httpredirect_item_element, HttpRedirectModuleDTO.class));
		}
		
		/*List<Element> item_elements = (List<Element>)doc.selectNodes("//bhu_module/module");
		if(item_elements != null && !item_elements.isEmpty()){
			result.setModules(new ArrayList<ModuleDTO>());
			for(Element item:item_elements){
				if("http404".equals(item.attributeValue("type"))){
					result.getModules().add(Dom4jHelper.fromElement(item, Http404ModuleDTO.class));
				}else if("redirect".equals(item.attributeValue("type"))){
					result.getModules().add(Dom4jHelper.fromElement(item, HttpRedirectModuleDTO.class));
				}
			}
		}*/
		return result;
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
	 * 设备测速的分段数据解析
	 * <ITEM serial=“0200000002” cmd=“netspeed_test” statue=“doing”>
	 *    <download>
               <SUB time_cost=“” rx_bytes=“1111” last="true"/>
          </download>
          <upload>
               <SUB time_cost=“” tx_bytes=“1111” />
          </upload>
     </ITEM>
	 * @param doc
	 * @return
	 */
	public static WifiDevicePeakSectionDTO generateDTOFromQuerySpeedTest(Document doc){
		WifiDevicePeakSectionDTO dto = null;
		try{
			Element download_sub_element = Dom4jHelper.select(doc, "//download/SUB");
			if(download_sub_element != null){
				WifiDeviceRxPeakSectionDTO rx_dto = Dom4jHelper.fromElement(download_sub_element, 
						WifiDeviceRxPeakSectionDTO.class);
				String last = download_sub_element.attributeValue("last");
				if(!StringUtils.isEmpty(last)){
					rx_dto.setLast(Boolean.parseBoolean(last));
				}
				if(dto == null){
					dto = new WifiDevicePeakSectionDTO();
				}
				dto.setRx_dto(rx_dto);
			}
	
			Element upload_sub_element = Dom4jHelper.select(doc, "//upload/SUB");
			if(upload_sub_element != null){
				WifiDeviceTxPeakSectionDTO tx_dto = Dom4jHelper.fromElement(upload_sub_element, 
						WifiDeviceTxPeakSectionDTO.class);
				String last = upload_sub_element.attributeValue("last");
				if(!StringUtils.isEmpty(last)){
					tx_dto.setLast(Boolean.parseBoolean(last));
				}
				if(dto == null){
					dto = new WifiDevicePeakSectionDTO();
				}
				dto.setTx_dto(tx_dto);
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
	
	
	private static final String Node_Today = "//today";
	private static final String Node_Yesterday = "//yesterday";
	private static final String Node_Time_Attr = "time";
	private static final String Node_Time_ALL_Value = "all";
	/**
	 * 解析获取wifi设备使用情况
		<return>         
		   <ITEM cmd="data_stats" status="done">
		     <today>
		        <SUB time="0" tx_bytes="1234" rx_bytes="222" sta="4" />
		        <SUB time="1" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="2" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="3" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="4" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="5" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="22" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="all" tx_bytes="62816" rx_bytes="5654" sta="100" sta_max_time="458" sta_max_time_num ="5"/>
		     </today>
		     <yesterday>
		        <SUB time="0" tx_bytes="1234" rx_bytes="222" sta="4" />
		        <SUB time="1" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="2" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="3" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="4" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="5" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="22" tx_bytes="6816" rx_bytes="5468" sta="0"/>
		        <SUB time="23" tx_bytes="6816" rx_bytes="5468" sta="0"/> 
		        <SUB time="all" tx_bytes="62816" rx_bytes="5654" sta="100" sta_max_time ="458" sta_max_time_num ="2"/>
		     </yesterday>
		  </ITEM>
		</return> 
	 * @param doc
	 * @return
	 */
	@SuppressWarnings({"rawtypes" })
	public static DeviceUsedStatisticsDTO generateDTOFromQueryDeviceUsedStatus(Document doc){
		DeviceUsedStatisticsDTO dto = null;
		Element today = null;
		Element yesterday = null;
		try{
			dto = new DeviceUsedStatisticsDTO();
			dto.setToday_detail(new ArrayList<HourUsedStatisticsDTO>());
			dto.setYesterday_detail(new ArrayList<HourUsedStatisticsDTO>());
			today = (Element)doc.selectSingleNode(Node_Today);
			Iterator iter_today = today.elements().iterator();
			while(iter_today.hasNext()){
				Element next =(Element) iter_today.next();
				String time = next.attribute(Node_Time_Attr).getStringValue();
				if(Node_Time_ALL_Value.equals(time)){
					dto.setToday(Dom4jHelper.fromElement(next, DailyUsedStatisticsDTO.class));
					if(dto.getToday() != null){
						ScoreDTO analyse = ScoreHelper.analyse(convert(dto.getToday().getRx_bytes()), Integer.parseInt(dto.getToday().getSta_max_time_num()));
						dto.getToday().setKo(analyse.getHint());
						dto.getToday().setScore(analyse.getScore());
					}
				}else{
					dto.getToday_detail().add(Dom4jHelper.fromElement(next, HourUsedStatisticsDTO.class));
				}
			}
			yesterday = (Element)doc.selectSingleNode(Node_Yesterday);
			Iterator iter_yesterday = yesterday.elements().iterator();
			while(iter_yesterday.hasNext()){
				Element next =(Element) iter_yesterday.next();
				String time = next.attribute(Node_Time_Attr).getStringValue();
				if(Node_Time_ALL_Value.equals(time)){
					dto.setYesterday(Dom4jHelper.fromElement(next, DailyUsedStatisticsDTO.class));
					if(dto.getYesterday() != null){
						ScoreDTO analyse = ScoreHelper.analyse(convert(dto.getYesterday().getRx_bytes()), Integer.parseInt(dto.getYesterday().getSta_max_time_num()));
						dto.getYesterday().setKo(analyse.getHint());
						dto.getYesterday().setScore(analyse.getScore());
					}
				}else{
					dto.getYesterday_detail().add(Dom4jHelper.fromElement(next, HourUsedStatisticsDTO.class));
				}
			}
			dto.analyseMaxFlow();
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}finally{
		}
		return dto;
	}
	
	private static long convert(String rx_bytes){
		long ret = 0l;
		try{
			ret = Long.parseLong(rx_bytes);
		}catch(NumberFormatException ex){
			ret = Long.MAX_VALUE;
		}
		return ret;
	}
	/***
	 * 获取实时速率的解析xml
	 * @param doc
	 * @return
	 */
	public static WifiDeviceRateDTO generateDTOFromQueryDeviceRate(Document doc){
		WifiDeviceRateDTO dto = null;
		try{
			Element element = Dom4jHelper.select(doc, "//SUB");
			if(element != null){
				return Dom4jHelper.fromElement(element, WifiDeviceRateDTO.class);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		}
		return dto;
	}
	
	/**
	 * 解析设备配置查询xml
	 * 因为只获取其中少部分属性,所以没有采用反射机制注入
	 * @param message
	 * @return
	 */
	public static WifiDeviceSettingDTO generateDTOFromQueryDeviceSetting(String message){
		return generateDTOFromQueryDeviceSetting(message, new WifiDeviceSettingDTO());
	}
	/**
	 * 解析设备配置查询xml
	 * 因为只获取其中少部分属性,所以没有采用反射机制注入
	 * @param message
	 * @param dto 配置dto
	 * @return
	 */
	public static WifiDeviceSettingDTO generateDTOFromQueryDeviceSetting(String message, WifiDeviceSettingDTO dto){
		Document doc = parserMessage(message);
		try{
			//解析 radio 多频设备会有多个
			List<Element> radio_items = Dom4jHelper.selectElements(doc, "dev/wifi/radio/ITEM");
			if(radio_items != null && !radio_items.isEmpty()){
				List<WifiDeviceSettingRadioDTO> radio_dtos = new ArrayList<WifiDeviceSettingRadioDTO>();
				for(Element radio_item : radio_items){
					WifiDeviceSettingRadioDTO radio_dto = new WifiDeviceSettingRadioDTO();
					radio_dto.setName(radio_item.attributeValue("name"));
					radio_dto.setPower(radio_item.attributeValue("power"));
					radio_dto.setReal_channel(radio_item.attributeValue("real_channel"));
					radio_dtos.add(radio_dto);
				}
				dto.setRadios(radio_dtos);
			}
//			Element radio_item = Dom4jHelper.select(doc, "dev/wifi/radio/ITEM");
//			if(radio_item != null){
//				dto.setPower(radio_item.attributeValue("power"));
//			}
			
			//解析 wan
			Element wan_item = Dom4jHelper.select(doc, "dev/mod/basic/wan/ITEM");
			if(wan_item != null){
				WifiDeviceSettingLinkModeDTO linkmodel_dto = new WifiDeviceSettingLinkModeDTO();
				linkmodel_dto.setModel(wan_item.attributeValue("mode"));
				linkmodel_dto.setGateway(wan_item.attributeValue("gateway"));
				linkmodel_dto.setDns(wan_item.attributeValue("dns"));
				linkmodel_dto.setIp(wan_item.attributeValue("ip"));
				linkmodel_dto.setNetmask(wan_item.attributeValue("netmask"));
				linkmodel_dto.setPassword_rsa(wan_item.attributeValue("password_rsa"));
				linkmodel_dto.setWan_interface(wan_item.attributeValue("wan_interface"));
				//linkmodel_dto.setReal_ipaddr(wan_item.attributeValue("real_ipaddr"));
				//linkmodel_dto.setReal_netmask(wan_item.attributeValue("real_netmask"));
				linkmodel_dto.setUsername(wan_item.attributeValue("username"));
				//dto.setMode(wan_item.attributeValue("mode"));
				dto.setMode(linkmodel_dto);
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
					vap_dto.setRadio(vap_item.attributeValue("radio"));
					vap_dto.setGuest_en(vap_item.attributeValue("guest_en"));
					vap_dto.setAuth_key_rsa(vap_item.attributeValue("auth_key_rsa"));
					vap_dto.setMode(vap_item.attributeValue("mode"));
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
					}else{
						acl_dto.setMacs(new ArrayList<String>());
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
					ratecontrol_dto.setIndex(ratecontrol_item.attributeValue("index"));
					ratecontrol_dto.setSsdel(ratecontrol_item.attributeValue("ssdel"));
					ratecontrol_dtos.add(ratecontrol_dto);
				}
				dto.setRatecontrols(ratecontrol_dtos);
			}
			//解析终端别名
			List<Element> mac_management_elements = Dom4jHelper.selectElements(doc, "dev/net/mac_management/ITEM");
			if(mac_management_elements != null && !mac_management_elements.isEmpty()){
				List<WifiDeviceSettingMMDTO> mac_management_dtos = new ArrayList<WifiDeviceSettingMMDTO>();
				for(Element mac_management_element : mac_management_elements){
					WifiDeviceSettingMMDTO mm_dto = new WifiDeviceSettingMMDTO();
					mm_dto.setName(mac_management_element.attributeValue("name"));
					mm_dto.setMac(mac_management_element.attributeValue("mac"));
					mm_dto.setSsdel(mac_management_element.attributeValue("ssdel"));
					mac_management_dtos.add(mm_dto);
				}
				dto.setMms(mac_management_dtos);
			}
			//解析广告
			Element ad_item = Dom4jHelper.select(doc, "dev/net/ad/ITEM");
			if(ad_item != null){
				WifiDeviceSettingVapAdDTO ad_dto = new WifiDeviceSettingVapAdDTO();
//				ad_dto.setId(ad_item.attributeValue("id"));
//				ad_dto.setEnable(ad_item.attributeValue("enable"));
//				ad_dto.setAd_interface(ad_item.attributeValue("interface"));
//				ad_dto.setAd_url(ad_item.attributeValue("ad_url"));
				ad_dto.setBhu_ad_url(ad_item.attributeValue("bhu_ad_url"));
				ad_dto.setBhu_enable(ad_item.attributeValue("bhu_enable"));
				ad_dto.setBhu_id(ad_item.attributeValue("bhu_id"));
				dto.setAd(ad_dto);
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
					user_dto.setPassword_rsa(user_item.attributeValue("password_rsa"));
					user_dtos.add(user_dto);
				}
				dto.setUsers(user_dtos);
			}
			//解析设备配置流水号
			Element config_element = Dom4jHelper.select(doc, "dev/sys/config/ITEM");
			if(config_element != null){
				dto.setSequence(config_element.attributeValue("sequence"));
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
					if(WifiDeviceTerminalDTO.Mode_STA.equals(element.attributeValue("mode"))){
						WifiDeviceTerminalDTO dto = new WifiDeviceTerminalDTO();
						dto.setVapname(element.attributeValue("interface"));
						dto.setMac(element.attributeValue("mac"));
						dto.setData_tx_rate(element.attributeValue("data_tx_rate"));
						dto.setData_rx_rate(element.attributeValue("data_rx_rate"));
						dto.setRx_bytes(element.attributeValue("rx_bytes"));
						dto.setTx_bytes(element.attributeValue("tx_bytes"));
						dtos.add(dto);
					}
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
//		long parseLong = convert("18446744072804816726");
//		
//		System.out.println(Long.MAX_VALUE);
//		System.out.println(new BigInteger("18446744072804816726").longValue());
//        System.out.println(new Date(1435807903055l));
//		BufferedReader in = new BufferedReader(new FileReader(new File("/BHUData/data/deviceusedstatus.xml")));
//        String str;
//        StringBuffer content = new StringBuffer();
//        while ((str = in.readLine()) != null) 
//        {
//        	content.append(str+"\n");
//        }
//        in.close();
//        
//        System.out.println(content.toString());
//        Document doc = parserMessage(content.toString());
//        DeviceUsedStatisticsDTO dto = generateDTOFromQueryDeviceUsedStatus(doc);
//        long today_rx = Long.parseLong(dto.getToday().getRx_bytes());
//        long today_rx_total = 0;
//        for(HourUsedStatisticsDTO sdto:dto.getToday_detail()){
//        	today_rx_total+= Long.parseLong(sdto.getRx_bytes());
//        }
//        System.out.println(String.format("today_rx[%s] today_rx_total[%s]",today_rx,today_rx_total));
        /*
        long yesterday_rx = Long.parseLong(dto.getYesterday().getRx_bytes());
        long yesterday_rx_total = 0;
        for(HourUsedStatisticsDTO sdto:dto.getYesterday_detail()){
        	yesterday_rx_total += Long.parseLong(sdto.getRx_bytes());
        }
        System.out.println(String.format("yesterday_rx[%s] yesterday_rx_total[%s]",yesterday_rx,yesterday_rx_total));*/
        /*System.out.println(JsonHelper.getJSONString(dto));
        
        String json = JsonHelper.getJSONString(dto);
        
        DeviceUsedStatisticsDTO dto2 = JsonHelper.getDTO(json, DeviceUsedStatisticsDTO.class);
        System.out.println(dto2);*/
        
        
		/*BufferedReader in = new BufferedReader(new FileReader(new File("/BHUData/data/wifitimer.xml")));
        String str;
        StringBuffer content = new StringBuffer();
        while ((str = in.readLine()) != null) 
        {
        	content.append(str+"\n");
        }
        in.close();
        Document doc = parserMessage(content.toString());
        QueryWifiTimerSerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QueryWifiTimerSerialReturnDTO.class);
        System.out.println(serialDto);*/
        //        ModifyDeviceSettingDTO dto = RPCMessageParseHelper.generateDTOFromMessage("<return><ITEM result=\"ok\" config_sequence=\"60\" /></return>", ModifyDeviceSettingDTO.class);
//		String status = WifiDeviceDownTask.State_Failed;
//		if(ModifyDeviceSettingDTO.Result_Success.equals(dto.getResult())){
//			status = WifiDeviceDownTask.State_Done;
//		}
		
		/*String text = "<root><dev><sys><config><ITEM sequence=\"63\"/></config></sys></dev><dev><net><ad><ITEM id=\"400889\" bhu_ad_url=\"http://auth.wi2o.cn/ad/ad.js\" bhu_enable=\"enable\" /></ad></net></dev></root>";
		WifiDeviceSettingDTO dto = generateDTOFromQueryDeviceSetting(text);
		//WifiDeviceSettingDTO dto = generateDTOFromQueryDeviceSetting(content.toString());
		System.out.println(dto.getSequence());*/
		
        
		String text = "<ITEM serial=\"0200000002\" cmd=\"netspeed_test\" statue=\"doing\"><download><SUB time_cost=\"3333\" rx_bytes=\"1111\" last=\"true\"/></download><upload><SUB time_cost=\"4444\" tx_bytes=\"1113\" /></upload></ITEM>";
		Document doc = RPCMessageParseHelper.parserMessage(text);
		WifiDevicePeakSectionDTO obj = generateDTOFromQuerySpeedTest(doc);
        System.out.println(obj.toString());
        
        /*String text1 = "<register>"+
							"<login>"+
							    "<ITEM mac=\"62:68:75:aa:00:00\" version=\"1.2.15M23\" />"+
							"</login>"+
							"<bhu_module>"+
							    "<http404>"+
							        "<ITEM enable=\"enable\" codes=\"404,50*\" url=\"vap.bhunetworks.com/urlwrite\" ver=\"style001-00.00.03\" />"+
							    "</http404>"+
							    "<redirect>"+
							        //"<ITEM enable=\"enable\" rule=\"**(TBD)\" ver=\"style001-00.00.03\" />"+
							        "<ITEM enable=\"disable\" />"+
							    "</redirect>"+
							"</bhu_module>"+
						"</register>";*/
        /*String text1 = 
				"<bhu_module>"+
				    "<http404>"+
				        "<ITEM enable=\"enable\" codes=\"404,50*\" url=\"vap.bhunetworks.com/urlwrite\" ver=\"style001-00.00.03\" />"+
				    "</http404>"+
				    "<redirect>"+
				        //"<ITEM enable=\"enable\" rule=\"**(TBD)\" ver=\"style001-00.00.03\" />"+
				        "<ITEM enable=\"disable\" />"+
				    "</redirect>"+
				"</bhu_module>";*/
        //Document doc = RPCMessageParseHelper.parserMessage(payload);
		//WifiDeviceVapReturnDTO vapDTO = RPCMessageParseHelper.generateVapDTOFromMessage(doc);
        
        String text1 = "<register><login>"+
        					"<ITEM version=\"H108V1.2.10M8281\" />"+
        						"</login>"+
        				"<bhu_module>"+
        					"<http404>"+
					                "<ITEM enable=\"disable\" ver=\"style001-00.00.03\" codes=\"40*,50*,10*\" url=\"http://vap.bhunetworks.com/vap/rw404?bid=10002\" />"+
					        "</http404>"+
					        "<redirect>"+
					                "<ITEM enable=\"enable\" ver=\"style000-00.00.01\" rule=\"100,11:00:00,23:00:00,http://www.sina.com.cn,http://www.bhunetworks.com,http://www.chinaren.com,http://www.bhunetworks.com\" />"+
					        "</redirect>"+
					"</bhu_module>"+
					"</register>";
        Document doc1= RPCMessageParseHelper.parserMessage(text1);
        
        WifiDeviceVapReturnDTO generateVapDTOFromMessage = generateVapDTOFromMessage(doc1);
        
        System.out.println(generateVapDTOFromMessage);
        
	}
}
