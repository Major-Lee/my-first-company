package com.bhu.vas.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.dto.DeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRpcService;

public class MessageCenterConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUDevicesRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.234:2181");
		System.setProperty("provider.port", "");
		//System.setProperty("provider.port", "20882");
		//System.out.println("~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();
		IDeviceRpcService tokenRpcService = (IDeviceRpcService)context.getBean("deviceRpcService");
		//System.out.println(tokenRpcService);
		/*DeviceDTO dto = new DeviceDTO();
		dto.setMac("34:36:3b:d0:4b:ac");
		dto.setT(System.currentTimeMillis());
		for(int i=0;i<1000;i++){
			try{
				boolean ret = tokenRpcService.deviceRegister(dto, null);//.generateUserAccessToken(200082, true, true);
				//System.out.println(ret);
				//ad.sleep(50);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//System.out.println(i);
		}
		System.out.println("done");
		

		//Thread.sleep(1000000000l);
		//System.out.println("Press any key to exit.");
		//System.in.read();
		 * 
*/	
		String message = "";
		tokenRpcService.wifiDeviceRegister(message, null);
		System.out.println("done2");
	}
}
