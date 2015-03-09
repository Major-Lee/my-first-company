package com.bhu.vas.business.mq.activemq.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TestDynamicMqService {
	public static void main(String[] argv) throws InterruptedException{
		String[] locations = {"classpath*:/springmq/applicationContext-activemq-dynamic-server.xml"};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		///ActiveMQDynamicService service = (ActiveMQDynamicService)ctx.getBean("activeMQDynamicService");
		//System.out.println(service);
		
		
		Thread.sleep(100000000000l);
	}
}
