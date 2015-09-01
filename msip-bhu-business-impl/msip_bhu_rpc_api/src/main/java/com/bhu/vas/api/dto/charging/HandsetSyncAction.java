package com.bhu.vas.api.dto.charging;

import java.util.List;

public class HandsetSyncAction extends ChargingAction{
	private List<String> hmacs;


	public List<String> getHmacs() {
		return hmacs;
	}

	public void setHmacs(List<String> hmacs) {
		this.hmacs = hmacs;
	}

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.HandsetSync.getPrefix();
	}
	
}
