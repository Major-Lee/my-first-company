package com.bhu.vas.api.dto.charging;

public class DeviceOfflineAction extends ChargingAction{

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.DeviceOffline.getPrefix();
	}
	
}
