package com.bhu.vas.rpc.consumer;

import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.rpc.social.vto.WifiCommentVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public class SocialCommentServiceConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUUserRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml"});
		context.start();

		ISocialRpcService socialRpcService = (ISocialRpcService)context.getBean("socialRpcService");

		System.out.println("123123123");
		socialRpcService.comment(100312,"1234", "123123123");
		RpcResponseDTO<TailPage<WifiCommentVTO>> rpcResult=socialRpcService.pageWifiCommentVTO(100312,"1234", 1, 3);
		System.out.println("end");
		RpcResponseDTO<Set<String>>rpcset=socialRpcService.fetchUserCommentWifiList("100312");
		Thread.currentThread().join();
		context.close();
	}
}
