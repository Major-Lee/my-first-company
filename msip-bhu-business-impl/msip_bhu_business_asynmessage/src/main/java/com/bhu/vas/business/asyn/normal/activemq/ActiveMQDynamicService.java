package com.bhu.vas.business.asyn.normal.activemq;

import javax.annotation.PostConstruct;

//@Service
public class ActiveMQDynamicService{// implements QueueMessageListener{
	@PostConstruct
	public void initialize(){
		//QueueMsgObserverManager.MsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		ActiveMQConnectionManager.getInstance();//(this);
	}
	
	//@Resource
	//private BusinessNotifyMsgProcessor businessNotifyMsgProcessor;
	
	/*@Override
	public void onMessage(String ctx, String message) {
		businessNotifyMsgProcessor.handler(ctx, message);
	}*/
	
	public void stop(){
		ActiveMQConnectionManager.getInstance().stop();
		ActiveMQConnectionManager.getInstance().destroy();
	}
}
