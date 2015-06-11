package com.bhu.vas.business.asyn.kafka.consumer;

import com.bhu.vas.business.logger.BusinessStatisticsLogger;
import com.bhu.vas.business.observer.QueueMsgObserverManager;

/**
 * Date: 2008-8-28
 * Time: 17:10:34
 */
public class KafkaMessageQueueConsumer {

	/**
	 * 
	 */
    public void receive(final String message) {
    	BusinessStatisticsLogger.doActionMessageLog(message);
    	QueueMsgObserverManager.SpringQueueMessageObserver.notifyMsgComming(message);
	}
}
