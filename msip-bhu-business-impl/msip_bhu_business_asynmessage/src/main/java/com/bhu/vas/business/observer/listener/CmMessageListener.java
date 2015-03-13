package com.bhu.vas.business.observer.listener;


public abstract interface CmMessageListener {
	/*public void onCmOnline(CmInfo info);
	public void onCmOffline(CmInfo info);*/
	public void onMessage(String msg);
}
