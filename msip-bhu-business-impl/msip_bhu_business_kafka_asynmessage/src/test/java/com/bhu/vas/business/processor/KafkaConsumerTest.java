package com.bhu.vas.business.processor;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class KafkaConsumerTest {
	
	public static void main(String[] argv) throws InterruptedException{
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/springkafka/testConsumerCtx.xml", KafkaConsumerTest.class);
		ctx.start();
		//ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		//ctx.getBean(arg0);
		Thread.sleep(1000000000l);
	}
}
