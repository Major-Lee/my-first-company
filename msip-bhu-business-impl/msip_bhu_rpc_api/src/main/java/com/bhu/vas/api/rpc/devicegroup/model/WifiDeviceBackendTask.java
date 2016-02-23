package com.bhu.vas.api.rpc.devicegroup.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.smartwork.msip.cores.orm.model.BaseLongModel;

/**
 * 针对wifi device 群组生成的后台执行任务
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceBackendTask extends BaseLongModel implements Comparable<WifiDeviceBackendTask>{
	
	public static final String State_Pending = "pending";//待处理状态
	public static final String State_Reading = "reading";//已进入线程，准备处理状态
	public static final String State_Doing = "doing";//正在做
	public static final String State_Timeout = "timeout";//任务超时
	public static final String State_Failed = "failed";//任务失败
	public static final String State_Completed = "completed";//任务已经完成
	
	//此后台搜索群组设备的字符串
	private String message;
	//此任务创建人
	private int uid;
	//符合条件的记录，任务创建的时候和开始执行的时候会进行更新
	private long total;
	//当前执行到多少条，执行过程中进行更新，每千次更新一次
	private long current;
	 
	//任务状态
	private String state = State_Pending;
	//任务类型 OperationCMD no
	private String opt;
	//任务子类型
	private String subopt;
	//上下文变量，用于存储页面表单中的extparams内容 在有些指令下发后可能会，收到消息后可能会触发别的指令需要用到此数据
	private String context_var;
	//任务描述
	private String description;
	private Date started_at;
	private Date completed_at;
	private Date created_at;
	
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
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
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	public String getOpt() {
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
	}
	public String getSubopt() {
		return subopt;
	}
	public void setSubopt(String subopt) {
		this.subopt = subopt;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public long getCurrent() {
		return current;
	}
	public void setCurrent(long current) {
		this.current = current;
	}
	
	public String getContext_var() {
		return context_var;
	}
	public void setContext_var(String context_var) {
		this.context_var = context_var;
	}
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof WifiDeviceBackendTask){
			WifiDeviceBackendTask dto = (WifiDeviceBackendTask)obj;
			if(dto.getId().intValue() == this.id.intValue()) return true;
			else return false;
		}else return false;
	}
	@Override
	public int hashCode() {
		if(this.getId() == null) this.setId(0l);
		return this.getId().hashCode();
	}
	public Date getStarted_at() {
		return started_at;
	}
	public void setStarted_at(Date started_at) {
		this.started_at = started_at;
	}
	public Date getCompleted_at() {
		return completed_at;
	}
	public void setCompleted_at(Date completed_at) {
		this.completed_at = completed_at;
	}
	
	@Override
	public int compareTo(WifiDeviceBackendTask o) {
		
		long thisTime = this.getCreated_at().getTime();
		long anotherTime = o.getCreated_at().getTime();
		return (thisTime>anotherTime ? -1 : (thisTime==anotherTime ? 0 : 1));
		
	}
	
}