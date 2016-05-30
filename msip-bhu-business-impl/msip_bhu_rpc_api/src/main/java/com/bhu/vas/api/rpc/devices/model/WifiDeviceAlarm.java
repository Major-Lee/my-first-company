package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
/*
 * 移动设备基础信息
 */
@SuppressWarnings("serial")
public class WifiDeviceAlarm extends BaseIntModel{
	//告警名称
	private String name;
	//告警英文名称
	private String trapname;
	//设备序列号
	private String serial_number;
	//网元编码
	private String ne_name;
	//wifi mac
	private String mac_addr;
	//告警等级
	private String alarm_level;
	//告警类别
	private String alarm_type;
	//
	private String alarm_cause;
	//告警原因
	private String alarm_reason;
	//告警触发时间
	private String alarm_event_time;
	//告警状态
	private String alarm_status;
	//告警标题
	private String alarm_title;
	//告警内容
	private String alarm_content;
	//告警id
	private String alarm_serial_id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTrapname() {
		return trapname;
	}

	public void setTrapname(String trapname) {
		this.trapname = trapname;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	public String getNe_name() {
		return ne_name;
	}

	public void setNe_name(String ne_name) {
		this.ne_name = ne_name;
	}

	public String getMac_addr() {
		return mac_addr;
	}

	public void setMac_addr(String mac_addr) {
		this.mac_addr = mac_addr;
	}

	public String getAlarm_level() {
		return alarm_level;
	}

	public void setAlarm_level(String alarm_level) {
		this.alarm_level = alarm_level;
	}

	public String getAlarm_type() {
		return alarm_type;
	}

	public void setAlarm_type(String alarm_type) {
		this.alarm_type = alarm_type;
	}

	public String getAlarm_cause() {
		return alarm_cause;
	}

	public void setAlarm_cause(String alarm_cause) {
		this.alarm_cause = alarm_cause;
	}

	public String getAlarm_reason() {
		return alarm_reason;
	}

	public void setAlarm_reason(String alarm_reason) {
		this.alarm_reason = alarm_reason;
	}

	public String getAlarm_event_time() {
		return alarm_event_time;
	}

	public void setAlarm_event_time(String alarm_event_time) {
		this.alarm_event_time = alarm_event_time;
	}

	public String getAlarm_status() {
		return alarm_status;
	}

	public void setAlarm_status(String alarm_status) {
		this.alarm_status = alarm_status;
	}

	public String getAlarm_title() {
		return alarm_title;
	}

	public void setAlarm_title(String alarm_title) {
		this.alarm_title = alarm_title;
	}

	public String getAlarm_content() {
		return alarm_content;
	}

	public void setAlarm_content(String alarm_content) {
		this.alarm_content = alarm_content;
	}

	public String getAlarm_serial_id() {
		return alarm_serial_id;
	}

	public void setAlarm_serial_id(String alarm_serial_id) {
		this.alarm_serial_id = alarm_serial_id;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}