package com.bhu.vas.api.rpc.user.dto;

import com.bhu.vas.api.helper.CMDBuilder;

public class UpgradeDTO {
	private int dut;
	private int gl;
	//升级类别 true固件升级 false 增值组件升级
	private boolean fw;
	private boolean forceDeviceUpgrade;
	private boolean forceAppUpgrade;
	private String name;
	private String upgradeurl;
	private String currentDVB;
    private String currentAVB;
    private String desc;
	public UpgradeDTO(int dut,int gl,boolean fw,boolean forceDeviceUpgrade) {
		this.dut = dut;
		this.gl = gl;
		this.fw = fw;
		this.forceDeviceUpgrade = forceDeviceUpgrade;
	}
	public UpgradeDTO(int dut,int gl,boolean fw,boolean forceDeviceUpgrade, String name, String upgradeurl) {
		this.dut = dut;
		this.gl = gl;
		this.fw = fw;
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
	
	public String getCurrentAVB() {
		return currentAVB;
	}
	public void setCurrentAVB(String currentAVB) {
		this.currentAVB = currentAVB;
	}
	public String toString(){
		return String.format("dut[%s] gl[%s] currentDVB[%s] forceDeviceUpgrade[%s] name[%s] upgradeurl[%s] forceAppUpgrade[%s] desc[%s]", 
				dut,gl,currentDVB,forceDeviceUpgrade,name,upgradeurl,forceAppUpgrade,desc);
		/*StringBuilder sb = new StringBuilder();
		sb.append("gray")
		return sb.toString();*/
	}
	public String getCurrentDVB() {
		return currentDVB;
	}
	public void setCurrentDVB(String currentDVB) {
		this.currentDVB = currentDVB;
	}
	public int getDut() {
		return dut;
	}
	public void setDut(int dut) {
		this.dut = dut;
	}
	public int getGl() {
		return gl;
	}
	public void setGl(int gl) {
		this.gl = gl;
	}
	public boolean isFw() {
		return fw;
	}
	public void setFw(boolean fw) {
		this.fw = fw;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String buildUpgradeCMD(String mac, long taskid,String beginTime,String endTime){
		if(taskid == 0){
			taskid = CMDBuilder.auto_taskid_fragment.getNextSequence();
		}
		String cmdPayload = CMDBuilder.builderDeviceUpgrade(mac, taskid,beginTime,endTime,this.getUpgradeurl());
		return cmdPayload;
	}
}
