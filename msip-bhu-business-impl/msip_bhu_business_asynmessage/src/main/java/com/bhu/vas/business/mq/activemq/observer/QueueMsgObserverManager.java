package com.bhu.vas.business.mq.activemq.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.rpc.devices.dto.CmInfo;
import com.bhu.vas.business.mq.activemq.observer.listener.CmMessageListener;
import com.bhu.vas.business.mq.activemq.observer.listener.DynaQueueMessageListener;

public class QueueMsgObserverManager {
	private static final Logger logger = LoggerFactory.getLogger(QueueMsgObserverManager.class);
	
	private static List<DynaQueueMessageListener> msgCommingListeners = new CopyOnWriteArrayList<DynaQueueMessageListener>();

	public static class DynaMsgCommingObserver{
		public static void addMsgCommingListener(DynaQueueMessageListener listener) {
			msgCommingListeners.add(listener);
	    }
		
		public static void removeMsgCommingListener(DynaQueueMessageListener listener) {
			msgCommingListeners.remove(listener);
	    }
		public static void notifyMsgComming(String ctx,String msg){
			logger.info(String.format("notifyMsgComming ctx[%s] msg[%s]", ctx,msg));
	    	for(DynaQueueMessageListener listener:msgCommingListeners){
	    		listener.onMessage(ctx,msg);
	    	}
		}
	}
	
	
	private static List<CmMessageListener> cmMessageListeners = new CopyOnWriteArrayList<CmMessageListener>();

	public static class CmMessageObserver{
		public static void addCmMessageListener(CmMessageListener listener) {
			cmMessageListeners.add(listener);
	    }
		
		public static void removeCmMessageListener(CmMessageListener listener) {
			cmMessageListeners.remove(listener);
	    }
		
		public static void notifyCmOnline(CmInfo cmInfo){
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
		}
	}
}
