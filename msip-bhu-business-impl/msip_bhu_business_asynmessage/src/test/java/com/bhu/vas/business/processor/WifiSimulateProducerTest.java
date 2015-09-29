package com.bhu.vas.business.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.business.asyn.normal.activemq.ActiveMQDynamicProducer;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

public class WifiSimulateProducerTest {
	
	public static void main(String[] argv){
		String[] locations = {
							  "classpath*:com/bhu/vas/business/processor/testCtx.xml",
							  "classpath*:spring/applicationContextCore-resource.xml",
							 // "classpath*:springmq/applicationContext-activemq-server.xml",
							  //"classpath*:springmq/applicationContext-activemq-deliver-producer.xml",
							  "classpath*:springmq/applicationContext-activemq-dynamic-server.xml",
							  "classpath*:springmq/applicationContext-activemq-dynamic-producer.xml"/*,
							  "classpath*:springmq/applicationContext-activemq-dynamic-consumer.xml"*/
		};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		/*DeliverMessageService deliverMessageService =(DeliverMessageService) ctx.getBean("deliverMessageService");
		
		for(int i=0;i<cms.size();i++){
			CmCtxInfo cinfo = cms.get(i);
			deliverMessageService.sendPureText("00010000"+JsonHelper.getJSONString(cinfo));
		}
		
		for(int i=0;i<cms.size();i++){
			CmCtxInfo cinfo = cms.get(i);
			deliverMessageService.sendPureText("00010001"+JsonHelper.getJSONString(cinfo));
		}
		
		CmCtxInfo cinfo = new CmCtxInfo("cm003","1");
		deliverMessageService.sendPureText("00010000"+JsonHelper.getJSONString(cinfo));
		deliverMessageService.sendPureText("00010001"+JsonHelper.getJSONString(cinfo));
		*/
		ActiveMQDynamicProducer activeMQDynamicProducer =(ActiveMQDynamicProducer) ctx.getBean("activeMQDynamicProducer");
		//for Input Queue Create test Producers
		activeMQDynamicProducer.initTestProducers();
		for(int i=0;i<online_devices.size();i++){
			WifiInfo winfo = online_devices.get(i);
			String Queue_Key = activeMQDynamicProducer.randomTestProducerKey();
			System.out.println(Queue_Key);
			//wifi上线消息
			activeMQDynamicProducer.deliverTestMessage(Queue_Key, 
					String.format(wifi_online_msg_template,
							StringHelper.unformatMacAddress(winfo.getMac()), winfo.getSn(),winfo.getMac(),winfo.getIp()));
			//wifi下线消息
			//activeMQDynamicProducer.deliverTestMessage(Queue_Key, 
			//		String.format(wifi_offline_msg_template, winfo.getMac(),winfo.getIp()));
		}
		
		
	}
	private static String[] devices_sn = {"BN009BI112190AA","BN002BI112190AA","BN008BI112190AA","BN019BI112190AA","BN000BI112190AA"};
	private static String[] devices_ip = {"192.168.0.1","192.168.0.2","192.168.0.3","192.168.0.4","192.168.0.5"};
	private static String macAddress_template = "%s:%s:%s:%s:%s:%s";
	public static final String SuffixTemplete = "%02d";
	
