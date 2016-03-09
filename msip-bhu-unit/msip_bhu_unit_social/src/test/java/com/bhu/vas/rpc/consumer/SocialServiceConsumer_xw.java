package com.bhu.vas.rpc.consumer;

import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Set;

public class SocialServiceConsumer_xw {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUUserRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml"});
		context.start();

		ISocialRpcService socialRpcService = (ISocialRpcService)context.getBean("socialRpcService");

		Thread.currentThread().join();
		context.close();
	}
}
