package com.bhu.vas.di.op;

public enum Handset {
	Samsung("GT-","三星"),
	HUAWEI("HUAWEI ","华为"),
	HTC("HTC ","HTC"),
	MACOS("iPhone","iOS"),
	UNKNOW("UNKNOW","未知"),
	;
	
	String prefix;
	String group;
	
	Handset(String prefix,String group){
		this.prefix = prefix;
		this.group = group;
	}
	/*public enum Android{
		
		Samsung("GT-"),
		HUAWEI("HUAWEI "),
		HTC("HTC "),
		UNKNOW("UNKNOW"),
		;
		
		String prefix;
		
		Android(String prefix){
			this.prefix = prefix;
		}
	}
	
	public enum iOS{
		iPhone(),
		iPad(),
		iPod(),
		iMac(),
		;
		
	}
	
	public class Windows{
		
	}*/
}
