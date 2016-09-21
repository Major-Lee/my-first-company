package com.bhu.vas.pa.processor.task;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.pa.processor.BusinessDynaMsgProcessor;

public class DaemonProcessesStatusTask extends TimerTask{
	private final Logger logger = LoggerFactory.getLogger(DaemonProcessesStatusTask.class);
	private BusinessDynaMsgProcessor processor;
	
	public DaemonProcessesStatusTask(BusinessDynaMsgProcessor processor){
		this.processor = processor;
	}
	@Override
	public void run() {
		logger.info("DaemonProcessesStatusTask starting...");
		StringBuilder sb = new StringBuilder();
		
		int[] hits = processor.getHits();
		for(int i=0;i<hits.length;i++){
			sb.append(String.format("processor index[%s] hit[%s]\n", i,hits[i]));
		}
		logger.info(sb.toString());
    	//System.out.println(String.format("DaemonProcessesStatusTask ended! Total[%s] Sended[%s] UnSended[%s] notExistMac[%s]",i,sended,unsended,notExistMac));
	}
}
