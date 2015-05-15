package com.bhu.vas.di.op;

import com.smartwork.msip.cores.helper.StringHelper;

public class DpiInfo {
	//任务id
	private long taskid;
	//终端mac
	private String tmac;
	//设备mac
	private String dmac;
	//访问ip
	private String aip;
	//访问host
	private String ahost;
	//访问url
	private String url;
	
	private String accept;
	private String useragent;
	public long getTaskid() {
		return taskid;
	}
	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}
	public String getTmac() {
		return tmac;
	}
	public void setTmac(String tmac) {
		this.tmac = tmac;
	}
	public String getDmac() {
		return dmac;
	}
	public void setDmac(String dmac) {
		this.dmac = dmac;
	}
	public String getAip() {
		return aip;
	}
	public void setAip(String aip) {
		this.aip = aip;
	}
	public String getAhost() {
		return ahost;
	}
	public void setAhost(String ahost) {
		this.ahost = ahost;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public String getUseragent() {
		return useragent;
	}
	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}
	
	public static DpiInfo fromTextLine(String lineText){
		//int currentIndex = 0;
		DpiInfo dinfo = new DpiInfo();
		String currentText = new String(lineText);
		do{
			String index = currentText.substring(0, 4);
			int tlen = Integer.parseInt(currentText.substring(4,8));
			String payload = null;
			if(Index_2.equals(index)){
				payload = currentText.substring(8,8+tlen+1);
			}else{
				payload = currentText.substring(8,8+tlen);
			}
			
			if(Index_1.equals(index)) dinfo.setTaskid(Long.parseLong(payload));
			if(Index_2.equals(index)) dinfo.setTmac(StringHelper.removeWhiteSpace(payload));
			if(Index_3.equals(index)) dinfo.setDmac(payload);
			if(Index_4.equals(index)) dinfo.setAip(payload);
			if(Index_5.equals(index)) dinfo.setAhost(payload);
			if(Index_6.equals(index)) dinfo.setUrl(payload);
			if(Index_7.equals(index)) dinfo.setAccept(payload);
			if(Index_8.equals(index)) dinfo.setUseragent(payload);
			
			if(Index_2.equals(index))
				currentText = currentText.substring(8+tlen+1);
			else
				currentText = currentText.substring(8+tlen);
		}while(!currentText.equals(""));
		return dinfo;
	}
	
	private static final String Index_1 = "0001";
	private static final String Index_2 = "0002";
	private static final String Index_3 = "0003";
	private static final String Index_4 = "0004";
	private static final String Index_5 = "0005";
	private static final String Index_6 = "0006";
	private static final String Index_7 = "0007";
	private static final String Index_8 = "0008";
	
	public static void main(String[] argv){
		DpiInfo info = DpiInfo.fromTextLine("0001001000000000080002001774: e5:43:9d:13:c80003001784:82:f4:90:04:1c00040014121.18.239.14000050022miserupdate.aliyun.com00060028/data/2.4.1.6/brfversion.xml00070063text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.800080063Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1;Miser Report)00000000");
		
		System.out.println(info.getTaskid());
		System.out.println(info.getTmac());
		System.out.println(info.getDmac());
		System.out.println(info.getAip());
		System.out.println(info.getAhost());
		System.out.println(info.getUrl());
		System.out.println(info.getAccept());
		System.out.println(info.getUseragent());
	}
}
