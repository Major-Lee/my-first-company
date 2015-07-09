package com.bhu.vas.business.backendws;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class BackendWifistasnifferMain {
	public static void main(String[] args) throws InterruptedException {
		//String[] locations = {"classpath*:/springtest/testCtx.xml"};//,"classpath:springmq/applicationContext-activemq-server.xml", "classpath:springmq/applicationContext-activemq-message-consumer.xml"};
		String[] locations = {"classpath*:spring/appCtxBackend.xml"};
		/*String[] locations = {
				//"/com/bhu/vas/business/processor/testCtx.xml",
				"classpath*:/spring/applicationContextCore-resource.xml",
				"classpath*:/springws/inbound/applicationContext-InboundKafka-ws-String-AdapterParser.xml",
				"classpath*:/springws/inbound/applicationContext-InboundKafka-ws-String-MessageHandler.xml"
				//"/com/smartwork/async/messagequeue/kafka/inbound/applicationContext-InboundKafka-String-MessageHandler.xml",
				//"/com/smartwork/async/messagequeue/kafka/inbound/applicationContext-InboundKafka-String-AdapterParser.xml"
				};*/
				
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		//ctx.start();
		//ctx.getBean("kafkaInboundChannelAdapter");
		
/*		String[] CONFIG = {
				//"/com/bhu/vas/business/processor/testCtx.xml",
				"/spring/applicationContextCore-resource.xml",
				"/springws/inbound/applicationContext-InboundKafka-ws-String-AdapterParser.xml",
				"/springws/inbound/applicationContext-InboundKafka-ws-String-MessageHandler.xml"
				//"/com/smartwork/async/messagequeue/kafka/inbound/applicationContext-InboundKafka-String-MessageHandler.xml",
				//"/com/smartwork/async/messagequeue/kafka/inbound/applicationContext-InboundKafka-String-AdapterParser.xml"
				};
			
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, BackendWifistasnifferMain.class);
		ctx.start();*/
			//ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
			//ctx.getBean(arg0);
			//Thread.sleep(1000000000l);
		
		
//		AsyncMsgBackendProcessor service = (AsyncMsgBackendProcessor)ctx.getBean("asyncMsgBackendProcessor");
		//Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(ctx));
//		Thread.sleep(5000);
//		Object obj = ctx.getBean("remoteCommandServer");
//		System.out.println(obj);
		
		/*		final DefaultMessageListenerContainer container = (DefaultMessageListenerContainer) ctx.getBean("deliverMessageQueueListenerContainer");
		final DeliverMessageQueueConsumer consumer = (DeliverMessageQueueConsumer) ctx.getBean("deliverMessageQueueConsumer");
		if(container != null){
			PooledRemoteCommandServer server = new PooledRemoteCommandServer(4321, 3);
			server.addCommandListener(new ICommandListener(){
				@Override
				public void stop() {
					if(container != null){
						container.stop();
					}
					if(consumer != null){
						if(consumer.getExec() != null){
							consumer.getExec().shutdown();
							new Thread(new DaemonExecRunnable(consumer.getExec())).start();
						}
					}
				}

				@Override
				public void start() {
					if(container != null){
						container.start();
					}
				}
				@Override
				public void bye() {}
			});
			//server.setListenerContainer(container);
	        // 初始化线程池
	        server.setUpHandlers();
	        // 开始在指定端口等待到来的请求
	        server.acceptConnections();
		}*/
	}
}
