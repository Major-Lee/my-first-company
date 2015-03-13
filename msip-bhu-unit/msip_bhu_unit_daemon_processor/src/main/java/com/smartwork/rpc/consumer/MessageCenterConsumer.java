package com.smartwork.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.daemon.iservice.IWifiDeviceCmdDownRpcService;
import com.bhu.vas.api.rpc.devices.dto.WifiDeviceContextDTO;

public class MessageCenterConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUDaemonProcessorRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		//System.setProperty("provider.port", "20882");
		//System.out.println("~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/smartwork/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();
		IWifiDeviceCmdDownRpcService rpcService = (IWifiDeviceCmdDownRpcService)context.getBean("wifiDeviceCmdDownRpcService");
		//System.out.println(tokenRpcService);
		WifiDeviceContextDTO dto = new WifiDeviceContextDTO();
		dto.setCmId("1380");
		dto.setCmName("CM001");
		dto.setMac("34:36:3b:d0:4b:ac");
		for(int i=0;i<1000;i++){
			try{
				boolean ret = rpcService.wifiDeviceRegister(dto);//.deviceRegister(dto);//.generateUserAccessToken(200082, true, true);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		System.out.println("done");
	}
}
