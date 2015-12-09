package com.bhu.vas.api.dto;

import java.io.Serializable;
/**
 * 移动设备上下线记录DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class HandsetLogDTO implements Serializable{

	// online ts
	private long o;
	//offline ts
	private long f;
	//total_rx_bytes
	private long trb;
	public long getO() {
		return o;
	}
	public void setO(long o) {
		this.o = o;
	}
	public long getF() {
		return f;
	}
	public void setF(long f) {
		this.f = f;
	}
	public long getTrb() {
		return trb;
	}
	public void setTrb(long trb) {
		this.trb = trb;
	}
	
	public static HandsetLogDTO buildOnline(long ts){
		HandsetLogDTO dto = new HandsetLogDTO();
		dto.setO(ts);
		return dto;
	}
	public static HandsetLogDTO buildFull(long o_ts,long f_ts,long trb){
		HandsetLogDTO dto = new HandsetLogDTO();
		dto.setO(o_ts);
		dto.setF(f_ts);
		dto.setTrb(trb);
		return dto;
	}
	/**
	 * 是否是完整的片段，存在上线并且存在下线
	 * @return
	 */
	public boolean wasComplete(){
		return o>0l && f >0l && f>=0;
	}
}