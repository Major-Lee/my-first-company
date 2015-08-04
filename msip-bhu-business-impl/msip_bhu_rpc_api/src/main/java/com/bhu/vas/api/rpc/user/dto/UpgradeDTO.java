package com.bhu.vas.api.rpc.user.dto;

public class UpgradeDTO {
	private boolean gray;
	private String name;
	private String upgradeurl;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUpgradeurl() {
		return upgradeurl;
	}
	public void setUpgradeurl(String upgradeurl) {
		this.upgradeurl = upgradeurl;
	}
}
