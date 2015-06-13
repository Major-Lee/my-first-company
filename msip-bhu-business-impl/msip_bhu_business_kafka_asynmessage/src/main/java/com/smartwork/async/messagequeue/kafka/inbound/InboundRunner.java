package com.smartwork.async.messagequeue.kafka.inbound;

import java.io.UnsupportedEncodingException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InboundRunner {
	//private static final String CONFIG = "kafkaInboundAdapterParserTests-context.xml";
	private static final String CONFIG = "/com/smartwork/async/messagequeue/kafka/inbound/applicationContext-InboundKafkaAdapterParser.xml";

	public static void main(final String args[]) throws UnsupportedEncodingException {
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, InboundRunner.class);
		ctx.start();
		/*EventDrivenConsumer obj = ctx.getBean("stdout",EventDrivenConsumer.class);
		obj.
		System.out.println("~~~~~~~:"+obj);
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ctx.close();*/
	}
}
