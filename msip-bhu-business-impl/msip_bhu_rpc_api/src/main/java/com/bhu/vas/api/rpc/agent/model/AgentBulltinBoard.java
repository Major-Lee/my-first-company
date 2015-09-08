package com.bhu.vas.api.rpc.agent.model;

import java.util.Date;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseLongModel;

/**
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class AgentBulltinBoard extends BaseLongModel implements IRedisSequenceGenable{
	//发布者
    private int publisher;
    //消费者
    private int consumer;
    //公告类别
    private String type;
    //公告内容
    private String content;

    private Date created_at;
    
    public int getPublisher() {
		return publisher;
	}

	public void setPublisher(int publisher) {
		this.publisher = publisher;
	}

	public int getConsumer() {
		return consumer;
	}

	public void setConsumer(int consumer) {
		this.consumer = consumer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	@Override
	public void setSequenceKey(Long key) {
		this.id = key;
	}
	
}
