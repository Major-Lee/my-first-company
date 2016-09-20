package com.bhu.vas.business.asyn.spring.model.async.tag;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;
import com.bhu.vas.business.asyn.spring.model.IDTO;

/**
 * 
 * @author Edmond
 *
 */
public class OperTagDTO extends AsyncDTO implements IDTO{

	private int uid;
	private String message;
	private String tag;
	private char dtoType;
	
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
    public String getAsyncType() {
        return AsyncMessageType.BatchDeviceOperTag.getPrefix();
    }

	@Override
	public char getDtoType() {
		return dtoType;
	}
	public void setDtoType(char dtoType) {
		this.dtoType = dtoType;
	}
}
