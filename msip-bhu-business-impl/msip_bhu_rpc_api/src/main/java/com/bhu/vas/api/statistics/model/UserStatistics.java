package com.bhu.vas.api.statistics.model;

import com.smartwork.msip.cores.orm.model.BaseIntModel;

@SuppressWarnings("serial")
public class UserStatistics extends BaseIntModel {
	//share 次数
	private long share;
	//subject up次数
	private long s_up;
	//subject down次数
	private long s_down;
	//subject abstract up次数
	private long a_up;
	//subject abstract down次数
	private long a_down;
	//评星次数
	private long estimate;
	
	public long getShare() {
		return share;
	}
	public void setShare(long share) {
		this.share = share;
	}
	public long getS_up() {
		return s_up;
	}
	public void setS_up(long s_up) {
		this.s_up = s_up;
	}
	public long getS_down() {
		return s_down;
	}
	public void setS_down(long s_down) {
		this.s_down = s_down;
	}
	public long getA_up() {
		return a_up;
	}
	public void setA_up(long a_up) {
		this.a_up = a_up;
	}
	public long getA_down() {
		return a_down;
	}
	public void setA_down(long a_down) {
		this.a_down = a_down;
	}
	public long getEstimate() {
		return estimate;
	}
	public void setEstimate(long estimate) {
		this.estimate = estimate;
	}
}
