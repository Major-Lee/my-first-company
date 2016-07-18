package com.bhu.vas.business.backend.terminalstatus;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service.BusinessHelper;
import com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service.Step00ReadSimulateLogService;
import com.smartwork.msip.plugins.hook.ShutdownHookThread;

public class BackendTerminalStatusMain {
	public static void main(String[] args) throws InterruptedException {
		//String[] locations = {"classpath*:/springtest/testCtx.xml"};//,"classpath:springmq/applicationContext-activemq-server.xml", "classpath:springmq/applicationContext-activemq-message-consumer.xml"};
		//String[] locations = {"classpath*:/spring/appCtxBackend.xml"};
		//ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		String[] CONFIG = {"/spring/appCtxBackend.xml"};
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG, BackendTerminalStatusMain.class);
		context.start();
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(context,"BackendTerminalStatusMain Server"));
		
		
		//BusinessCacheService businessCacheService = (BusinessCacheService)context.getBean("businessCacheService");
		Step00ReadSimulateLogService step00ReadSimulateLogService = (Step00ReadSimulateLogService)context.getBean("step00ReadSimulateLogService");
		
		String path1 = "/BHUData/bulogs/reportinglogsnew/i1";
		String path2 = "/BHUData/bulogs/reportinglogsnew/i2";

//		String path1 = "/BHUData/bulogs/reportinglogsnew/i1";
//		String path2 = "/BHUData/bulogs/reportinglogsnew/i2";
////		String path1 = "E:/onORoff";
//		String path2 = "E:/onORoff/i2";
		
		String date = BusinessHelper.getCurrentPreviousMinuteString(1);
		
		step00ReadSimulateLogService.parser(date, path1);
		step00ReadSimulateLogService.parser(date, path2);
		
		//OnlineOfflineDataParserOp op = new OnlineOfflineDataParserOp();
//		for (int i = 28; i >= 0; i--) {
//			
////			op.perdayDataGen(path1,date);
////			op.perdayDataGen(path2,date);
//		}
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
