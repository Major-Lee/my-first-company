package com.bhu.vas.business.asyn.spring.model.tag;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * Created by xiaowei on 4/19/16.
 */
public class DelTagDTO extends ActionDTO {

	private int uid;
	private String message;
	
	
    public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
    public String getActionType() {
        return ActionMessageType.DeviceBatchDelTag.getPrefix();
    }
}
