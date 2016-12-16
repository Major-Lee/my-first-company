package com.bhu.vas.api.vto.device;

@SuppressWarnings("serial")
public class UpgradeCheckVTO implements java.io.Serializable{
	private boolean needUpgrade = false;
	private String url;
	public boolean isNeedUpgrade() {
		return needUpgrade;
	}
	public void setNeedUpgrade(boolean needUpgrade) {
		this.needUpgrade = needUpgrade;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
