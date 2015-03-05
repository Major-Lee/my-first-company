package com.smartwork.im;

public class ServerInfo {
	//主机名称 server hostname
	private String hn;
	//此主机服务的编号 配置
	private String no;
	//此主机服务的名称 配置 如果不存在则使用主机名hostname
	private String n;
	//此主机服务的ip
	private String ip;
	
	public ServerInfo(String n,String no,String hn,String ip){
		this.n = n;
		this.no = no;
		this.ip = ip;
		this.hn = hn;
	}

	public String getMark(){
		StringBuilder sb = new StringBuilder();
		sb.append(no).append('.').append(n);
		return sb.toString();
	}
	
	public String getHn() {
		return hn;
	}
	public void setHn(String hn) {
		this.hn = hn;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}


	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}
	
}
