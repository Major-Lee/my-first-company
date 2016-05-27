package com.bhu.pure.kafka.business.observer.listener;

/**
 * 动态Queue中过来的消息
 * @author Edmond
 *
 */
public abstract interface DynaMessageListener {
	public void onMessage(String topic,int partition,String key,String payload,long offset,String consumerId);
}
