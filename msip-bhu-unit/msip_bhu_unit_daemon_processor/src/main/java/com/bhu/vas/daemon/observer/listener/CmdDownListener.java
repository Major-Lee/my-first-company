package com.bhu.vas.daemon.observer.listener;

/**
 * 动态Queue中过来的消息
 * @author Edmond
 *
 */
public abstract interface CmdDownListener {
	public boolean wifiDeviceCmdDown(String ctx,String mac, String cmd);
}
