package com.smartwork.helper;

import org.apache.mina.filter.codec.textline.LineDelimiter;

public class Test {
	public static void main(String argv[]){
		System.out.println(LineDelimiter.UNIX.getValue());
		System.out.println("-");
		System.out.println(LineDelimiter.WINDOWS.getValue());
		System.out.println("-");
		System.out.println("\n");
		System.out.println("-");
		
		String ssss = "{\"client\":\"android\",\"clientid\":\"1373016909331\",\"ip\":\"192.168.101.37\",\"token\":\"MgAdVh5RUlRRQkBIQV5cV1hZXFI=\"}";
		System.out.println(ssss.length());
	}
}
