package com.bhu.vas.api.rpc.devices.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 设备固件版本包含信息
 * @author Edmond
 *
 */
public class DeviceVersion {
	public static final String Build_Normal_Prefix = "Build";
	public static final String Build_R_Prefix = "r";
	//version prefix
	private String vp;
	//版本号1.3.0、1.3.0r1、1.3.2Build8606、1.2.16Buildwaip
	private String ver;
	//Device software type T：设备软件类型， 取值： U(家用版本，urouter), C(商业wifi版本), S(soc版本) 
	private String dut;
	//Manufacturer name N：厂家名称，在属性T取值为C(商业wifi)时，存在此属性项. 该属性的取值为各个商业wifi厂家的缩写.
	private String mn;
	
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

	public String getDut() {
		return dut;
	}
	public void setDut(String dut) {
		this.dut = dut;
	}
	public String getMn() {
		return mn;
	}
	public void setMn(String mn) {
		this.mn = mn;
	}
	// U(家用版本，urouter), C(商业wifi版本), S(soc版本)
	public static final String DUT_uRouter = "TU";
	public static final String DUT_CWifi = "TC";
	public static final String DUT_soc = "TS";
	
	/**
	 * dut == null || dut = DST_uRouter
	 * @return
	 */
	public boolean wasDutURouter(){
		if(StringUtils.isEmpty(dut)) return false;
		return DUT_uRouter.equals(dut);
	}
	
	public boolean wasDutSoc(){
		if(StringUtils.isEmpty(dut)) return false;
		return DUT_soc.equals(dut);
	}
	/**
	 * 解析设备的软件版本
	 * 返回数组 0 大版本号 1 小版本号
	 * AP106P06V1.3.2Build8606
	 * AP106P07V1.3.2r1_TU
	 * AP106P06V1.3.2Build8606_TU
	 * @param orig_swver
	 * @return
	 */
	public String[] parseDeviceSwverVersion(){
		try{
	    	Pattern p = Pattern.compile("(.*)(B|r)");
	    	Matcher m = p.matcher(ver);
	    	String top_version = null;
	    	while(m.find()){  
	    		top_version = m.group(1);  
	    	}
	    	if(top_version == null){
	    		return new String[]{ver, null};
	    	}
	    	p = Pattern.compile("Build(.*)|r(.*)");
	    	m = p.matcher(ver);
	    	String bottom_version = null;
	    	while(m.find()){  
	    		bottom_version = m.group();  
	    	}
	    	return new String[]{top_version, bottom_version};
		}catch(Exception ex){
			
		}
		return null;
	}
	
	//String[] array = {"AP106P07V1.3.2r1_TU","AP106P07V1.3.2r1_TU","AP106P06V1.3.2Build8606_TU","AP109P06V1.3.0_TC_NGT","CPE302P07V1.2.16r1","AP106P06V1.2.16Buildwaip_oldsytle"};
	private static final String Swver_Spliter_Patterns = "[V|_]+";
	public static DeviceVersion parser(String orig_swver){
		DeviceVersion dv = null;
		if(StringUtils.isEmpty(orig_swver)) return dv;
		dv = new DeviceVersion();
		String[] split = orig_swver.split(Swver_Spliter_Patterns);
		int index = 0;
		for(String s:split){
			if(index == 0) dv.setVp(s);
			if(index == 1) dv.setVer(s);
			if(index > 1){
				char prefix = s.charAt(0);
				if(prefix == 'T'){
					dv.setDut(s);
				}else if(prefix == 'N'){
					dv.setMn(s);
				}
			}
			index++;
		}
		return dv;
	}
	
