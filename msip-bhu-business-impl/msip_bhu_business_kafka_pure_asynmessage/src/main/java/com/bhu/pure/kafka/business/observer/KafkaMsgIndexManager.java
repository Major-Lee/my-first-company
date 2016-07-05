package com.bhu.pure.kafka.business.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.pure.kafka.business.observer.listener.DynaMessageListener;

public class KafkaMsgIndexManager {
	private static final Logger logger = LoggerFactory.getLogger(KafkaMsgIndexManager.class);
	private static List<DynaMessageListener> incrementMsgCommingListeners = new CopyOnWriteArrayList<DynaMessageListener>();

	public static class IncrementMsgCommingObserver{
		public static void addMsgCommingListener(DynaMessageListener listener) {
			incrementMsgCommingListeners.add(listener);
			logger.info(String.format("IncrementMsgComming[%s] Index register ok", listener.getClass().getSimpleName()));
	    }
		
		public static void removeMsgCommingListener(DynaMessageListener listener) {
			incrementMsgCommingListeners.remove(listener);
	    }
		
		public static void notifyMsgComming(String topic,int partition,String key,String payload,long offset,String consumerId){
			//logger.info(String.format("notifyMsgComming ctx[%s] msg[%s]", ctx,msg));
	    	for(DynaMessageListener listener:incrementMsgCommingListeners){
	    		listener.onMessage(topic, partition, key, payload, offset, consumerId);
	    	}
		}
	}
	
	
	private static List<DynaMessageListener> incrementPerformCommingListeners = new CopyOnWriteArrayList<DynaMessageListener>();

	public static class IncrementPerformMsgCommingObserver{
		public static void addMsgCommingListener(DynaMessageListener listener) {
			incrementPerformCommingListeners.add(listener);
			logger.info(String.format("IncrementPerform[%s] Index register ok", listener.getClass().getSimpleName()));
	    }
		
		public static void removeMsgCommingListener(DynaMessageListener listener) {
			incrementPerformCommingListeners.remove(listener);
	    }
		
		public static void notifyMsgComming(String topic,int partition,String key,String payload,long offset,String consumerId){
	    	for(DynaMessageListener listener:incrementPerformCommingListeners){
	    		listener.onMessage(topic, partition, key, payload, offset, consumerId);
	    	}
		}
	}
}
