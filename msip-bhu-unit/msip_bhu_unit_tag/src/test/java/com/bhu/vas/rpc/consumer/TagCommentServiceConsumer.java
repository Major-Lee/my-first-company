package com.bhu.vas.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.tag.iservice.ITagRpcService;

public class TagCommentServiceConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUTag");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml"});
		context.start();

		ITagRpcService tagRpcService = (ITagRpcService)context.getBean("tagRpcService");
		System.out.println("123123123");
		//tagRpcService.bindTag(100035,"84:82:f4:28:7a:ec", "咖啡馆"); //{"items":[{"tag":"公司"}]}
		//tagRpcService.aaa("nihao");
		System.out.println("end");
		Thread.currentThread().join();
		context.close();
	}
}
