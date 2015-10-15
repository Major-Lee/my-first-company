package com.bhu.vas.business.asyn.normal.activemq.multi;

import javax.annotation.PostConstruct;

//@Service
public class ActiveMQDynamicsService{// implements QueueMessageListener{
	@PostConstruct
	public void initialize(){
		//QueueMsgObserverManager.MsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		ActiveMQConnectionsManager.getInstance();//(this);
	}
	
	//@Resource
	//private BusinessNotifyMsgProcessor businessNotifyMsgProcessor;
	
	/*@Override
	public void onMessage(String ctx, String message) {
		businessNotifyMsgProcessor.handler(ctx, message);
	}*/
	
	public void stop(){
		ActiveMQConnectionsManager.getInstance().stop();
		ActiveMQConnectionsManager.getInstance().destroy();
	}
}
