package com.bhu.vas.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.tag.iservice.ITagRpcService;
import com.bhu.vas.api.rpc.tag.vto.TagGroupVTO;

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
//		tagRpcService.saveTreeNode(100001, 0, 24, "101");
//		boolean tagGroupVTO = tagRpcService.saveDevices2Group(100001, 21, 0, "84:82:f4:28:8f:b2");
//		tagRpcService.delNode(100001, "19");
		System.out.println("2222");
		Thread.currentThread().join();
		context.close();
	}
}