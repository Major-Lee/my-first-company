package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;

/**
 * 设备reset push dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class DeviceResetPushDTO extends NotificationPushDTO{

	@Override
	public String getPushType() {
		return PushType.DeviceReset.getType();
	}

}
