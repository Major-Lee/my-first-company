package com.bhu.vas.business.mq.activemq.observer.listener;

import com.bhu.vas.api.dto.CmInfo;

public abstract interface CmMessageListener {
	public void onCmOnline(CmInfo info);
	public void onCmOffline(CmInfo info);
}
