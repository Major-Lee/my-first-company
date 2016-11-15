package com.bhu.vas.api.dto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdDevicePositionVTO implements java.io.Serializable{
	private List<String> positions; 
	private long count = 0L;
	
	public List<String> getPositions() {
		return positions;
	}
	public void setPositions(List<String> positions) {
		this.positions = positions;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
}
