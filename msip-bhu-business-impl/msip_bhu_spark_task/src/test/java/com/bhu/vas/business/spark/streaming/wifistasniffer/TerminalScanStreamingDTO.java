package com.bhu.vas.business.spark.streaming.wifistasniffer;

@SuppressWarnings("serial")
public class TerminalScanStreamingDTO implements java.io.Serializable{
	private String mac;
	private String hd_mac;
	private long ts;
	
	public TerminalScanStreamingDTO(){
		
	}
	public TerminalScanStreamingDTO(String mac, String hd_mac){
		this.mac = mac;
		this.hd_mac = hd_mac;
		this.ts = System.currentTimeMillis();
	}
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getHd_mac() {
		return hd_mac;
	}
	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
//	public static void main(){
//		TerminalScanStreamingDTO dto1 = new TerminalScanStreamingDTO("aa:aa:aa:aa:aa:aa", "hh:hh:hh:hh:hh:h1");
//		TerminalScanStreamingDTO dto2 = new TerminalScanStreamingDTO("aa:aa:aa:aa:aa:aa", "hh:hh:hh:hh:hh:h2");
//		TerminalScanStreamingDTO dto3 = new TerminalScanStreamingDTO("bb:bb:bb:bb:bb:bb", "hh:hh:hh:hh:hh:h3");
//	}
}
