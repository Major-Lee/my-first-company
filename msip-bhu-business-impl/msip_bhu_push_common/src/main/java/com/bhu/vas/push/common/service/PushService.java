package com.bhu.vas.push.common.service;

import com.bhu.vas.push.common.dto.PushMsg;

public interface PushService {
	public boolean pushTransmission(PushMsg pushMsg);
}
