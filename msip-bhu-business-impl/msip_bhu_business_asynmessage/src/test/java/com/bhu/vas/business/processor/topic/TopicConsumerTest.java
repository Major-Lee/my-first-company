package com.bhu.vas.business.processor.topic;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TopicConsumerTest {
	
	public static void main(String[] argv){
		String[] locations = {
							  "classpath*:com/bhu/vas/business/processor/testCtx.xml",
							  "classpath*:spring/applicationContextCore-resource.xml",
							  "classpath*:springmq/applicationContext-activemq-server.xml",
							  "classpath*:springmq/applicationContext-activemq-deliver-topic-consumer.xml"
		};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		
	}
}
