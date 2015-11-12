package com.bhu.vas.xml;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Test;

import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.EventWlanDTO;
import com.smartwork.msip.cores.helper.ReflectionHelper;
import com.smartwork.msip.cores.helper.XStreamHelper;
import com.smartwork.msip.cores.helper.dom4j.Dom4jHelper;
import com.smartwork.msip.localunit.BaseTest;

public class XMLParserTest extends BaseTest{
	public static final String xml = "<join_req><ITEM orig_vendor=\"BHU\" hdtype=\"H104\" orig_model=\"BXO2000n(2S-Lite)\" orig_hdver=\"B1\" orig_swver=\"2015-03-11-18:27 Revision: 6855\" oem_vendor=\"BHU\" oem_model=\"BXO2000n(2S-Lite)\" oem_hdver=\"B1\" oem_swver=\"2015-03-11-18:27 Revision: 6855\" sn=\"AAA\" mac=\"62:68:75:02:00:06\" ip=\"192.168.66.176\" build_info=\"2015-03-11-18:27 Revision: 6855\" config_model_ver=\"V3\" config_mode=\"basic\" work_mode=\"router-ap\" config_sequence=\"14\" join_reason=\"0\" wan_ip=\"192.168.66.176\" /></join_req>";
	public static final String xml2 = "<event><wlan><ITEM action=\"sync\" mac=\"0c:1d:af:e4:7c:ab\" phy_tx_rate=\"72M\" phy_rx_rate=\"36.5M\" phy_rate=\"72M\" tx_power=\"3dBm\" rssi=\"-34dBm\" snr=\"61dB\" idle=\"0\" uptime=\"3205\" rx_pkts=\"1483\" rx_bytes=\"169195\" tx_pkts=\"912\" tx_bytes=\"110946\" rx_unicast=\"0\" tx_assoc=\"1\" bssid=\"62:68:75:00:00:3f\" ssid=\"ythello2\" location=\"\" /></wlan></event>";
	//@Test
	public void	xstream_test(){
		//JoinReqDTO dto = XStreamHelper.fromXML(xml, JoinReqDTO.class);
		@SuppressWarnings("unused")
		EventWlanDTO dto = XStreamHelper.fromXML(xml2, EventWlanDTO.class);
	}
	
	//@Test
	public void dom4j_test(){
		try {
			Document doc = Dom4jHelper.parseText(xml);
			Element item_element = (Element)doc.selectSingleNode("//ITEM");
			@SuppressWarnings("unchecked")
			List<Attribute> list = item_element.attributes();
			WifiDeviceDTO dto = WifiDeviceDTO.class.newInstance();
			for(Attribute attr : list){
				ReflectionHelper.invokeSetterMethod(dto, attr.getName(), attr.getValue());
			}
			//System.out.println(dto.getMac());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void performance_dom4j(){
		dom4j_test();
		long t = System.currentTimeMillis();
		for(int i=0;i<1000;i++){
			dom4j_test();
		}
		System.out.println((System.currentTimeMillis() - t)+"ms dom4j");
	}
	
	//@Test
	public void performance_xstream(){
		xstream_test();
		long t = System.currentTimeMillis();
		for(int i=0;i<1000;i++){
			xstream_test();
		}
		System.out.println((System.currentTimeMillis() - t)+"ms xstream");
	}
}
