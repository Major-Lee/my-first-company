package com.bhu.vas.business.asyn.normal.activemq.single;

import javax.annotation.PostConstruct;

public class ActiveMQDynamicConsumer {//implements CmMessageListener{//DynaQueueMessageListener,
	
	//@Resource
	//private ActiveMQDynamicService activeMQDynamicService;
	@PostConstruct
	public void initialize(){
		//QueueMsgObserverManager.CmMessageObserver.addCmMessageListener(this);
		//初始化ActiveMQConnectionManager
		ActiveMQConnectionManager.getInstance().initConsumerQueues();
	}
	
	//@Resource
	//private BusinessNotifyMsgProcessor businessNotifyMsgProcessor;
	
	/*@Override
	public void onMessage(String ctx, String message) {
		businessNotifyMsgProcessor.handler(ctx, message);
	}*/

	/*@Override
	public void onCmOnline(CmInfo info) {
		ActiveMQConnectionManager.getInstance().createNewConsumerQueues("up", info.toString(),true);
	}

	@Override
	public void onCmOffline(CmInfo info) {
		
	}*/
	
	/*public void stop(){
		ActiveMQConnectionManager.getInstance().stop();
		ActiveMQConnectionManager.getInstance().destroy();
	}*/
}
