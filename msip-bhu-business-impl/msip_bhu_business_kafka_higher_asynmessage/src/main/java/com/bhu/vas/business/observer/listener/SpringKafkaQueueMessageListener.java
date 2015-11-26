package com.bhu.vas.business.observer.listener;


public abstract interface SpringKafkaQueueMessageListener {
	public void onMessage(String msg);
}
