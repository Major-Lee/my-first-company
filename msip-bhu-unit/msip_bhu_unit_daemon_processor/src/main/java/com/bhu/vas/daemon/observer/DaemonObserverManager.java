package com.bhu.vas.daemon.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.daemon.observer.listener.CmdDownListener;

public class DaemonObserverManager {
	private static final Logger logger = LoggerFactory.getLogger(DaemonObserverManager.class);
	
	private static List<CmdDownListener> cmdDownListeners = new CopyOnWriteArrayList<CmdDownListener>();

	public static class CmdDownObserver{
		public static void addCmdDownListener(CmdDownListener listener) {
			cmdDownListeners.add(listener);
	    }
		
		public static void removeCmdDownListener(CmdDownListener listener) {
			cmdDownListeners.remove(listener);
	    }
		public static void notifyCmdDown(String ctx,String mac,String cmd){
			logger.info(String.format("notifyMsgComming ctx[%s] mac[%s] cmd[%s]", ctx,mac,cmd));
	    	for(CmdDownListener listener:cmdDownListeners){
	    		listener.wifiDeviceCmdDown(ctx, mac, cmd);
	    	}
		}
	}
	
}
