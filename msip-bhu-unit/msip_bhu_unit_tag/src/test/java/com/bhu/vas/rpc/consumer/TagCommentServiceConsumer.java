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
		tagRpcService.bindTag("84:82:f4:28:7a:ec", "{\"items\":[{\"tag\":\"咖啡馆\"}]}"); //{"items":[{"tag":"公司"}]}
//		socialRpcService.comment(100312,"123123","ussss", "123123123");
//		RpcResponseDTO<TailPage<WifiCommentVTO>> rpcResult=socialRpcService.pageWifiCommentVTO(100312,"123123", 1, 3);
//		RpcResponseDTO<List<CommentedWifiVTO>>rpcset=socialRpcService.fetchUserCommentWifiList("100312","222222");
		System.out.println("end");
		Thread.currentThread().join();
		context.close();
	}
}
