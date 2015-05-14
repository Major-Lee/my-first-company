package com.bhu.vas.daemon;


public class SessionInfo {
	private String mac;
	private String ctx;
	private long rects;
	public SessionInfo(){
		
	}
	public SessionInfo(String mac, String ctx) {
		super();
		this.mac = mac;
		this.ctx = ctx;
		this.rects = System.currentTimeMillis();
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public long getRects() {
		return rects;
	}
	public void setRects(long rects) {
		this.rects = rects;
	}
	
	public String getCtx() {
		return ctx;
	}
	public void setCtx(String ctx) {
		this.ctx = ctx;
	}
	/**
	 * 每台设备十分钟发送查询指令
	 * @return
	 */
	public boolean canBeExecute(long now){
		return ((now-rects)>10*60*1000l );//10*60*1000l
	}
	
	public String toString(){
		return String.format("mac[%s] ctx[%s] serialno[%s] rects[%s]", mac,ctx,rects);
	}
}
