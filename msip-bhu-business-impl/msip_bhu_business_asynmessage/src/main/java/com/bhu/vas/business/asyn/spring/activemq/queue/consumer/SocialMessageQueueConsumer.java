package com.bhu.vas.business.asyn.spring.activemq.queue.consumer;

import com.bhu.vas.business.logger.BusinessStatisticsLogger;
import com.bhu.vas.business.observer.QueueMsgObserverManager;

/**
 * Date: 2008-8-28
 * Time: 17:10:34
 */
public class SocialMessageQueueConsumer {
	//private final Logger logger = LoggerFactory.getLogger(DeliverMessageQueueConsumer.class);
	//@Resource
	//private DeliverMsgProcessor deliverMsgProcessor;
	/*private ExecutorService exec = Executors.newFixedThreadPool(5);
	public ExecutorService getExec() {
		return exec;
	}*/

	/**
	 * 
	 */
    public void receive(final String message) {
    	//contain.st
    	//long t0 = System.currentTimeMillis();
    	//System.out.println(message);
    	BusinessStatisticsLogger.doActionQueueMessageLog(message);
    	QueueMsgObserverManager.SpringQueueMessageObserver.notifyMsgComming(message);
    	//deliverMsgProcessor.onMessage(message);
    	/*exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info("receive:"+message);
					System.out.println("receive:"+message);
					DeliverMessage deliverMsg = DeliverMessageFactoryBuilder.fromJson(message);
			    	switch(deliverMsg.getType()){
			    		case 'A':
			    			processActionMessage(deliverMsg);
			    			break;
			    	}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));*/
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
