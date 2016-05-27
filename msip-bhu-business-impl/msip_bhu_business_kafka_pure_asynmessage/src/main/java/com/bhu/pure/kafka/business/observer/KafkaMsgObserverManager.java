package com.bhu.pure.kafka.business.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bhu.pure.kafka.business.observer.listener.DynaMessageListener;

public class KafkaMsgObserverManager {
	private static List<DynaMessageListener> dynaMsgCommingListeners = new CopyOnWriteArrayList<DynaMessageListener>();

	public static class DynaMsgCommingObserver{
		public static void addMsgCommingListener(DynaMessageListener listener) {
			dynaMsgCommingListeners.add(listener);
	    }
		
		public static void removeMsgCommingListener(DynaMessageListener listener) {
			dynaMsgCommingListeners.remove(listener);
	    }
		public static void notifyMsgComming(String topic,int partition,String key,String payload,long offset,String consumerId){
			//logger.info(String.format("notifyMsgComming ctx[%s] msg[%s]", ctx,msg));
	    	for(DynaMessageListener listener:dynaMsgCommingListeners){
	    		listener.onMessage(topic, partition, key, payload, offset, consumerId);
	    	}
		}
	}
	
	
	private static List<DynaMessageListener> cmNotifyCommingListeners = new CopyOnWriteArrayList<DynaMessageListener>();

	public static class CMNotifyCommingObserver{
		public static void addMsgCommingListener(DynaMessageListener listener) {
			cmNotifyCommingListeners.add(listener);
	    }
		
		public static void removeMsgCommingListener(DynaMessageListener listener) {
			cmNotifyCommingListeners.remove(listener);
	    }
		public static void notifyMsgComming(String topic,int partition,String key,String payload,long offset,String consumerId){
	    	for(DynaMessageListener listener:cmNotifyCommingListeners){
	    		listener.onMessage(topic, partition, key, payload, offset, consumerId);
	    	}
		}
	}
}
