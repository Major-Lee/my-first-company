package com.bhu.vas.business.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.CmInfo;
import com.bhu.vas.business.observer.listener.BusinessMessageListener;
import com.bhu.vas.business.observer.listener.CmMessageListener;
import com.bhu.vas.business.observer.listener.DynaQueueMessageListener;

public class QueueMsgObserverManager {
	private static final Logger logger = LoggerFactory.getLogger(QueueMsgObserverManager.class);
	
	private static List<DynaQueueMessageListener> dynaMsgCommingListeners = new CopyOnWriteArrayList<DynaQueueMessageListener>();

	public static class DynaMsgCommingObserver{
		public static void addMsgCommingListener(DynaQueueMessageListener listener) {
			dynaMsgCommingListeners.add(listener);
	    }
		
		public static void removeMsgCommingListener(DynaQueueMessageListener listener) {
			dynaMsgCommingListeners.remove(listener);
	    }
		public static void notifyMsgComming(String ctx,String msg){
			logger.info(String.format("notifyMsgComming ctx[%s] msg[%s]", ctx,msg));
	    	for(DynaQueueMessageListener listener:dynaMsgCommingListeners){
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
		public static void notifyMsgComming(String msg){
			logger.info(String.format("notifyMsgComming msg[%s]",msg));
	    	for(CmMessageListener listener:cmMessageListeners){
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
	
	
	
	
	private static List<BusinessMessageListener> businessMessageListeners = new CopyOnWriteArrayList<BusinessMessageListener>();

	public static class BusinessMessageObserver{
		public static void addBusinessMessageListener(BusinessMessageListener listener) {
			businessMessageListeners.add(listener);
	    }
		
		public static void removeBusinessMessageListener(CmMessageListener listener) {
			businessMessageListeners.remove(listener);
	    }
		
		public static void notifyMsgComming(String ctx,String msg){
			logger.info(String.format("notifyMsgComming ctx[%s] msg[%s]", ctx,msg));
	    	for(BusinessMessageListener listener:businessMessageListeners){
	    		listener.onMessage(ctx,msg);
	    	}
		}
	}
}
