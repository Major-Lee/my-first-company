package com.bhu.vas.api.dto.charging;

public class DeviceOnlineAction extends ChargingAction{

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.DeviceOnline.getPrefix();
	}
	
}
