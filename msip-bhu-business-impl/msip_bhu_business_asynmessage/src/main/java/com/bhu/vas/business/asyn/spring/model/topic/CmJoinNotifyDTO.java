package com.bhu.vas.business.asyn.spring.model.topic;

import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.smartwork.msip.cores.helper.StringHelper;

public class CmJoinNotifyDTO extends NotifyDTO {
	private String name;
	private String process_seq;

	@Override
	public String getNotifyType() {
		return ActionMessageType.TOPICCMJoinNotify.getPrefix();
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getProcess_seq() {
		return process_seq;
	}


	public void setProcess_seq(String process_seq) {
		this.process_seq = process_seq;
	}

	public String toString(){
		return name.concat(StringHelper.UNDERLINE_STRING_GAP).concat(process_seq);
	}
}
