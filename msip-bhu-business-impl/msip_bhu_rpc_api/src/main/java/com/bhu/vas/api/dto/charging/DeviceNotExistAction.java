package com.bhu.vas.api.dto.charging;

public class DeviceNotExistAction extends ChargingAction{

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.DeviceNotExist.getPrefix();
	}
	
}
