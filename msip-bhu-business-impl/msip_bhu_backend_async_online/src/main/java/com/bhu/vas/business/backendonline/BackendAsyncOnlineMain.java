package com.bhu.vas.business.backendonline;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.business.backendonline.plugins.hook.ShutdownHookThread;

public class BackendAsyncOnlineMain {
	public static void main(String[] args) throws InterruptedException {
		//String[] locations = {"classpath*:/springtest/testCtx.xml"};//,"classpath:springmq/applicationContext-activemq-server.xml", "classpath:springmq/applicationContext-activemq-message-consumer.xml"};
		/*String[] locations = {"classpath*:/spring/appCtxBackend.xml"};
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		context.start();*/
		String[] CONFIG = {"/spring/appCtxBackend.xml"};
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG, BackendAsyncOnlineMain.class);
		context.start();
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(context));
	}
}
