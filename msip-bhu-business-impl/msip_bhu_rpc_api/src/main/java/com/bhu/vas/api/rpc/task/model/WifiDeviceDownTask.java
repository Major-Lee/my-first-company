package com.bhu.vas.api.rpc.task.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
/*
 * 下行wifi设备的任务  
 */
@SuppressWarnings("serial")
public class WifiDeviceDownTask extends BaseIntModel{
	public static final String State_Pending = "pending";//待处理状态
	public static final String State_Timeout = "timeout";//任务超时
	
	public static final String State_Done = "done";//完成
	public static final String State_Doing = "doing";//正在做
	public static final String State_None = "none";//没有这个任务
	public static final String State_Next = "next";//周期性上报的任务
	
	public static final String State_Failed = "failed";//任务失败
	public static final String State_Completed = "Completed";//任务已经完成
	
	public static final String Task_LOCAL_CHANNEL = "VAS";
	
	private Integer uid;
	//上下文变量，用于存储页面表单中的extparams内容 在有些指令下发后可能会，收到消息后可能会触发别的指令需要用到此数据
	private String context_var;
	//下发指令内容
	private String payload;
	//任务状态
	private String state = State_Pending;
	//任务类型 OperationCMD no
	private String opt;
	//任务子类型
	private String subopt;
	
	private String task;
	//任务下发的具体设备的mac地址
	private String mac;
	//
	private String time;
	private String serial;
	//给外部其他应用提供的channel值，每个外部应用有个编号
	private String channel = Task_LOCAL_CHANNEL;
	private String channel_taskid;
	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannel_taskid() {
		return channel_taskid;
	}

	public void setChannel_taskid(String channel_taskid) {
		this.channel_taskid = channel_taskid;
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

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getContext_var() {
		return context_var;
	}

	public void setContext_var(String context_var) {
		this.context_var = context_var;
	}
}