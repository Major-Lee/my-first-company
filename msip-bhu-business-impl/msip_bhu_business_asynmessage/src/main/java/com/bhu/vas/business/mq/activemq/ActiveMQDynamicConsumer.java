package com.bhu.vas.business.mq.activemq;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.bhu.vas.business.mq.activemq.observer.QueueMsgObserverManager;
import com.bhu.vas.business.mq.activemq.observer.listener.QueueMessageListener;
import com.bhu.vas.business.processor.BusinessNotifyMsgProcessor;

public class ActiveMQDynamicConsumer implements QueueMessageListener{
	
	@Resource
	private ActiveMQDynamicService activeMQDynamicService;
	@PostConstruct
	public void initialize(){
		QueueMsgObserverManager.MsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		ActiveMQConnectionManager.getInstance().initConsumerQueues();
	}
	
	@Resource
	private BusinessNotifyMsgProcessor businessNotifyMsgProcessor;
	
	@Override
	public void onMessage(String ctx, String message) {
		businessNotifyMsgProcessor.handler(ctx, message);
	}
	
	/*public void stop(){
		ActiveMQConnectionManager.getInstance().stop();
		ActiveMQConnectionManager.getInstance().destroy();
	}*/
}
