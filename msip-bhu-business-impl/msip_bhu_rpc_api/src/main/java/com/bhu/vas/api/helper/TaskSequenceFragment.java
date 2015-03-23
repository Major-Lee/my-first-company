package com.bhu.vas.api.helper;

import com.smartwork.msip.localunit.RandomData;

public class TaskSequenceFragment {
	private int start;
	private int end;
	private int current = 0;
	
	public TaskSequenceFragment(int start, int end) {
		this.start = start;
		this.end = end;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	
	public synchronized int getNextSequence(){
		if(current == 0){
			return RandomData.intNumber(start, end);
		}else{
			if(current >= end){
				current = start;
			}else{
				current++;
			}
			return current;
		}
	}
	
	public boolean wasInFragment(long taskid){
		if(end != -1){
			return ( taskid>=start && taskid <=end);
		}else{//end =-1 代表无穷大
			return taskid>=start;
		}
	}
}
