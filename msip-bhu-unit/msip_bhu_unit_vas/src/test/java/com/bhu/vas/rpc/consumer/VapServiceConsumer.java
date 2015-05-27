package com.bhu.vas.rpc.consumer;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class VapServiceConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUUserRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();

		IVapRpcService vapRpcService = (IVapRpcService)context.getBean("vapRpcService");
		vapRpcService.urlView("html","2323");
		
		Thread.currentThread().join();
	}
}
