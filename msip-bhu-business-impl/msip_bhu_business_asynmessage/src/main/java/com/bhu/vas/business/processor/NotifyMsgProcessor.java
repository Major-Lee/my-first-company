package com.bhu.vas.business.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotifyMsgProcessor {
	private final Logger logger = LoggerFactory.getLogger(NotifyMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(5);
	
	public void handler(final String message){
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info("NotifyMsgProcessorService receive:"+message);
					System.out.println("NotifyMsgProcessorService receive:"+message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));
	}
}
