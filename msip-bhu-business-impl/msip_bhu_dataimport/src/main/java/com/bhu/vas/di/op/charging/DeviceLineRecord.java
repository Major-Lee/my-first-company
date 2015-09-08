package com.bhu.vas.di.op.charging;

import java.util.Date;

import com.bhu.vas.api.dto.charging.ActionBuilder.Hint;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class DeviceLineRecord {
	private long uts;
	private long dts;
	private StringBuilder h = new StringBuilder();
	
	public long getUts() {
		return uts;
	}

	public void setUts(long uts) {
		this.uts = uts;
	}

	public long getDts() {
		return dts;
	}

	public void setDts(long dts) {
		this.dts = dts;
	}

	/*public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}*/
	public void appendHint(Hint hint) {
		/*if(h.length() > 0)
			this.h.append('\n');
		this.h.append(hint.getDesc());*/
		if(h.length() > 0)
			this.h.append(' ');
		this.h.append(hint.getKey());
	}
	public long gaps(){
		return dts-uts;
	}
	public String toString(){
		if(h.length() == 0){
			return String.format("u[%s] d[%s] v[%s]", 
					DateTimeHelper.formatDate(new Date(uts),DateTimeHelper.DefalutFormatPattern),
					DateTimeHelper.formatDate(new Date(dts),DateTimeHelper.DefalutFormatPattern),
					dts>uts);
		}else
			return String.format("u[%s] d[%s] v[%s] h[%s]", 
				DateTimeHelper.formatDate(new Date(uts),DateTimeHelper.DefalutFormatPattern),
				DateTimeHelper.formatDate(new Date(dts),DateTimeHelper.DefalutFormatPattern),
				dts>uts,h.toString());
	}
}
