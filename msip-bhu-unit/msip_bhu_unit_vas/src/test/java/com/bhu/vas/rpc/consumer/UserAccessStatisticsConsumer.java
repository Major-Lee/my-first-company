package com.bhu.vas.rpc.consumer;

import com.bhu.vas.api.rpc.statistics.iservice.IStatisticsRpcService;
import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.smartwork.msip.cores.helper.DateHelper;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserAccessStatisticsConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUUserRpcConsumerApp");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();

		IStatisticsRpcService statisticsRpcService = (IStatisticsRpcService)context.getBean("statisticsRpcService");
		statisticsRpcService.createUserAccessStatistics("/var/log/bhu/2015-04-29/logfile.log");
        //statisticsRpcService.createUserAccessStatistics("/Users/bluesand/Documents/bhu/msip_bhu_business/msip-bhu-business-impl/msip_bhu_business_ds/src/test/java/com/bhu/vas/business/statistics/logfile.log");

		Thread.currentThread().join();
	}
}
