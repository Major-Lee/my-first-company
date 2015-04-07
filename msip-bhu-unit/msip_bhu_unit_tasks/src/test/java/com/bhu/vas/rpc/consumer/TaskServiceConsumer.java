package com.bhu.vas.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;

public class TaskServiceConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUDevicesRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();
		ITaskRpcService taskRpcService = (ITaskRpcService)context.getBean("taskRpcService");
		
		RpcResponseDTO<TaskResDTO> ret = taskRpcService.createNewTask("62:68:75:02:00:06", OperationCMD.QueryDeviceStatus.getNo(), 
				/*"payload content",*/ "APP_VAS", "1236");
		
//		taskRpcService.createNewTask("", OperationCMD.QueryDeviceStatus.getNo(), 
//				"payload content", "APP_VAS", "123");
		
		taskRpcService.taskStatusFetch(123123123);
		//String message = null;
		//ParserHeader parserHeader = new ParserHeader();
		//ParserHeader parserHeader = new ParserHeader();

		//wifi设备上线
		/*parserHeader.setType(5);
		parserHeader.setMt(0);
		parserHeader.setSt(1);
		message = "<join_req><ITEM orig_vendor=\"BHU\" hdtype=\"H104\" orig_model=\"BXO2000n(2S-Lite)\" orig_hdver=\"B1\" orig_swver=\"2015-03-11-18:27 Revision: 6855\" oem_vendor=\"BHU\" oem_model=\"BXO2000n(2S-Lite)\" oem_hdver=\"B1\" oem_swver=\"2015-03-11-18:27 Revision: 6855\" sn=\"AAA\" mac=\"62:68:75:02:00:06\" ip=\"192.168.66.176\" build_info=\"2015-03-11-18:27 Revision: 6855\" config_model_ver=\"V3\" config_mode=\"basic\" work_mode=\"router-ap\" config_sequence=\"14\" join_reason=\"0\" wan_ip=\"192.168.66.176\" /></join_req>";
		tokenRpcService.messageDispatch("cm1", message, parserHeader);
		System.out.println("done1");
		
		//移动设备同步
		parserHeader = new ParserHeader();
		parserHeader.setMac("62:68:75:02:00:06");
		parserHeader.setType(5);
		parserHeader.setMt(1);
		parserHeader.setSt(7);
		message = "<event><wlan><ITEM action=\"online\" mac=\"0c:1d:af:e4:7c:ab\" phy_tx_rate=\"72M\" phy_rx_rate=\"36.5M\" phy_rate=\"72M\" tx_power=\"3dBm\" rssi=\"-34dBm\" snr=\"61dB\" idle=\"0\" uptime=\"3205\" rx_pkts=\"1483\" rx_bytes=\"169195\" tx_pkts=\"912\" tx_bytes=\"110946\" rx_unicast=\"0\" tx_assoc=\"1\" bssid=\"62:68:75:00:00:3f\" ssid=\"ythello2\" location=\"\" /></wlan></event>";
		tokenRpcService.messageDispatch("cm1", message, parserHeader);*/
		System.out.println("done2");
		
/*//		boolean a = true;
//		while(a){
			//wifi设备离线
			parserHeader = new ParserHeader();
			parserHeader.setType(3);
			parserHeader.setMac("62:68:75:02:00:06");
			message = null;
			tokenRpcService.messageDispatch("cm1", message, parserHeader);
			System.out.println("done3");
//			Thread.sleep(500);
//		}
		
		//wifi设备告警
		parserHeader = new ParserHeader();
		parserHeader.setType(5);
		parserHeader.setMt(1);
		parserHeader.setSt(6);
		parserHeader.setMac("62:68:75:02:00:06");
		message = "<event><trap><ITEM name=\"apAcTimeSyncFailureTrap\" serial_number=\"BN009BC100056AA\" ne_name=\"\" mac_addr=\"62:68:75:00:00:3e\" alarm_level=\"1\" alarm_type=\"0\" alarm_cause=\"1\" alarm_reason=\"System Time Synchronize Failure\" alarm_event_time=\"1426325752\" alarm_status=\"1\" alarm_title=\"System Time Synchronize Failure Trap\" alarm_content=\"\" alarm_serial_id=\"1\" /></trap></event>";
		tokenRpcService.messageDispatch("cm1", message, parserHeader);
		System.out.println("done4");*/
	}
}