	/**
	 * 比较两个设备的软件版本号
	 * 前置条件：版本号不包括
	 * 备注：只针对大版本号不一致或者 大版本一致情况下的小版本都存在Build情况
	 * 返回 1 表示 device_orig_swver 大于 gray_orig_swver
	 * 返回 0 表示 device_orig_swver 等于 gray_orig_swver
	 * 返回 -1 表示 device_orig_swver 小于 gray_orig_swver 可以升级
	 * 小版本号的前缀不一致的情况下都返回 0 eg：build或r
	 * @param orig_swver1
	 * @param orig_swver2
	 * @return
	 */
	public static int compareVersions(String device_orig_swver, String gray_defined_orig_swver){
		if(StringUtils.isEmpty(device_orig_swver) || StringUtils.isEmpty(gray_defined_orig_swver)) 
			throw new RuntimeException("param validate empty");
		if(device_orig_swver.equalsIgnoreCase(gray_defined_orig_swver)) return 0;
		DeviceVersion ver1 = DeviceVersion.parser(device_orig_swver);
		if(ver1 == null || !ver1.canExecuteUpgrade()) return 0;
		String[] orig_swver1_versions = ver1.parseDeviceSwverVersion();
		if(orig_swver1_versions == null) return -1;
		DeviceVersion ver2 = DeviceVersion.parser(gray_defined_orig_swver);
		String[] orig_swver2_versions = ver2.parseDeviceSwverVersion();
		if(orig_swver2_versions == null) return 1;
		//暂时忽略在vp相等的前提下才能允许升级
		if(true){//{ver1.getVp().equals(ver2.getVp())){
			//判断大版本号
			int top_ret = StringHelper.compareVersion(orig_swver1_versions[0], orig_swver2_versions[0]);
			//System.out.println("top ret " + top_ret);
			if(top_ret != 0) return top_ret;
			if(orig_swver1_versions[1] != null &&  orig_swver2_versions[1] != null){
				if(orig_swver1_versions[1].startsWith(DeviceVersion.Build_Normal_Prefix) && orig_swver2_versions[1].startsWith(DeviceVersion.Build_Normal_Prefix)){
					int bottom_ret = StringHelper.compareVersion(
							orig_swver1_versions[1].substring(DeviceVersion.Build_Normal_Prefix.length()), orig_swver2_versions[1].substring(DeviceVersion.Build_Normal_Prefix.length()));
					return bottom_ret;
				}
			}
		}
		return 0;
	}
	
	public boolean canExecuteUpgrade(){
		return StringUtils.isEmpty(dut) || wasDutURouter();
	}
	
	public static void main(String[] argv){
		String[] array = {"AP106P06V1.3.2Build8606","AP106P07V1.3.2r1_TU","AP106P07V1.3.2r1_TU","AP106P06V1.3.2Build8606_TU","AP109P06V1.3.0_TU_NGT","AP109P06V1.3.0_TC_NGT","CPE302P07V1.2.16r1","AP106P06V1.2.16Buildwaip_oldsytle"};
		for(String orig:array){
			DeviceVersion parser = DeviceVersion.parser(orig);
			String[] parseDeviceSwverVersion = parser.parseDeviceSwverVersion();
			System.out.println(orig+"   "+parser.wasDutURouter() + "  "+ parser.getVp() +"  "+parser.getVer()+ "  "+parseDeviceSwverVersion[0]+"  "+parseDeviceSwverVersion[1]);
		}
		
		String ss = "Build8606";
		System.out.println(ss.substring(5));
		
		String current = "AP106P06V1.3.2Build8606_TU";
		String[] array1 = {"AP106P06V1.3.2Build8606","AP106P06V1.3.2Build8677","AP106P06V1.3.2Build8600","AP106P07V1.3.3r1_TU","AP106P07V1.3.1r1_TU","AP106P06V1.3.0Build8606_TU","AP109P06V1.3.0_TU_NGT","AP109P06V1.3.0_TC_NGT","CPE302P07V1.2.16r1","AP106P06V1.2.16Buildwaip_oldsytle"};
		for(String orig:array1){
			int compareDeviceVersions = compareVersions(current, orig);
			System.out.println(compareDeviceVersions);
		}
			
	}
}
