package com.bhu.vas.business.processor;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class KafkaCommonMessageConsumerTest {
	
	public static void main(String[] argv) throws InterruptedException{
		String[] CONFIG = {
			"/com/bhu/vas/business/processor/testCtx.xml",
			"/spring/applicationContextCore-resource.xml",
			"/com/smartwork/async/messagequeue/kafka/inbound/applicationContext-InboundKafka-CommonMessage-MessageHandler.xml",
			"/com/smartwork/async/messagequeue/kafka/inbound/applicationContext-InboundKafka-CommonMessage-AdapterParser.xml"};
		/*String[] locations = {
				  "classpath*:com/bhu/vas/business/processor/testCtx.xml",
				  "classpath*:spring/applicationContextCore-resource.xml",
				  "classpath*:springmq/applicationContext-activemq-server.xml",
				  "classpath*:springmq/applicationContext-activemq-deliver-consumer.xml",
				  "classpath*:springmq/applicationContext-activemq-dynamic-server.xml",
				  "classpath*:springmq/applicationContext-activemq-dynamic-consumer.xml"
		};*/
		
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, KafkaCommonMessageConsumerTest.class);
		ctx.start();
		//ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		//ctx.getBean(arg0);
		Thread.sleep(1000000000l);
	}
}
