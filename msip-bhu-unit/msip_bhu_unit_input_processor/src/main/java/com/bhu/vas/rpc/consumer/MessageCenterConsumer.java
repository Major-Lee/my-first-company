package com.bhu.vas.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageCenterConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUDaemonProcessorRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.1.3:2181");
		System.setProperty("provider.port", "");
		//System.setProperty("provider.port", "20882");
		//System.out.println("~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		/*ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/smartwork/rpc/consumer/applicationContextRpcUnitConsumer.xml" });*/
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:springunit/appCtxUnit.xml" });
		//context.start();
		/*IWifiDeviceCmdDownRpcService rpcService = (IWifiDeviceCmdDownRpcService)context.getBean("wifiDeviceCmdDownRpcService");
		//System.out.println(tokenRpcService);
		WifiDeviceContextDTO dto = new WifiDeviceContextDTO();
		dto.setCm_id("1380");
		dto.setCm_name("CM001");
		dto.setMac("34:36:3b:d0:4b:ac");
		for(int i=0;i<100;i++){
			try{
				boolean ret = rpcService.wifiDeviceRegister(dto);//.deviceRegister(dto);//.generateUserAccessToken(200082, true, true);
				//System.out.println(ret);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		System.out.println("done");*/
		//ActiveMQDynamicService dynamicService = (ActiveMQDynamicService)context.getBean("activeMQDynamicService");
		/*
		dynamicService.onMessage("1234", "dddddddddddddddddd");
		System.out.println("done");*/
	}
}
