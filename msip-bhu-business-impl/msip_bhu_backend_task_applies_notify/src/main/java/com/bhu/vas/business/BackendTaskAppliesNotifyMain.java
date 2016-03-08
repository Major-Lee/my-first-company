package com.bhu.vas.business;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class BackendTaskAppliesNotifyMain {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		//String[] locations = {"classpath*:/springtest/testCtx.xml"};//,"classpath:springmq/applicationContext-activemq-server.xml", "classpath:springmq/applicationContext-activemq-message-consumer.xml"};
		//org.springframework.scheduling.quartz
		String[] locations = {"classpath*:/spring/appCtx.xml"};
		@SuppressWarnings("unused")
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
	}
}
