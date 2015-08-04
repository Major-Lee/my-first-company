package com.bhu.vas.api.rpc.user.dto;

public class UpgradeDTO {
	private boolean gray;
	private boolean forceUpgrade;
	private String name;
	private String upgradeurl;
	public UpgradeDTO(boolean gray,boolean forceUpgrade) {
		this.gray = gray;
		this.forceUpgrade = forceUpgrade;
	}
	public UpgradeDTO(boolean gray,boolean forceUpgrade, String name, String upgradeurl) {
		this.gray = gray;
		this.forceUpgrade = forceUpgrade;
		this.name = name;
		this.upgradeurl = upgradeurl;
	}
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
	public boolean isGray() {
		return gray;
	}
	public void setGray(boolean gray) {
		this.gray = gray;
	}

	public boolean isForceUpgrade() {
		return forceUpgrade;
	}
	public void setForceUpgrade(boolean forceUpgrade) {
		this.forceUpgrade = forceUpgrade;
	}
	public String toString(){
		return String.format("gray[%s] forceUpgrade[%s] name[%s] upgradeurl[%s]", gray,forceUpgrade,name,upgradeurl);
		/*StringBuilder sb = new StringBuilder();
		sb.append("gray")
		return sb.toString();*/
	}
}
