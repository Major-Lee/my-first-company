package com.bhu.vas.business.bucache.redis.serviceimpl.unique;

public class SequenceRange {
	private long start;
	private long end;
	//旋转 为true的情况下，如果值》=end,则重新从start开始
	private boolean rotate;
	
	public SequenceRange(long start, long end, boolean rotate) {
		this.start = start;
		this.end = end;
		this.rotate = rotate;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public boolean isRotate() {
		return rotate;
	}
	public void setRotate(boolean rotate) {
		this.rotate = rotate;
	}
	
	
	public boolean wasInRange(long current){
		return current>=start && current<end;
	}
}
