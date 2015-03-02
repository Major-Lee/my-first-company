package com.bhu.vas.api.subject.dto;

public class ClickDTO {
	private int id;
	private long up;
	private long down;
	private long incr;
	
	public ClickDTO() {
	}
	public ClickDTO(int id, long up, long down) {
		super();
		this.id = id;
		this.up = up;
		this.down = down;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getUp() {
		return up;
	}
	public void setUp(long up) {
		this.up = up;
	}
	public long getDown() {
		return down;
	}
	public void setDown(long down) {
		this.down = down;
	}
	
	public long getScore(){
		return (up>=down?(up-down):0);
	}
	
	public long getIncr() {
		return incr;
	}
	public void setIncr(long incr) {
		this.incr = incr;
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(" id:").append(id);
		sb.append(" up:").append(up);
		sb.append(" down:").append(down);
		return sb.toString();
	}
}
