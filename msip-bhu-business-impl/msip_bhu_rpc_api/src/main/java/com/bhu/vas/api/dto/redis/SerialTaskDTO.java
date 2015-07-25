package com.bhu.vas.api.dto.redis;


public class SerialTaskDTO {
	private static int can_execute = 5*60;//5分钟
	private static int expire_sec = 2*60*60;//2小时
	private String mac;
	private long taskid;
	private String serialno;
	//收到的时间 3分钟后执行发送
	private long rects;
	public SerialTaskDTO(){
		
	}
	public SerialTaskDTO(String mac, long taskid, String serialno) {
		super();
		this.mac = mac;
		this.taskid = taskid;
		this.serialno = serialno;
		this.rects = System.currentTimeMillis();
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public long getTaskid() {
		return taskid;
	}
	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}
	public String getSerialno() {
		return serialno;
	}
	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
	public long getRects() {
		return rects;
	}
	public void setRects(long rects) {
		this.rects = rects;
	}
	
	public boolean wasExpired(long now){
		return (now - rects) >expire_sec*1000;
	}
	/**
	 * 超过3分钟
	 * @return
	 */
	public boolean canBeExecute(long now){
		return ((now-rects)>can_execute*1000 );
	}
	
	public String toString(){
		return String.format("mac[%s] taskid[%s] serialno[%s] rects[%s]", mac,taskid,serialno,rects);
	}
}
