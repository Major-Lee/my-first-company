package com.bhu.vas.business.asyn.spring.model.tag;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * Created by xiaowei on 4/19/16.
 */
public class BindTagDTO extends ActionDTO {

	private int uid;
	private String message;
	private String tag;
	
	
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



	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
    public String getActionType() {
        return ActionMessageType.DeviceBatchBindTag.getPrefix();
    }
}
