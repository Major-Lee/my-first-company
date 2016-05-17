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
		//tagRpcService.bindTag(100035,"84:82:f4:28:7a:ec", "咖啡馆"); //{"items":[{"tag":"公司"}]}
		System.out.println("1111");
//		tagRpcService.saveTreeNode(100001, 0, 0, "测试");
		tagRpcService.saveDevices2Group(100234, 11,"11/" , "84:82:f4:28:7a:ec");
//		tagRpcService.delNode(100001, "2");
		System.out.println("2222");
		Thread.currentThread().join();
		context.close();
	}
}