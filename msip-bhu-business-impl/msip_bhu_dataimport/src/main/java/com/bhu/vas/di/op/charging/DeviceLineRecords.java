package com.bhu.vas.di.op.charging;

import java.util.ArrayList;
import java.util.List;

public class DeviceLineRecords {
	private List<DeviceLineRecord> records = new ArrayList<>();
	private DeviceLineRecord current;
	public List<DeviceLineRecord> getRecords() {
		return records;
	}
	public void setRecords(List<DeviceLineRecord> records) {
		this.records = records;
	}
	public DeviceLineRecord getCurrent() {
		return current;
	}
	public void setCurrent(DeviceLineRecord current) {
		this.current = current;
	}
	
	
	public boolean hasCurrent(){
		return current != null;
	}
	
	public boolean currentHasUp(){
		return current != null && current.getUp_ts() > 0;
	}
	
	public boolean currentHasDown(){
		return current != null && current.getDown_ts() > 0;
	}
}
