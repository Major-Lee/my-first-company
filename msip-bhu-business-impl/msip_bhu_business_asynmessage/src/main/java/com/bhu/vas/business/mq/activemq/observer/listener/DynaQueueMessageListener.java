package com.bhu.vas.business.mq.activemq.observer.listener;

/**
 * 动态Queue中过来的消息
 * @author Edmond
 *
 */
public abstract interface DynaQueueMessageListener {
	public void onMessage(String ctx,String msg);
}
