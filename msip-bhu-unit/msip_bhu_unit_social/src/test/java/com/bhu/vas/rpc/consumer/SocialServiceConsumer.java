package com.bhu.vas.rpc.consumer;

import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SocialServiceConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUSocial");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "21880");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml"});
		context.start();

		ISocialRpcService socialRpcService = (ISocialRpcService)context.getBean("socialRpcService");

		System.out.println("123123123");
		socialRpcService.comment(1016,"1234", "123123123");
		socialRpcService.handsetMeet(1016L, "123123123", "34234234", "13333", "baidu", "10.18", "10.1");

		System.out.println("end");
		
		Thread.currentThread().join();
		context.close();
	}
}