	private static List<WifiInfo> online_devices = new ArrayList<WifiInfo>();
	static{
		online_devices.add(new WifiInfo("34:36:3b:d0:4b:ac","BN001BI112190AA","192.168.0.1"));
		online_devices.add(new WifiInfo("72:00:08:75:ef:c0","BN002BI112190AA","192.168.0.2"));
		/*online_devices.add(new WifiInfo("72:00:08:75:ef:c1","BN003BI112190AA","192.168.0.3"));
		online_devices.add(new WifiInfo("06:36:3b:d0:4b:ac","BN004BI112190AA","192.168.0.4"));
		online_devices.add(new WifiInfo("8a:48:9b:16:97:0f","BN005BI112190AA","192.168.0.5"));
		online_devices.add(new WifiInfo("36:36:3b:0d:18:00","BN006BI112190AA","192.168.0.6"));
		online_devices.add(new WifiInfo("C8:9C:DC:E6:77:E6","BN007BI112190AA","192.168.0.7"));
		online_devices.add(new WifiInfo("D4:BE:D9:E3:8F:EB","BN008BI112190AA","192.168.0.8"));
		online_devices.add(new WifiInfo("8a:48:9b:16:07:0f","BN009BI112190AA","192.168.0.9"));
		online_devices.add(new WifiInfo("8a:48:9b:16:17:1f","BN010BI112190AA","192.168.0.10"));
		online_devices.add(new WifiInfo("8a:48:9b:16:27:2f","BN011BI112190AA","192.168.0.11"));
		online_devices.add(new WifiInfo("8a:48:9b:16:37:3f","BN012BI112190AA","192.168.0.12"));
		online_devices.add(new WifiInfo("8a:48:9b:16:47:4f","BN013BI112190AA","192.168.0.13"));
		online_devices.add(new WifiInfo("8a:48:9b:16:57:5f","BN014BI112190AA","192.168.0.14"));
		online_devices.add(new WifiInfo("8a:48:9b:16:67:6f","BN015BI112190AA","192.168.0.15"));
		online_devices.add(new WifiInfo("8a:48:9b:16:77:7f","BN016BI112190AA","192.168.0.16"));
		online_devices.add(new WifiInfo("8a:48:9b:16:87:8f","BN017BI112190AA","192.168.0.17"));
		online_devices.add(new WifiInfo("8a:48:9b:16:97:9f","BN019BI112190AA","192.168.0.18"));
		online_devices.add(new WifiInfo("8a:48:1b:16:07:7f","BN019BI112190AA","192.168.0.19"));
		online_devices.add(new WifiInfo("8a:48:2b:16:17:6f","BN020BI112190AA","192.168.0.20"));
		online_devices.add(new WifiInfo("8a:48:3b:16:27:5f","BN000BI112190AA","192.168.0.21"));*/
		
		long ts = System.currentTimeMillis();
		Set<WifiInfo> tmps = new HashSet<WifiInfo>();
		int size = 0;
		while(true){
			//boolean ret = tmps.add(new WifiInfo(randomMacAddress(),RandomPicker.pick(devices_sn),RandomPicker.pick(devices_ip)));
			boolean ret = tmps.add(new WifiInfo("62:68:75:02:ff:05",RandomPicker.pick(devices_sn),RandomPicker.pick(devices_ip)));
			if(ret){
				size++;
			}
			if(size >=1)
				break;
			else
				System.out.println(size);
		}
		online_devices.addAll(tmps);
		System.out.println("init 10000 unique devices cost:"+(System.currentTimeMillis()-ts)/1000+"s");
	}
	private static String randomMacAddress(){
		return String.format(macAddress_template, formatRandom(), formatRandom(), formatRandom(), formatRandom(), formatRandom(), formatRandom());
	}
	
	private static String formatRandom(){
		return String.format(SuffixTemplete, RandomData.intNumber(1, 99));
	}
	
	
	private static List<CmCtxInfo> cms = new ArrayList<CmCtxInfo>();
	static{
		cms.add(new CmCtxInfo("cm001","1"));
		cms.add(new CmCtxInfo("cm001","2"));
		
		cms.add(new CmCtxInfo("cm002","1"));
		cms.add(new CmCtxInfo("cm002","2"));
	}
	//0000000562687500003e0000000000000000000001
	private static String wifi_online_msg_template = "00000005%s0000000000000000000001<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
									 "<join_req>"+										
									  	"<ITEM orig_vendor=\"BHU\" hdtype=\"H104\" orig_model=\"BXO2000n(2S-Lite)\" orig_hdver=\"B1\" orig_swver=\"AP104P07V1.2.9Build6755\" oem_vendor=\"BHU\" oem_model=\"BXO2000n(2S-Lite)\" oem_hdver=\"B1\" oem_swver=\"AP104P07V1.2.9Build6755\" sn=\"%s\" mac=\"%s\" ip=\"%s\" build_info=\"2015-02-16-11:09 Revision: 6755\" config_model_ver=\"V3\" config_mode=\"basic\" work_mode=\"bridge-ap\" config_sequence=\"63\" join_reason=\"3\" />"+
									 "</join_req>";
	
	private static String wifi_offline_msg_template = "00000003%s0000000000000000000001<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
									 "<leave_req>"+										
									  	"<ITEM mac=\"%s\" ip=\"%s\" leave_reason=\"3\" />"+
									 "</leave_req>";
	
	private static String wifi_not_exist_msg_template = "00000004%s";
	
	/*private static String wifi_task_timeout_msg_template = "00000004<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
									 "<task_timeout_req>"+										
									  	"<ITEM mac=\"84:82:f4:05:54:27\" taskid=\"0000001234\"/>"+
									 "</task_timeout_req>";*/
	
	//private static String user_online_msg = "";
	//private static String user_offline_msg = "";
	
}
