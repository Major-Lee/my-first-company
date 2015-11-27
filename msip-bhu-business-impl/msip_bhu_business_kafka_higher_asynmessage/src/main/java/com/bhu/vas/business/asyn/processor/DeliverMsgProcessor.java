package com.bhu.vas.business.asyn.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.SpringKafkaQueueMessageListener;

@Service
public class DeliverMsgProcessor implements SpringKafkaQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(DeliverMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(2);
	@PostConstruct
	public void initialize(){
		logger.info("DeliverMsgProcessor initialize...");
		QueueMsgObserverManager.SpringKafkaQueueMessageObserver.addSpringKafkaQueueMessageListener(this);
	}
	public void onMessage(final String message){
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info("DeliverMsgProcessor receive:"+message);
					//System.out.println("DeliverMsgProcessor receive:"+message);
					//System.out.println("DeliverMsgProcessor receive type:"+type+" payload:"+payload);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMsgProcessor", ex);
				}
			}
		}));
	}
}
