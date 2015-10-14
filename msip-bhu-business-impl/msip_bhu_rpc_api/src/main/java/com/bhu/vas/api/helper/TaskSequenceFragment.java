package com.bhu.vas.api.helper;

import java.util.concurrent.atomic.AtomicLong;

import com.smartwork.msip.localunit.RandomData;

public class TaskSequenceFragment {
	private long start;
	private long end;
	private AtomicLong current;
	//private int current = 0;
	public TaskSequenceFragment(long start, long end) {
		this.start = start;
		this.end = end;
		current = new AtomicLong(RandomData.longNumber(start, end));
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
	public void setEnd(long end) {
		this.end = end;
	}
	/*public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}*/
	
	public long getNextSequence(){
		long result = current.incrementAndGet();
		if(result >= end){
			current.set(start);
			return current.get();
		}
		return result;
		/*if(current == 0){
			current = RandomData.intNumber(start, end);
			return RandomData.intNumber(start, end);
		}else{
			if(current >= end){
				current = start;
			}else{
				current++;
			}
			return current;
		}*/
	}
	
	public boolean wasInFragment(long taskid){
		return ( taskid>=start && taskid <=end);
		/*if(end != -1){
			return ( taskid>=start && taskid <=end);
		}else{//end =-1 代表无穷大
			return taskid>=start;
		}*/
	}
}
