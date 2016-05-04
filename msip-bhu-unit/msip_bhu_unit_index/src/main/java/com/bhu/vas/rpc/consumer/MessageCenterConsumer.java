package com.bhu.vas.rpc.consumer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.index.iservice.IWifiDeviceDocumentRpcService;

public class MessageCenterConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUDevicesRpcConsumerApp");
		System.setProperty("zookeeper", "127.0.0.1:2181");
		System.setProperty("provider.port", "");
		//System.setProperty("provider.port", "20882");
		//System.out.println("~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();
//		IDeviceMessageDispatchRpcService tokenRpcService = (IDeviceMessageDispatchRpcService)context.getBean("deviceMessageDispatchRpcService");
//
//		String message = null;
//		ParserHeader parserHeader = new ParserHeader();
//		//ParserHeader parserHeader = new ParserHeader();
//		
//		//wifi设备上线
//		parserHeader.setType(5);
//		parserHeader.setMt(0);
//		parserHeader.setSt(1);
//		message = "<join_req><ITEM orig_vendor=\"BHU\" hdtype=\"H104\" orig_model=\"BXO2000n(2S-Lite)\" orig_hdver=\"B1\" orig_swver=\"2015-03-11-18:27 Revision: 6855\" oem_vendor=\"BHU\" oem_model=\"BXO2000n(2S-Lite)\" oem_hdver=\"B1\" oem_swver=\"2015-03-11-18:27 Revision: 6855\" sn=\"AAA\" mac=\"62:68:75:02:00:06\" ip=\"192.168.66.176\" build_info=\"2015-03-11-18:27 Revision: 6855\" config_model_ver=\"V3\" config_mode=\"basic\" work_mode=\"router-ap\" config_sequence=\"14\" join_reason=\"0\" wan_ip=\"192.168.66.176\" /></join_req>";
//		tokenRpcService.messageDispatch("cm1", message, parserHeader);
//		System.out.println("done1");
//		
//		//移动设备同步
//		parserHeader = new ParserHeader();
//		parserHeader.setMac("62:68:75:02:00:06");
//		parserHeader.setType(5);
//		parserHeader.setMt(1);
//		parserHeader.setSt(7);
//		message = "<event><wlan><ITEM action=\"online\" mac=\"0c:1d:af:e4:7c:ab\" phy_tx_rate=\"72M\" phy_rx_rate=\"36.5M\" phy_rate=\"72M\" tx_power=\"3dBm\" rssi=\"-34dBm\" snr=\"61dB\" idle=\"0\" uptime=\"3205\" rx_pkts=\"1483\" rx_bytes=\"169195\" tx_pkts=\"912\" tx_bytes=\"110946\" rx_unicast=\"0\" tx_assoc=\"1\" bssid=\"62:68:75:00:00:3f\" ssid=\"ythello2\" location=\"\" /></wlan></event>";
//		tokenRpcService.messageDispatch("cm1", message, parserHeader);
//		System.out.println("done2");
//		
////		boolean a = true;
////		while(a){
//			//wifi设备离线
//			parserHeader = new ParserHeader();
//			parserHeader.setType(3);
//			parserHeader.setMac("62:68:75:02:00:06");
//			message = null;
//			tokenRpcService.messageDispatch("cm1", message, parserHeader);
//			System.out.println("done3");
////			Thread.sleep(500);
////		}
//		
//		//wifi设备告警
//		parserHeader = new ParserHeader();
//		parserHeader.setType(5);
//		parserHeader.setMt(1);
//		parserHeader.setSt(6);
//		parserHeader.setMac("62:68:75:02:00:06");
//		message = "<event><trap><ITEM name=\"apAcTimeSyncFailureTrap\" serial_number=\"BN009BC100056AA\" ne_name=\"\" mac_addr=\"62:68:75:00:00:3e\" alarm_level=\"1\" alarm_type=\"0\" alarm_cause=\"1\" alarm_reason=\"System Time Synchronize Failure\" alarm_event_time=\"1426325752\" alarm_status=\"1\" alarm_title=\"System Time Synchronize Failure Trap\" alarm_content=\"\" alarm_serial_id=\"1\" /></trap></event>";
//		tokenRpcService.messageDispatch("cm1", message, parserHeader);
//		System.out.println("done4");
		
		IWifiDeviceDocumentRpcService service = (IWifiDeviceDocumentRpcService)context.getBean("wifiDeviceDocumentRpcService");
/*		for(int i = 0;i<5;i++){
			service.messageDispose("1", "message1");
			service.messageDispose("2", "message2");
			service.messageDispose("3", "message3");
			service.messageDispose("4", "message1");
			service.messageDispose("5", "message2");
			service.messageDispose("6", "message3");
			service.messageDispose("7", "message1");
			service.messageDispose("8", "message2");
			service.messageDispose("9", "message3");
			service.messageDispose("10", "message1");
		}*/
		//service.templateUpdIncrement("34:31:11:10:76:35", "tmp1");
		List<String> ids = new ArrayList<String>();
		ids.add("34:31:11:10:76:35");
		service.blukIndexs(ids);
	}
}
