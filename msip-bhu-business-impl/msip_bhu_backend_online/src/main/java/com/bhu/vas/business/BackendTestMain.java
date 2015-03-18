package com.bhu.vas.business;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class BackendTestMain {
	public static void main(String[] args) throws InterruptedException {
		//String[] locations = {"classpath*:/springtest/testCtx.xml"};//,"classpath:springmq/applicationContext-activemq-server.xml", "classpath:springmq/applicationContext-activemq-message-consumer.xml"};
		String[] locations = {"classpath*:/spring/appCtxBackend.xml"};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		
		/*DeliverMessageService deliverMessageService =(DeliverMessageService) ctx.getBean("deliverMessageService");
		
		deliverMessageService.sendUserRegisteredActionMessage(DeliverMessageType.AC.getPrefix(), 1001, "wandojia", "ADR", "192.168.0.1");
		
		long ts = System.currentTimeMillis();
		for(int j=0;j<10;j++){
			for(int i=0;i<20000;i++){
				deliverMessageService.sendUserRegisteredActionMessage(DeliverMessageType.AC.getPrefix(), 1001, "wandojia", "ADR", "192.168.0.1");
			}
		}
		long cost = System.currentTimeMillis() - ts;
		System.out.println("cost:"+cost/1000);*/
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
