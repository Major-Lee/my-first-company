package com.bhu.vas.business.backendsocial;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.backendsocial.plugins.hook.ShutdownHookThread;

public class BackendSocialMain {
	public static void main(String[] args) throws InterruptedException {
		//String[] locations = {"classpath*:/springtest/testCtx.xml"};//,"classpath:springmq/applicationContext-activemq-server.xml", "classpath:springmq/applicationContext-activemq-message-consumer.xml"};
		String[] locations = {"classpath*:/spring/appCtxBackend.xml"};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		
//		AsyncMsgBackendProcessor service = (AsyncMsgBackendProcessor)ctx.getBean("asyncMsgBackendProcessor");
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(ctx));
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
