package com.bhu.vas.business.mq.activemq;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.mq.activemq.observer.QueueMsgObserverManager;
import com.bhu.vas.business.mq.activemq.observer.listener.QueueMessageListener;

@Service
public class ActiveMQDynamicService implements QueueMessageListener{
	@PostConstruct
	public void initialize(){
		QueueMsgObserverManager.MsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		ActiveMQConnectionManager.getInstance();//(this);
	}
	
	@Override
	public void onMessage(String ctx, String msg) {
		// TODO Auto-generated method stub
		System.out.println(ctx+"::::"+msg);
	}
}
