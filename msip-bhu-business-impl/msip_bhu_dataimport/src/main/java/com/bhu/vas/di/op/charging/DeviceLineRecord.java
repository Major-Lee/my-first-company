package com.bhu.vas.di.op.charging;

public class DeviceLineRecord {
	private long up_ts;
	private long down_ts;
	private String hint;
	public long getUp_ts() {
		return up_ts;
	}
	public void setUp_ts(long up_ts) {
		this.up_ts = up_ts;
	}
	public long getDown_ts() {
		return down_ts;
	}
	public void setDown_ts(long down_ts) {
		this.down_ts = down_ts;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	
	
	public String toString(){
		return String.format("up[%s] down[%s] hint[%s] valid[%s]", up_ts,down_ts,hint,down_ts>=up_ts);
	}
}
