package com.bhu.vas.processor.bulogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;


public class DynamicLogWriter {
	private static final Logger logger = LoggerFactory.getLogger(DynamicLogWriter.class);

/*	public static void main(String[] args) throws InterruptedException {
//		logger.info(MarkerFactory.getMarker("h1"),"1");
//		logger.info(MarkerFactory.getMarker("h2"),"2");
//		logger.info(MarkerFactory.getMarker("h3"),"3");
		Thread thread1 = new LogThread();
		Thread thread2 = new LogThread();
		Thread thread3 = new LogThread();
		thread1.start();
		thread2.start();
		thread3.start();
		Thread.sleep(10000l);
	}*/
	
	public static void doLogger(String mac,String message){
		logger.info(MarkerFactory.getMarker(String.format("%04d",HashAlgorithmsHelper.rotatingHash(mac, 10))),message);
	}
}

/*class LogThread extends Thread{
//	private String value;
//	public LogThread(String hashid){
//		MDC.put("hashid", hashid);
//		value = hashid;
//	}
	@Override
	public void run(){
		System.out.println(1);
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
}*/


