package com.bhu.vas.api.helper.credentials;

import java.util.HashMap;
import java.util.Map;

import com.smartwork.msip.cores.helper.authorization.SafetyBitMarkHelper;

/**
 * 产品服务线定义定义
 * Unicorn MSIP平台
 * uRouter App
 * uCloud
 * CWifi云平台
 * uBBS论坛
 * @author Edmond
 *
 */
public enum ProductServiceLineType {
	
	uRouterApp(SafetyBitMarkHelper.A01,"UR","uRouterApp"),
	uCloudPlatform(SafetyBitMarkHelper.A02,"UC","uCloudPlatform"),
	
	CWifiPlatform(SafetyBitMarkHelper.A10,"CW","CWifiPlatform"),
	uBBS(SafetyBitMarkHelper.A11,"UB","uBBS"),
	
	UnicornMSIP(SafetyBitMarkHelper.A31,"UN","UnicornMSIP",false),
	
	;
	//private String index;
	private int index;
	//short name
	private String sname;
	//full name
	private String fname;
	//是否可以通过通用api进行创建
	private boolean apiGen;
	
	private static Map<String, ProductServiceLineType> 	allProductServiceLinesWithSName;
	private static Map<Integer, ProductServiceLineType> 	allProductServiceLinesWithIndex;
	
	ProductServiceLineType(int index,String sname,String fname){
		this(index,sname,fname,true);
	}
	
	ProductServiceLineType(int index,String sname,String fname,boolean apiGen){
		this.index = index;
		this.sname = sname;
		this.fname = fname;
		this.apiGen = apiGen;
	}
		
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public boolean isApiGen() {
		return apiGen;
	}

	public void setApiGen(boolean apiGen) {
		this.apiGen = apiGen;
	}

	public static ProductServiceLineType getBySName(String sname) {
		ProductServiceLineType utype = allProductServiceLinesWithSName.get(sname);
		if(utype != null)
			return utype;
		else
			return ProductServiceLineType.uRouterApp;
	}	
	
	public static ProductServiceLineType getByIndex(int index) {
		ProductServiceLineType utype = allProductServiceLinesWithIndex.get(index);
		if(utype != null)
			return utype;
		else
			return ProductServiceLineType.uRouterApp;
	}
	
	static {
		allProductServiceLinesWithSName = new HashMap<String,ProductServiceLineType>();
		allProductServiceLinesWithIndex = new HashMap<Integer,ProductServiceLineType>();
		ProductServiceLineType[] types = values();
		for (ProductServiceLineType type : types){
			allProductServiceLinesWithSName.put(type.sname, type);
			allProductServiceLinesWithIndex.put(type.index, type);
		}
			
	}
	
		
}
