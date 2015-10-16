package com.bhu.vas.api.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class DeviceVersion {
	public static final String Build_Normal_Prefix = "Build";
	public static final String Build_R_Prefix = "r";
	//version prefix
	private String vp;
	//版本号1.3.0、1.3.0r1、1.3.2Build8606、1.2.16Buildwaip
	private String ver;
	//Device software type T：设备软件类型， 取值： U(家用版本，urouter), C(商业wifi版本), S(soc版本) 
	private String dst;
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
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
	public String getMn() {
		return mn;
	}
	public void setMn(String mn) {
		this.mn = mn;
	}
	// U(家用版本，urouter), C(商业wifi版本), S(soc版本)
	public static final String DST_uRouter = "TU";
	public static final String DST_CWifi = "TC";
	public static final String DST_soc = "TS";
	
	/**
	 * dst == null || dst = DST_uRouter
	 * @return
	 */
	public boolean wasDstURouter(){
		if(StringUtils.isEmpty(dst)) return true;
		return DST_uRouter.equals(dst);
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
					dv.setDst(s);
				}else if(prefix == 'N'){
					dv.setMn(s);
				}
			}
			index++;
		}
		return dv;
	}
	
	
	public static void main(String[] argv){
		String[] array = {"AP106P06V1.3.2Build8606","AP106P07V1.3.2r1_TU","AP106P07V1.3.2r1_TU","AP106P06V1.3.2Build8606_TU","AP109P06V1.3.0_TU_NGT","AP109P06V1.3.0_TC_NGT","CPE302P07V1.2.16r1","AP106P06V1.2.16Buildwaip_oldsytle"};
		for(String orig:array){
			DeviceVersion parser = DeviceVersion.parser(orig);
			String[] parseDeviceSwverVersion = parser.parseDeviceSwverVersion();
			System.out.println(orig+"   "+parser.wasDstURouter()  +"  "+parser.getVer()+ "  "+parseDeviceSwverVersion[0]+"  "+parseDeviceSwverVersion[1]);
		}
		
		String ss = "Build8606";
		System.out.println(ss.substring(5));
	}
}
