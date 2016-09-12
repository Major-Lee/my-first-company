package com.bhu.vas.business.asyn.spring.model.async.group;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchGroupSendSortMessageDTO extends AsyncDTO{
	private int taskid;
	private String commdityid;
	
	
	public int getTaskid() {
		return taskid;
	}



	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}


	public String getCommdityid() {
		return commdityid;
	}

	public void setCommdityid(String commdityid) {
		this.commdityid = commdityid;
	}



	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchGroupSendSortMessage.getPrefix();
	}
}
