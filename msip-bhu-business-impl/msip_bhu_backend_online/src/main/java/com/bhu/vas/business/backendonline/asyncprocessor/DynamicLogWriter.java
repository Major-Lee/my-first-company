package com.bhu.vas.business.backendonline.asyncprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;


public class DynamicLogWriter {
	public static final Logger logger = LoggerFactory.getLogger(DynamicLogWriter.class);

	public static void main(String[] args) {
//		logger.info(MarkerFactory.getMarker("h1"),"1");
//		logger.info(MarkerFactory.getMarker("h2"),"2");
//		logger.info(MarkerFactory.getMarker("h3"),"3");
		Thread thread1 = new LogThread();
		Thread thread2 = new LogThread();
		Thread thread3 = new LogThread();
		thread1.start();
		thread2.start();
		thread3.start();
	}
	

}

class LogThread extends Thread{
//	private String value;
//	public LogThread(String hashid){
//		MDC.put("hashid", hashid);
//		value = hashid;
//	}
	@Override
	public void run(){
		for(int i=0;i<10;i++){
			DynamicLogWriter.logger.info(MarkerFactory.getMarker(Thread.currentThread().getName()),Thread.currentThread().getName());
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}


