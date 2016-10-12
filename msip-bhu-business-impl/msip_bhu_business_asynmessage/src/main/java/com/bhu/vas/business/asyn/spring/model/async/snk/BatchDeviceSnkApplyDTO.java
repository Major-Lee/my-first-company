package com.bhu.vas.business.asyn.spring.model.async.snk;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class BatchDeviceSnkApplyDTO extends AsyncDTO implements IDTO {
	private String snk_type;
	private String template;
	@JsonInclude(Include.NON_NULL)
	private List<String> macs;
	private boolean onlyindexupdate;
	private char dtoType;
	//是否发送portal指令给设备（当把打赏金额时长等参数合入设备portal配置中时，如果只是修改金额和时长，不需要给设备下发portal指令）
	private boolean senddevicecmd = true;
	
	
	public boolean isSenddevicecmd() {
		return senddevicecmd;
	}

	public void setSenddevicecmd(boolean senddevicecmd) {
		this.senddevicecmd = senddevicecmd;
	}

	public List<String> getMacs() {
		return macs;
	}

	public void setMacs(List<String> macs) {
		this.macs = macs;
	}

	public String getSnk_type() {
		return snk_type;
	}

	public void setSnk_type(String snk_type) {
		this.snk_type = snk_type;
	}

	
	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchDeviceSnkApply.getPrefix();
	}

	@Override
	public char getDtoType() {
		return dtoType;
	}
	public void setDtoType(char dtoType) {
		this.dtoType = dtoType;
	}

	public boolean isOnlyindexupdate() {
		return onlyindexupdate;
	}

	public void setOnlyindexupdate(boolean onlyindexupdate) {
		this.onlyindexupdate = onlyindexupdate;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
}
