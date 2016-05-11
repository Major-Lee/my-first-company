package com.bhu.vas.business.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bhu.vas.business.observer.listener.DynaQueueMessageListener;
import com.bhu.vas.business.observer.listener.SpringQueueMessageListener;
import com.bhu.vas.business.observer.listener.SpringTopicMessageListener;

public class QueueMsgObserverManager {
	//private static final Logger logger = LoggerFactory.getLogger(QueueMsgObserverManager.class);
	
	private static List<DynaQueueMessageListener> dynaMsgCommingListeners = new CopyOnWriteArrayList<DynaQueueMessageListener>();

	public static class DynaMsgCommingObserver{
		public static void addMsgCommingListener(DynaQueueMessageListener listener) {
			dynaMsgCommingListeners.add(listener);
	    }
		
		public static void removeMsgCommingListener(DynaQueueMessageListener listener) {
			dynaMsgCommingListeners.remove(listener);
	    }
		public static void notifyMsgComming(String ctx,String msg){
			//logger.info(String.format("notifyMsgComming ctx[%s] msg[%s]", ctx,msg));
	    	for(DynaQueueMessageListener listener:dynaMsgCommingListeners){
	    		listener.onMessage(ctx,msg);
	    	}
		}
	}
	
	private static List<SpringQueueMessageListener> spingQueueAsyncMessageListeners = new CopyOnWriteArrayList<SpringQueueMessageListener>();

	public static class SpringQueueAsyncMessageObserver{
		public static void addSpringQueueMessageListener(SpringQueueMessageListener listener) {
			spingQueueAsyncMessageListeners.add(listener);
	    }
		
		public static void removeSpringQueueMessageListener(SpringQueueMessageListener listener) {
			spingQueueAsyncMessageListeners.remove(listener);
	    }
		public static void notifyMsgComming(String msg){
			//logger.info(String.format("notifyQueueMsgComming msg[%s]",msg));
	    	for(SpringQueueMessageListener listener:spingQueueAsyncMessageListeners){
	    		listener.onMessage(msg);
	    	}
		}
	}
	
	private static List<SpringQueueMessageListener> spingQueueMessageListeners = new CopyOnWriteArrayList<SpringQueueMessageListener>();

	public static class SpringQueueMessageObserver{
		public static void addSpringQueueMessageListener(SpringQueueMessageListener listener) {
			spingQueueMessageListeners.add(listener);
	    }
		
		public static void removeSpringQueueMessageListener(SpringQueueMessageListener listener) {
			spingQueueMessageListeners.remove(listener);
	    }
		public static void notifyMsgComming(String msg){
			//logger.info(String.format("notifyQueueMsgComming msg[%s]",msg));
	    	for(SpringQueueMessageListener listener:spingQueueMessageListeners){
	    		listener.onMessage(msg);
	    	}
		}
	}
	
	
	private static List<SpringTopicMessageListener> spingTopicMessageListeners = new CopyOnWriteArrayList<SpringTopicMessageListener>();

	public static class SpringTopicMessageObserver{
		public static void addSpringTopicMessageListener(SpringTopicMessageListener listener) {
			spingTopicMessageListeners.add(listener);
	    }
		
		public static void removeSpringTopicMessageListener(SpringTopicMessageListener listener) {
			spingTopicMessageListeners.remove(listener);
	    }
		public static void notifyMsgComming(String msg){
			//logger.info(String.format("notifyTopicMsgComming msg[%s]",msg));
			//System.out.println(String.format("notifyTopicMsgComming msg[%s]",msg));
	    	for(SpringTopicMessageListener listener:spingTopicMessageListeners){
	    		listener.onMessage(msg);
	    	}
		}
	}
}
