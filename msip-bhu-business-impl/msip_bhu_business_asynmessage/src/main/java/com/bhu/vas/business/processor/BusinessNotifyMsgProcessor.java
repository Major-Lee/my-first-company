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
					String type = message.substring(0, 8);
					String payload = message.substring(8);
					System.out.println("BusinessNotifyMsgProcessor receive type:"+type+" payload:"+payload);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));
	}
}
