package com.bhu.vas.business.processor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.mq.activemq.ActiveMQDynamicProducer;

public class WifiSimulateTest {
	
	public static void main(String[] argv){
		String[] locations = {
							  "classpath*:com/bhu/vas/business/processor/testCtx.xml",
							  "classpath*:spring/applicationContextCore-resource.xml",
							  "classpath*:springmq/applicationContext-activemq-server.xml",
							  "classpath*:springmq/applicationContext-activemq-deliver-producer.xml",
							  "classpath*:springmq/applicationContext-activemq-dynamic-server.xml",
							  "classpath*:springmq/applicationContext-activemq-dynamic-producer.xml",
							  "classpath*:springmq/applicationContext-activemq-dynamic-consumer.xml"
		};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		
		ActiveMQDynamicProducer activeMQDynamicProducer =(ActiveMQDynamicProducer) ctx.getBean("activeMQDynamicProducer");
		
		for(int i=0;i<10;i++){
			activeMQDynamicProducer.deliverMessage("out_11", String.valueOf(i));
			activeMQDynamicProducer.deliverMessage("out_22", String.valueOf(i));
			activeMQDynamicProducer.deliverMessage("out_33", String.valueOf(i));
			activeMQDynamicProducer.deliverMessage("out_44", String.valueOf(i));
		}
		
		/*DeliverMessageService deliverMessageService =(DeliverMessageService) ctx.getBean("deliverMessageService");
		
		deliverMessageService.sendUserRegisteredActionMessage(DeliverMessageType.AC.getPrefix(), 1001, "wandojia", "ADR", "192.168.0.1");
		
		long ts = System.currentTimeMillis();
		for(int j=0;j<10;j++){
			//for(int i=0;i<20000;i++){
			deliverMessageService.sendUserRegisteredActionMessage(DeliverMessageType.AC.getPrefix(), 1001, "wandojia", "ADR", "192.168.0.1");
			//}
		}
		long cost = System.currentTimeMillis() - ts;
		System.out.println("cost:"+cost/1000);*/
	}
}
