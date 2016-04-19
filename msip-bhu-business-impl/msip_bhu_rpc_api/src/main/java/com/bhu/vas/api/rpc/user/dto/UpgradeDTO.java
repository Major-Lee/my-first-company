package com.bhu.vas.api.rpc.user.dto;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.WifiDeviceHelper;

public class UpgradeDTO {
	private String dut;
	private int gl;
	//升级类别 true固件升级 false 增值组件升级
	private boolean fw;
	private boolean forceDeviceUpgrade;
	private boolean forceAppUpgrade;
	private String name;
	private String upgradeurl;
	private String upgrade_slaver_urls;
	private String currentDVB;
    private String currentAVB;
    private String desc;
    
    private Date currentGrayPublished_at;
    
    public UpgradeDTO(boolean fw,boolean forceDeviceUpgrade) {
    	this.fw = fw;
		this.forceDeviceUpgrade = forceDeviceUpgrade;
    }
    
	public UpgradeDTO(String dut,int gl,boolean fw,boolean forceDeviceUpgrade) {
		this.dut = dut;
		this.gl = gl;
		this.fw = fw;
		this.forceDeviceUpgrade = forceDeviceUpgrade;
	}
	public UpgradeDTO(String dut,int gl,boolean fw,boolean forceDeviceUpgrade, String name, String upgradeurl) {
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
		return String.format("dut[%s] gl[%s] fw[%s] currentDVB[%s] forceDeviceUpgrade[%s] name[%s] upgradeurl[%s] forceAppUpgrade[%s] desc[%s]", 
				dut,gl,fw,currentDVB,forceDeviceUpgrade,name,upgradeurl,forceAppUpgrade,desc);
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
	public String getDut() {
		return dut;
	}
	public void setDut(String dut) {
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
	

	public String getUpgrade_slaver_urls() {
		if(StringUtils.isEmpty(upgrade_slaver_urls)) return StringUtils.EMPTY;
		return upgrade_slaver_urls;
	}

	public void setUpgrade_slaver_urls(String upgrade_slaver_urls) {
		this.upgrade_slaver_urls = upgrade_slaver_urls;
	}

	public String buildUpgradeCMD(String mac, long taskid,String beginTime,String endTime){
		if(taskid == 0){
			taskid = CMDBuilder.auto_taskid_fragment.getNextSequence();
		}
		String cmd = null;
		if(WifiDeviceHelper.WIFI_DEVICE_UPGRADE_FW == fw){
			/*//测试时使用，都变成立即升级
			beginTime = StringUtils.EMPTY;
			endTime = StringUtils.EMPTY;*/
			cmd = CMDBuilder.builderDeviceUpgrade(mac, taskid,beginTime,endTime,this.getUpgradeurl(),this.getUpgrade_slaver_urls());
		}else
			cmd = CMDBuilder.builderVapModuleUpgrade(mac, taskid,this.getUpgradeurl(),this.getUpgrade_slaver_urls(),
					WifiDeviceHelper.Upgrade_Module_Default_Retry_Count, WifiDeviceHelper.Upgrade_Module_Default_RetryInterval);
		return cmd;
		//return CMDBuilder.builderDeviceUpgrade(mac, taskid,beginTime,endTime,this.getUpgradeurl());
	}

	public Date getCurrentGrayPublished_at() {
		return currentGrayPublished_at;
	}

	public void setCurrentGrayPublished_at(Date currentGrayPublished_at) {
		this.currentGrayPublished_at = currentGrayPublished_at;
	}
	
	/*public boolean aaa(){
		
	}*/
}
