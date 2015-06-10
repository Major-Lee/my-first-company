/*package com.bhu.vas.business.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.mq.activemq.observer.QueueMsgObserverManager;
import com.bhu.vas.business.mq.activemq.observer.listener.DynaQueueMessageListener;

@Service
public class DynaMsgProcessor implements DynaQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(DynaMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(5);
	@PostConstruct
	public void initialize(){
		QueueMsgObserverManager.DynaMsgCommingObserver.addMsgCommingListener(this);
	}

	@Override
	public void onMessage(final String ctx,final String message) {
		System.out.println("BusinessNotifyMsgProcessor receive:"+ctx+"~~~~"+message);
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info("BusinessNotifyMsgProcessor receive:"+ctx+"~~~~"+message);
					String type = message.substring(0, 8);
					String payload = message.substring(8);
					System.out.println("BusinessNotifyMsgProcessor receive type:"+type+" payload:"+payload);
					QueueMsgObserverManager.BusinessMessageObserver.notifyMsgComming(ctx, message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));
	}
}
*/