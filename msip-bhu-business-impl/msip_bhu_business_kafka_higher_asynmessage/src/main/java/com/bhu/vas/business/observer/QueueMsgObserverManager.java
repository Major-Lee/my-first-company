package com.bhu.vas.business.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.observer.listener.SpringKafkaQueueMessageListener;

public class QueueMsgObserverManager {
	private static final Logger logger = LoggerFactory.getLogger(QueueMsgObserverManager.class);
	
	private static List<SpringKafkaQueueMessageListener> spingKafkaQueueMessageListeners = new CopyOnWriteArrayList<SpringKafkaQueueMessageListener>();

	public static class SpringKafkaQueueMessageObserver{
		public static void addSpringKafkaQueueMessageListener(SpringKafkaQueueMessageListener listener) {
			spingKafkaQueueMessageListeners.add(listener);
	    }
		
		public static void removeSpringKafkaQueueMessageListener(SpringKafkaQueueMessageListener listener) {
			spingKafkaQueueMessageListeners.remove(listener);
	    }
		public static void notifyMsgComming(String msg){
			System.out.println(String.format("notifyKafkaMsgComming msg[%s]",msg));
			logger.info(String.format("notifyKafkaMsgComming msg[%s]",msg));
	    	for(SpringKafkaQueueMessageListener listener:spingKafkaQueueMessageListeners){
	    		listener.onMessage(msg);
	    	}
		}
	}
}
