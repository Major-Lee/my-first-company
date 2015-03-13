package com.bhu.vas.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.WifiDeviceContextDTO;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;

public class MessageDaemonConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUDaemonProcessorRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.234:2181");
		System.setProperty("provider.port", "");
		//System.setProperty("provider.port", "20882");
		//System.out.println("~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();
		IDaemonRpcService rpcService = (IDaemonRpcService)context.getBean("daemonRpcService");
		//System.out.println(tokenRpcService);
		CmCtxInfo info = new CmCtxInfo("cm002","1");
		WifiDeviceContextDTO dto = new WifiDeviceContextDTO();
		dto.setInfo(info);
		//dto.setCmId("1380");
		//dto.setCmName("CM001");
		dto.setMac("34:36:3b:d0:4b:ac");
		rpcService.wifiDeviceOnline(dto);
		rpcService.wifiDeviceOffline(dto);
		
		rpcService.wifiDeviceCmdDown(dto, "where are u");
		rpcService.cmJoinService(info);
		rpcService.cmLeave(info);
		/*for(int i=0;i<1000;i++){
			try{
				boolean ret = rpcService.wifiDeviceOnline(dto);//.deviceRegister(dto);//.generateUserAccessToken(200082, true, true);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}*/
		System.out.println("done");
	}
}
