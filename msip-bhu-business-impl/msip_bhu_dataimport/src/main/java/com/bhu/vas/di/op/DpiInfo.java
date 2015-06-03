package com.bhu.vas.di.op;

import java.io.UnsupportedEncodingException;

import com.smartwork.msip.cores.helper.ByteArrayHelper;
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
	
	/*public static DpiInfo fromTextLine(String lineText){
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
	}*/
	
	public static DpiInfo fromTextLine(String lineText) throws UnsupportedEncodingException{
		int start = 0;
		//int currentIndex = 0;
		DpiInfo dinfo = new DpiInfo();
		//System.out.println(lineText);
		byte[] lineBytes = lineText.getBytes(StringHelper.CHATSET_UTF8);
		//String currentText = new String(lineText);
		try {
			do {
				//byte[] index = ByteArrayHelper.get(lineBytes, 0, 4);
				String index = new String(ByteArrayHelper.get(lineBytes, start + 0, 4));
				int tlen = Integer.parseInt(new String(ByteArrayHelper.get(lineBytes, start + 4, 4)));
				String payload = null;
				if (Index_2.equals(index)) {
					payload = new String(ByteArrayHelper.get(lineBytes, start + 8, tlen + 1), StringHelper.CHATSET_UTF8);
					start = start + 8 + tlen + 1;
					//System.out.println(payload);
					//payload = currentText.substring(8,8+tlen+1);
				} else {
					payload = new String(ByteArrayHelper.get(lineBytes, start + 8, tlen), StringHelper.CHATSET_UTF8);
					start = start + 8 + tlen;
					//System.out.println(payload);
					//payload = currentText.substring(8,8+tlen);
				}
				if (Index_1.equals(index)) dinfo.setTaskid(Long.parseLong(payload));
				if (Index_2.equals(index)) dinfo.setTmac(StringHelper.removeWhiteSpace(payload));
				if (Index_3.equals(index)) dinfo.setDmac(payload);
				if (Index_4.equals(index)) dinfo.setAip(payload);
				if (Index_5.equals(index)) dinfo.setAhost(payload);
				if (Index_6.equals(index)) dinfo.setUrl(payload);
				if (Index_7.equals(index)) dinfo.setAccept(payload);
				if (Index_8.equals(index)) dinfo.setUseragent(payload);
			
			/*if(Index_2.equals(index))
				start = start+ 8+tlen+1;
			else
				start = start+8+tlen;*/
			} while (start < lineBytes.length);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(lineText);
		}
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
	
	public static void main(String[] argv) throws UnsupportedEncodingException{
		//DpiInfo info = DpiInfo.fromTextLine("0001001000000000080002001774: e5:43:9d:13:c80003001784:82:f4:90:04:1c00040014121.18.239.14000050022miserupdate.aliyun.com00060028/data/2.4.1.6/brfversion.xml00070063text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.800080063Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1;Miser Report)00000000");

		DpiInfo info = DpiInfo.fromTextLine("0001001000000008020002001760: d8:19:d0:73:270003001784:82:f4:90:03:9800040015124.225.131.14800050017upfile1.kdnet.net00060151/textareaeditor/GetPostFast_ubb.asp?boardid=1&followup=16627149&rootid=8952705&star=1&TotalUseTable=DV_BBS8&UserName=&topicname=猫眼看人&pages=3&lay=4200070037text/html, application/xhtml+xml, */*00080081Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0; JuziBrowser) like Gecko00000000");
		//DpiInfo info = DpiInfo.fromTextLine("0001001000000096270002001700: 61:71:4a:c1:880003001784:82:f4:90:03:4800040015111.206.227.16100050008x.jd.com00060092/exsites?spread_type=2&ad_ids=645:5&location_info=0&mobile_type=1&callback=getjjsku_callback00070063text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.800080145Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_2 like Mac OS X; zh-CN) AppleWebKit/537.51.1 (KHTML, like Gecko) Mobile/11D257 UCBrowser/10.4.5.568 Mobile00000000");
		//info = DpiInfo.fromTextLine("0001001000000068720002001784: 4b:f5:42:14:a70003001784:82:f4:90:04:1c0004001461.135.169.12500050013www.baidu.com00060036/s?cl=3&wd=Ҷ����������%20˫��Ƥ����00070037text/html, application/xhtml+xml, */*00080073Mozilla/5.0 (Windows NT 6.3; Win64; x64; Trident/7.0; rv:11.0) like Gecko00000000");

		System.out.println(info.getTaskid());
		System.out.println(info.getTmac());
		System.out.println(info.getDmac());
		System.out.println(info.getAip());
		System.out.println(info.getAhost());
		System.out.println(info.getUrl());
		System.out.println(info.getAccept());
		System.out.println(info.getUseragent());
		
		/*UserAgent useragent = new UserAgent(info.getUseragent());
		System.out.println(useragent.getOperatingSystem());
        System.out.println(useragent.getBrowser());
        System.out.println(useragent.getId());
        System.out.println(useragent.getBrowserVersion());*/
		String ss = "123";

		System.out.println(ss.length());
		System.out.println(ss.getBytes().length);
		System.out.println(ss.getBytes("utf-8").length);
		System.out.println(ss.getBytes("GBK").length);
	}
}
