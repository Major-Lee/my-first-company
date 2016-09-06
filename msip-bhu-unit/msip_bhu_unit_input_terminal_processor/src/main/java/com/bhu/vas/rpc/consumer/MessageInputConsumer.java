package com.bhu.vas.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageInputConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUInputRpcConsumerApp");
		//System.setProperty("zookeeper", "192.168.66.191:2181");
		System.setProperty("provider.port", "");
		//System.setProperty("provider.port", "20882");
		//System.out.println("~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		/*ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:springkafka/applicationContextCore-resource.xml",
				"classpath*:/com/smartwork/rpc/consumer/applicationContextRpcUnitConsumer.xml" });*/
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:springunit/appCtxUnit.xml" });
		context.start();
		Thread.currentThread().join();
	}
}
