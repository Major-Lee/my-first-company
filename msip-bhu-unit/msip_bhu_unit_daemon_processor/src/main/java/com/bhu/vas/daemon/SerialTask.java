package com.bhu.vas.daemon;


public class SerialTask {
	private String mac;
	private int taskid;
	private String serialno;
	//收到的时间 3分钟后执行发送
	private long rects;
	public SerialTask(){
		
	}
	public SerialTask(String mac, int taskid, String serialno) {
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
	public int getTaskid() {
		return taskid;
	}
	public void setTaskid(int taskid) {
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
	
	/**
	 * 超过3分钟
	 * @return
	 */
	public boolean canBeExecute(long now){
		return ((now-rects)>3*60*1000 );
	}
}
