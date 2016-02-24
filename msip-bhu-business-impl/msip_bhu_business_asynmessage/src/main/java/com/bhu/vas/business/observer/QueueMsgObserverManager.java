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
		/*public static void notifyCmOnline(CmInfo cmInfo){
			logger.info(String.format("notifyCmOnline msg[%s]", cmInfo));
	    	for(CmMessageListener listener:cmMessageListeners){
	    		listener.onCmOnline(cmInfo);
	    	}
		}
		
		
		public static void notifyCmOffline(CmInfo cmInfo){
			logger.info(String.format("notifyCmOffline msg[%s]", cmInfo));
	    	for(CmMessageListener listener:cmMessageListeners){
	    		listener.onCmOffline(cmInfo);
	    	}
		}*/
	}
	
	
	private static List<SpringTopicMessageListener> springTopicMessageListeners = new CopyOnWriteArrayList<SpringTopicMessageListener>();

	public static class SpringTopicMessageObserver{
		public static void addSpringTopicMessageListener(SpringTopicMessageListener listener) {
			springTopicMessageListeners.add(listener);
	    }
		
		public static void removeSpringTopicMessageListener(SpringTopicMessageListener listener) {
			springTopicMessageListeners.remove(listener);
	    }
		public static void notifyMsgComming(String msg){
			//logger.info(String.format("notifyTopicMsgComming msg[%s]",msg));
			//System.out.println(String.format("notifyTopicMsgComming msg[%s]",msg));
	    	for(SpringTopicMessageListener listener:springTopicMessageListeners){
	    		listener.onMessage(msg);
	    	}
		}
	}
}
