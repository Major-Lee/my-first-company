package com.bhu.vas.business.processor.topic;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.asyn.spring.activemq.topic.service.DeliverTopicMessageService;
import com.smartwork.msip.localunit.RandomPicker;

public class TopicProducerTest {
	
	public static void main(String[] argv){
		String[] locations = {
							  "classpath*:com/bhu/vas/business/processor/testCtx.xml",
							  "classpath*:spring/applicationContextCore-resource.xml",
							  "classpath*:springmq/applicationContext-activemq-server.xml",
							  "classpath*:springmq/applicationContext-activemq-deliver-topic-producer.xml"
		};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		DeliverTopicMessageService deliverMessageService =(DeliverTopicMessageService) ctx.getBean("deliverTopicMessageService");
		for(int i=0;i<1;i++)
			deliverMessageService.sendPureText(RandomPicker.pick(devices_sn));
		
		
	}
	private static String[] devices_sn = {"BN009BI112190AA","BN002BI112190AA","BN008BI112190AA","BN019BI112190AA","BN000BI112190AA"};
}
