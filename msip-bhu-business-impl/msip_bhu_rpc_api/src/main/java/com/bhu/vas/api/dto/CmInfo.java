package com.bhu.vas.api.dto;

import com.smartwork.msip.cores.helper.StringHelper;

public class CmInfo {
	private String name;
	private String thread;
	private String ip;
	
	public CmInfo(String name, String thread, String ip) {
		super();
		this.name = name;
		this.thread = thread;
		this.ip = ip;
	}
	public CmInfo() {
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String toInQueueString(){
		return "in_".concat(toString());
	}
	public String toOutQueueString(){
		return "out_".concat(toString());
	}
	public String toString(){
		return name.concat(StringHelper.UNDERLINE_STRING_GAP).concat(thread);
	}
}
