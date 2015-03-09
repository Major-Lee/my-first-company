package com.bhu.vas.business.mq.activemq.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.mq.activemq.observer.listener.QueueMessageListener;

public class QueueMsgObserverManager {
	private static final Logger logger = LoggerFactory.getLogger(QueueMsgObserverManager.class);
	
	private static List<QueueMessageListener> msgCommingListeners = new CopyOnWriteArrayList<QueueMessageListener>();

	public static class MsgCommingObserver{
		public static void addMsgCommingListener(QueueMessageListener listener) {
			msgCommingListeners.add(listener);
	    }
		
		public static void removeMsgCommingListener(QueueMessageListener listener) {
			msgCommingListeners.remove(listener);
	    }
		public static void notifyMsgComming(String ctx,String msg){
			logger.info(String.format("notifyMsgComming ctx[%s] msg[%s]", ctx,msg));
	    	for(QueueMessageListener listener:msgCommingListeners){
	    		listener.onMessage(ctx,msg);
	    	}
		}
		/*public static void notifyUserPresentOnline(String user){
			logger.info(String.format("notifyUserPresentOnline user[%s]", user));
	    	for(UserPresentListener listener:userOnlineListeners){
	    		listener.onPresentOnline(user);
	    	}
		}
		public static void notifyUserPresentOffline(String user){
			logger.info(String.format("notifyUserPresentOffline user[%s]", user));
	    	for(UserPresentListener listener:userOnlineListeners){
	    		listener.onPresentOffline(user);
	    	}
		}*/
	}
}
