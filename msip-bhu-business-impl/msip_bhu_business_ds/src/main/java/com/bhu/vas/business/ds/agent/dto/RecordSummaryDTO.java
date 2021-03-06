package com.bhu.vas.business.ds.agent.dto;

public class RecordSummaryDTO {
	private String id;
	private int t_devices;
	private long t_dod;
	private int  t_dct;//connecttimes;
	private long t_dtx_bytes;
	private long t_drx_bytes;
	
	
	private int  t_handsets;
	//终端连接次数 
	private int  t_hct;
	//handset onlineduration
	private long t_hod;
	//handsets 上行流量
	private long t_htx_bytes;
	//handsets 下行流量
	private long t_hrx_bytes;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public long getT_dod() {
		return t_dod;
	}
	public void setT_dod(long t_dod) {
		this.t_dod = t_dod;
	}
	public int getT_dct() {
		return t_dct;
	}
	public void setT_dct(int t_dct) {
		this.t_dct = t_dct;
	}
	public long getT_dtx_bytes() {
		return t_dtx_bytes;
	}
	public void setT_dtx_bytes(long t_dtx_bytes) {
		this.t_dtx_bytes = t_dtx_bytes;
	}
	public long getT_drx_bytes() {
		return t_drx_bytes;
	}
	public void setT_drx_bytes(long t_drx_bytes) {
		this.t_drx_bytes = t_drx_bytes;
	}
	public int getT_handsets() {
		return t_handsets;
	}
	public void setT_handsets(int t_handsets) {
		this.t_handsets = t_handsets;
	}
	
	public int getT_hct() {
		return t_hct;
	}
	public void setT_hct(int t_hct) {
		this.t_hct = t_hct;
	}
	public long getT_hod() {
		return t_hod;
	}
	public void setT_hod(long t_hod) {
		this.t_hod = t_hod;
	}
	public long getT_htx_bytes() {
		return t_htx_bytes;
	}
	public void setT_htx_bytes(long t_htx_bytes) {
		this.t_htx_bytes = t_htx_bytes;
	}
	public long getT_hrx_bytes() {
		return t_hrx_bytes;
	}
	public void setT_hrx_bytes(long t_hrx_bytes) {
		this.t_hrx_bytes = t_hrx_bytes;
	}
	public void incr(RecordSummaryDTO dto){
		if(dto == null) return;
		t_dod += dto.getT_dod();
		t_dct += dto.getT_dct();
		t_dtx_bytes += dto.getT_dtx_bytes();
		t_drx_bytes += dto.getT_drx_bytes();
		
		t_handsets += dto.getT_handsets();
		t_hod += dto.getT_hod();
		t_hct += dto.getT_hct();
		t_htx_bytes += dto.getT_htx_bytes();
		t_hrx_bytes += dto.getT_hrx_bytes();
	}
	
	public int getT_devices() {
		return t_devices;
	}
	public void setT_devices(int t_devices) {
		this.t_devices = t_devices;
	}
	public String toString(){
		return String.format("id[%s] onlineduration[%s] connecttimes[%s] handsets[%s] tx_bytes[%s] rx_bytes[%s]", 
				id,t_dod,t_dct,t_handsets,t_dtx_bytes,t_drx_bytes);
	}
}
