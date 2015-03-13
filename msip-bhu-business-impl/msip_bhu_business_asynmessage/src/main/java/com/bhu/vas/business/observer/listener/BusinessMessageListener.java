package com.bhu.vas.business.observer.listener;

/**
 * 动态Queue中过来的消息
 * @author Edmond
 *
 */
public abstract interface BusinessMessageListener {
	public void onMessage(String ctx,String msg);
}
