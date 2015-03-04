package com.bhu.vas.business.asyn.web.activemq.queue.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.logger.BusinessStatisticsLogger;

/**
 * Date: 2008-8-28
 * Time: 17:10:34
 */
public class DeliverMessageQueueConsumer {
	private final Logger logger = LoggerFactory.getLogger(DeliverMessageQueueConsumer.class);
	private ExecutorService exec = Executors.newFixedThreadPool(50);
	
	public ExecutorService getExec() {
		return exec;
	}

	/**
	 * 
	 */
    public void receive(final String message) {
    	//contain.st
    	//long t0 = System.currentTimeMillis();
    	//logger.info(message);
    	BusinessStatisticsLogger.doActionMessageLog(message);
    	exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info("receive:"+message);
					System.out.println("receive:"+message);
					/*Thread.sleep(1000);
					System.out.println("receive:"+message);*/
					/*DeliverMessage deliverMsg = DeliverMessageFactoryBuilder.fromJson(message);
			    	switch(deliverMsg.getType()){
			    		case 'A':
			    			processActionMessage(deliverMsg);
			    			break;
			    	}*/
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));
    	//System.out.println("********* DeliverMessageQueueConsumer : cost:" + (System.currentTimeMillis() - t0));
	}
    
    /*public void processActionMessage(DeliverMessage deliverMsg){
    	String messageDataHasPrefix = deliverMsg.getMessagedata();
    	ActionMessageType actType = ActionMessageFactoryBuilder.determineActionType(messageDataHasPrefix);
    	if(actType == null)
    		throw new UnsupportedOperationException(messageDataHasPrefix+" message can not be parsed!");
    	String message = ActionMessageFactoryBuilder.determineActionMessage(messageDataHasPrefix);
    	switch(actType){
    		case REGISTERED:
    			UserRegisteredActHandler.getInstance().handlerMessage(message);
    			break;
    		case USERSUBJECTCLICK:
    			UserSubjectClickActHandler.getInstance().handlerMessage(message);
    			break;
    		case USERSUBJECTABSTRACTCLICK:
    			UserSubjectAbstractClickActHandler.getInstance().handlerMessage(message);
    			break;
    		case USERSUBJECTESTIMATE:
    			UserSubjectEstimateActHandler.getInstance().handlerMessage(message);
    			break;
    		case USERSUBJECTTAGGING:
    			UserSubjectTaggingActHandler.getInstance().handlerMessage(message);
    			break;	
    		case USERSUBJECTSHARE:
    			UserSubjectShareActHandler.getInstance().handlerMessage(message);
    			break;
    		case USERBLACKDOMAIN:
    			UserBlackDomainActHandler.getInstance().handlerMessage(message);
    			break;
    		default:
    			throw new UnsupportedOperationException(actType.getCname()+" message not yet implement handler process!");
    	}
    }*/
}
