package com.bhu.commdity.business.backendcommdity.plugins;
/*package com.bhu.vas.business.plugins;

import java.util.concurrent.ExecutorService;

import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.smartwork.msip.cores.commandserver.ICommandListener;
import com.bhu.vas.business.asyn.web.activemq.queue.consumer.DeliverMessageQueueConsumer;

public class BackendCommandListener implements ICommandListener{
	private DefaultMessageListenerContainer container;
	private DeliverMessageQueueConsumer consumer;
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

	public DefaultMessageListenerContainer getContainer() {
		return container;
	}

	public void setContainer(DefaultMessageListenerContainer container) {
		this.container = container;
	}

	public DeliverMessageQueueConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(DeliverMessageQueueConsumer consumer) {
		this.consumer = consumer;
	}
	
	
}
final class DaemonExecRunnable implements Runnable{
	private ExecutorService exec;
	public DaemonExecRunnable(ExecutorService _exec){
		this.exec = _exec;
	}
	@Override
	public void run() {
		while(true){
			if(exec.isTerminated()){
				System.exit(0);
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}*/