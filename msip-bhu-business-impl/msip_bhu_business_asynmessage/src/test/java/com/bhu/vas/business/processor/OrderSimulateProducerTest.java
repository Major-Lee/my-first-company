package com.bhu.vas.business.processor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.asyn.spring.activemq.service.CommdityMessageService;

public class OrderSimulateProducerTest {
	
	public static void main(String[] argv) throws InterruptedException{
		String[] locations = {
							  "classpath*:com/bhu/vas/business/processor/testCtx.xml",
							  "classpath*:spring/applicationContextCore-resource.xml",
							  "classpath*:springmq/applicationContext-activemq-server.xml",
							  "classpath*:springmq/applicationContext-activemq-commdity-producer.xml",
							  //"classpath*:springmq/applicationContext-activemq-dynamic-server.xml",
							  //"classpath*:springmq/applicationContext-activemq-dynamic-producer.xml"/*,
							  //"classpath*:springmq/applicationContext-activemq-dynamic-consumer.xml"*/
		};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		CommdityMessageService commdityMessageService =(CommdityMessageService) ctx.getBean("commdityMessageService");
		
		/*for(int i=0;i<cms.size();i++){
			CmCtxInfo cinfo = cms.get(i);
			deliverMessageService.sendPureText("00010000"+JsonHelper.getJSONString(cinfo));
		}*/
		commdityMessageService.sendPureText("test.......");
		//context.start();
		Thread.currentThread().join();
		/*for(int i=0;i<cms.size();i++){
			CmCtxInfo cinfo = cms.get(i);
			deliverMessageService.sendPureText("00010001"+JsonHelper.getJSONString(cinfo));
		}
		
		CmCtxInfo cinfo = new CmCtxInfo("cm003","1");
		deliverMessageService.sendPureText("00010000"+JsonHelper.getJSONString(cinfo));
		deliverMessageService.sendPureText("00010001"+JsonHelper.getJSONString(cinfo));
		*/
	}
}
