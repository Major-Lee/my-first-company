package com.smartwork.client.im;


public class IMClient{

	
	public static void main(String[] args){
		String uid = "100174";
		String host = "127.0.0.1";
		int port = 5222;
		
		IMClientSupport support = new IMClientSupport(uid);
		System.out.println("client connect : " + support.connect(host, port));
	}

}
