package com.bhu.vas.business.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BusinessNotifyMsgProcessor {
	private final Logger logger = LoggerFactory.getLogger(BusinessNotifyMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(50);
	
	public void handler(final String ctx,final String message){
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info("BusinessNotifyMsgProcessor receive:"+ctx+"~~~~"+message);
					System.out.println("BusinessNotifyMsgProcessor receive:"+message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));
	}
}
