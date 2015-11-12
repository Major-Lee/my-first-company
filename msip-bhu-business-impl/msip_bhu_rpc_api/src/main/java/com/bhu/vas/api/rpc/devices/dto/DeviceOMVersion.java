package com.bhu.vas.api.rpc.devices.dto;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 设备运营模块版本包含信息
 * @author Edmond
 *
 */
public class DeviceOMVersion {
	//version prefix
	private String vp;
	//版本号H108V1.2.10M8299，H106V1.3.2M8888
	private String ver;
	// module build no
	private String mno;
	
	public String getVp() {
		return vp;
	}
	public void setVp(String vp) {
		this.vp = vp;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	
	public String getMno() {
		return mno;
	}
	public void setMno(String mno) {
		this.mno = mno;
	}
	
	public String getHdt(){
		if(StringUtils.isNotEmpty(vp))
			return vp.substring(1);
		return null;
	}
	
	private static final String Swver_Spliter_Patterns = "[V|M]+";
	public static DeviceOMVersion parser(String device_om){
		DeviceOMVersion dv = null;
		if(StringUtils.isEmpty(device_om)) return dv;
		dv = new DeviceOMVersion();
		String[] split = device_om.split(Swver_Spliter_Patterns);
		int index = 0;
		for(String s:split){
			if(index == 0) dv.setVp(s);
			if(index == 1) dv.setVer(s);
			if(index == 2) dv.setMno(s);
			index++;
		}
		return dv;
	}
	public boolean valid(){
		return StringUtils.isNotEmpty(vp) && StringUtils.isNotEmpty(ver) && StringUtils.isNotEmpty(mno);
	}
	public boolean canExecuteUpgrade(){
		return true;
	}
	/**
	 * 比较两个设备的运营模块版本号
	 * 前置条件：版本号不包括
	 * 备注：只针对大版本号不一致或者 大版本一致情况下的小版本都存在Build情况
	 * 返回 1 表示 device_orig_swver 大于 gray_orig_swver
	 * 返回 0 表示 device_orig_swver 等于 gray_orig_swver
	 * 返回 -1 表示 device_orig_swver 小于 gray_orig_swver 可以升级
	 * 小版本号的前缀不一致的情况下都返回 0 eg：build或r 或者vp不一致的情况下也会返回0
	 * @param orig_swver1
	 * @param orig_swver2
	 * @return
	 */
	public static int compareVersions(String device_om, String gray_defined_om){
		if(StringUtils.isEmpty(device_om) || StringUtils.isEmpty(gray_defined_om)) 
			throw new RuntimeException("param validate empty");
		if(device_om.equalsIgnoreCase(gray_defined_om)) return 0;
		DeviceOMVersion ver1 = DeviceOMVersion.parser(device_om);
		if(ver1 == null || !ver1.canExecuteUpgrade()) return 0;
		DeviceOMVersion ver2 = DeviceOMVersion.parser(gray_defined_om);
		if(ver1 == null || !ver2.canExecuteUpgrade()) return 0;
		//在vp相等的前提下才能允许升级
		if(ver1.getVp().equals(ver2.getVp())){
			//判断大版本号
			int top_ret = StringHelper.compareVersion(ver1.getVer(), ver2.getVer());
			if(top_ret != 0) return top_ret;
			int bottom_ret = StringHelper.compareVersion(ver1.getMno(), ver2.getMno());
			return bottom_ret;
		}else{
			return 0;
		}
	}
	public boolean wasDutURouter(){
		return WifiDeviceHelper.isURouterHdType(vp);
	}
	public boolean wasDutSoc(){
		return WifiDeviceHelper.isSocHdType(vp);
	}
	
	public String toString(){
		return String.format("vp[%s] ver[%s] mno[%s]", vp,ver,mno);
	}
	
	public static void main(String[] argv){
		String[] array = {"H108V1.2.10M8299","H108V1.2.15M8299","H108V1.2.10M8399","H108V1.3.10M8299"};
		for(String orig:array){
			DeviceOMVersion parser = DeviceOMVersion.parser(orig);
			System.out.println(JsonHelper.getJSONString(parser));
		}
		
		String current = "H108V1.2.16M8399";
		String[] array1 = {"H108V1.2.10M8299","H106V1.2.15M8299","H108V1.3.10M8399","H108V1.3.10M8299","H108V1.2.16M8399"};
		for(String orig:array1){
			int compareDeviceVersions = compareVersions(current, orig);
			System.out.println(compareDeviceVersions);
		}
	}
}
