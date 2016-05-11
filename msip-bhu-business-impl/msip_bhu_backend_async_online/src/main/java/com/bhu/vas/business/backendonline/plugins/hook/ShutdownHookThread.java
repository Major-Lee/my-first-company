package com.bhu.vas.business.backendonline.plugins.hook;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ShutdownHookThread extends Thread {
	private ClassPathXmlApplicationContext ctx;
	
	public ShutdownHookThread(ClassPathXmlApplicationContext ctx){
		this.ctx = ctx;
	}
	
    public void run() {
    	System.err.println("BackendAsyncOnlineMain Server Hook Starting...");
    	ctx.destroy();
    	ctx.close();
    	/*final DefaultMessageListenerContainer container = (DefaultMessageListenerContainer) ctx.getBean("deliverMessageQueueListenerContainer");
		final DeliverMessageQueueConsumer consumer = (DeliverMessageQueueConsumer) ctx.getBean("deliverMessageQueueConsumer");
		if(container != null){
			System.out.println("DefaultMessageListenerContainer:starting stop...");
			container.stop();
			System.out.println("DefaultMessageListenerContainer:starting stop 成功");
		}
		if(consumer.getExec() != null){
			System.out.println("exec正在shutdown");
			consumer.getExec().shutdown();
			System.out.println("exec正在shutdown成功");
			while(true){
				System.out.println("正在判断exec是否执行完毕");
				if(consumer.getExec().isTerminated()){
					System.out.println("exec是否执行完毕,终止exec...");
					consumer.getExec().shutdownNow();
					System.out.println("exec是否执行完毕,终止exec成功");
					break;
				}else{
					System.out.println("exec未执行完毕...");
					try {
						Thread.sleep(2*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}*/
		System.err.println("BackendAsyncOnlineMain Server halted");
    }

	public ApplicationContext getCtx() {
		return ctx;
	}
    
}
