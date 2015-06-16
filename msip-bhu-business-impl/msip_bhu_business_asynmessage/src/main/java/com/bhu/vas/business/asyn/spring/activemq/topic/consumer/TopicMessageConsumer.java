package com.bhu.vas.business.asyn.spring.activemq.topic.consumer;

import com.bhu.vas.business.logger.BusinessStatisticsLogger;
import com.bhu.vas.business.observer.QueueMsgObserverManager;

/**
 * Date: 2008-8-28
 * Time: 17:10:34
 */
public class TopicMessageConsumer {

	/**
	 * 
	 */
    public void receive(final String message) {
    	//contain.st
    	//long t0 = System.currentTimeMillis();
    	//System.out.println(message);
    	BusinessStatisticsLogger.doActionTopicMessageLog(message);
    	QueueMsgObserverManager.SpringTopicMessageObserver.notifyMsgComming(message);
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
}
