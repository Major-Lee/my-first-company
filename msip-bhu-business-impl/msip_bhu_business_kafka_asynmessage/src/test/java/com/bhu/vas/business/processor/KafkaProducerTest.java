package com.bhu.vas.business.processor;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.business.asyn.kafka.producer.KafkaMessageQueueProducer;


public class KafkaProducerTest {
	
	public static void main(String[] argv){
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/springkafka/testProducerCtx.xml", KafkaProducerTest.class);
		ctx.start();

		KafkaMessageQueueProducer producer = ctx.getBean("producer", KafkaMessageQueueProducer.class);
		for(int i=0;i<100;i++){
			producer.send(String.valueOf(i));
		}
	}
	
}
