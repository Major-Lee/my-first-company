package com.bhu.vas.api.rpc.user.dto;

public class UpgradeDTO {
	private boolean gray;
	private boolean forceDeviceUpgrade;
	private boolean forceAppUpgrade;
	private String name;
	private String upgradeurl;
	public UpgradeDTO(boolean gray,boolean forceDeviceUpgrade) {
		this.gray = gray;
		this.forceDeviceUpgrade = forceDeviceUpgrade;
	}
	public UpgradeDTO(boolean gray,boolean forceDeviceUpgrade, String name, String upgradeurl) {
		this.gray = gray;
		this.forceDeviceUpgrade = forceDeviceUpgrade;
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

	public boolean isForceDeviceUpgrade() {
		return forceDeviceUpgrade;
	}
	public void setForceDeviceUpgrade(boolean forceDeviceUpgrade) {
		this.forceDeviceUpgrade = forceDeviceUpgrade;
	}
	
	public boolean isForceAppUpgrade() {
		return forceAppUpgrade;
	}
	public void setForceAppUpgrade(boolean forceAppUpgrade) {
		this.forceAppUpgrade = forceAppUpgrade;
	}
	public String toString(){
		return String.format("gray[%s] forceDeviceUpgrade[%s] name[%s] upgradeurl[%s] forceDeviceUpgrade[%s]", 
				gray,forceDeviceUpgrade,name,upgradeurl,forceAppUpgrade);
		/*StringBuilder sb = new StringBuilder();
		sb.append("gray")
		return sb.toString();*/
	}
}
