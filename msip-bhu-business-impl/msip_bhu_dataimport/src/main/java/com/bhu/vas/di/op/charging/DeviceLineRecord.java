package com.bhu.vas.di.op.charging;

import java.util.Date;

import com.smartwork.msip.cores.helper.DateTimeHelper;

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
		;
		return String.format("up[%s] down[%s] valid[%s] hint[%s]", 
				DateTimeHelper.formatDate(new Date(up_ts),DateTimeHelper.DefalutFormatPattern),
				DateTimeHelper.formatDate(new Date(down_ts),DateTimeHelper.DefalutFormatPattern),
				down_ts>up_ts,hint);
	}
}
