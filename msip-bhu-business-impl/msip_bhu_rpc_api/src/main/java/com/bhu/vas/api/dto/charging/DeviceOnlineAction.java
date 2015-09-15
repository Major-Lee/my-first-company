package com.bhu.vas.api.dto.charging;

public class DeviceOnlineAction extends ChargingAction{
	//是否模拟
	private boolean sm; 
	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.DeviceOnline.getPrefix();
	}
	public boolean isSm() {
		return sm;
	}
	public void setSm(boolean sm) {
		this.sm = sm;
	}
	
}
