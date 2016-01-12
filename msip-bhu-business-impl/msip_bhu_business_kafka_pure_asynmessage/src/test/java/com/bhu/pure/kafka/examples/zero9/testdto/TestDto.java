package com.bhu.pure.kafka.examples.zero9.testdto;

public class TestDto {
	private int seq;
	private String msg;
	public TestDto() {
	}
	public TestDto(int seq, String msg) {
		super();
		this.seq = seq;
		this.msg = msg;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return "[seq=" + seq + ", msg=" + msg + "]";
	}
	
}
