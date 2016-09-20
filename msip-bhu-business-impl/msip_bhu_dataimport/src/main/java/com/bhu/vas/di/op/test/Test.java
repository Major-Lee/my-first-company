package com.bhu.vas.di.op.test;

import nl.bitwalker.useragentutils.UserAgent;

public class Test {
	public static void main(String[] argv){
		UserAgent useragent = new UserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_2 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13F69");
		
		System.out.println(useragent.getBrowser());
		System.out.println(useragent.getBrowserVersion());
		System.out.println(useragent.getOperatingSystem());
	}
	
}
