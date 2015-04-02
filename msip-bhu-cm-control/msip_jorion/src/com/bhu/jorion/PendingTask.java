package com.bhu.jorion;

import java.util.UUID;

import javax.jms.TextMessage;

public class PendingTask {
	public final static int STATUS_PENDING = 0;
	public final static int STATUS_SENDING = 1;
	public final static int STATUS_SENT = 2;
	
	private long taskid;
	private TextMessage message;
	private int status;
	private UUID msgid;

	public UUID getMsgid() {
		return msgid;
	}
	public void setMsgid(UUID msgid) {
		this.msgid = msgid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getTaskid() {
		return taskid;
	}
	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}
	public TextMessage getMessage() {
		return message;
	}
	public void setMessage(TextMessage message) {
		this.message = message;
	}
	
	public PendingTask(long taskid,
			TextMessage message) {
		super();
		this.taskid = taskid;
		this.message = message;
		this.status = PendingTask.STATUS_PENDING;
		this.msgid = UUID.randomUUID();
	}
}
