package com.bhu.vas.rpc.consumer;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.iservice.IStatisticsRpcService;

public class UserAccessStatisticsConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUUserRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();

		//方法已经从service中移除挪到dataimport中
		IStatisticsRpcService statisticsRpcService = (IStatisticsRpcService)context.getBean("statisticsRpcService");
        //System.out.println(statisticsRpcService.createUserAccessStatistics("/var/log/bhu/2015-04-29/logfile.log"));
        //statisticsRpcService.createUserAccessStatistics("/Users/bluesand/Documents/bhu/msip_bhu_business/msip-bhu-business-impl/msip_bhu_business_ds/src/test/java/com/bhu/vas/business/statistics/logfile.log");

		RpcResponseDTO<List<UserBrandDTO>> result = statisticsRpcService.fetchUserBrandStatistics("2015-06-07");

		Thread.currentThread().join();
	}
}
