package com.bhu.vas.business.processor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class WifiSimulateMultiConsumerTest {
	
	public static void main(String[] argv){
		String[] locations = {
							  "classpath*:com/bhu/vas/business/processor/testCtx.xml",
							  "classpath*:spring/applicationContextCore-resource.xml",
							  "classpath*:spring/applicationContext-i18n.xml",
							  "classpath*:springmq/applicationContext-activemq-dynamics-server.xml",
							  "classpath*:springmq/applicationContext-activemq-dynamics-consumer.xml"
		};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		
		
		//ActiveMQConnectionsManager.getInstance().createNewConsumerQueues(QueueInfo.build("192.168.66.155", 61616, "E1"),"up",true);
		//ActiveMQConnectionsManager.getInstance().createNewConsumerQueues(QueueInfo.build("192.168.66.7", 61616, "F2"),"up",true);
	}
}
