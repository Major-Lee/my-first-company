package com.bhu.vas.business.mq.activemq.observer.listener;

public abstract interface DynaQueueMessageListener {
	public void onMessage(String ctx,String msg);
}
