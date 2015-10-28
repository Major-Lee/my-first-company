package com.bhu.jorion;

import java.util.UUID;

import javax.jms.TextMessage;

public class PendingTask {
	public final static int STATUS_PENDING = 0;
	public final static int STATUS_SENDING = 1;
	public final static int STATUS_SENT = 2;
	
	public final static int TASK_TYPE_XML = 1;
	public final static int TASK_TYPE_FILE = 2;

	private long taskid;
	private long type;
	private int rev;
	private TextMessage message;
	private int status;
	private UUID msgid;
	
	public long getType() {
		return type;
	}
	public void setType(long type) {
		this.type = type;
	}
	public int getRev() {
		return rev;
	}
	public void setRev(int rev) {
		this.rev = rev;
	}
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
	
	public PendingTask(int rev, long taskid, int type,
			TextMessage message) {
		super();
		this.rev = rev;
		this.taskid = taskid;
		this.type = type;
		this.message = message;
		this.status = PendingTask.STATUS_PENDING;
		this.msgid = UUID.randomUUID();
	}
}
