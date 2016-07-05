package com.bhu.vas.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.unifyStatistics.iservice.IUnifyStatisticsRpcService;

public class TagServiceConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUStat");
		System.setProperty("zookeeper", "192.168.66.124:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml"});
		context.start();

		IUnifyStatisticsRpcService unifyStatisticsRpcService = (IUnifyStatisticsRpcService)context.getBean("unifyStatisticsRpcService");
		unifyStatisticsRpcService.stateStat();
		System.out.println("123123123");
//		socialRpcService.comment(1016,"1234","u3333", "123123123");
//		socialRpcService.handsetMeet(1016L, "123123123", "34234234", "13333", "baidu", "10.18", "10.1");

		System.out.println("end");
		
		Thread.currentThread().join();
		context.close();
	}
}
