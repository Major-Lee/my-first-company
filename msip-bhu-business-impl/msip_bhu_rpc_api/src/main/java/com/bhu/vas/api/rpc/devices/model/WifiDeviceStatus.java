package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/**
 * wifi设备运行状态
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceStatus extends BaseStringModel{
	//当前cpu占用比例
	private int current_cpu_usage;
	//平均cpu占用比例
	private int average_cpu_usage;
	//最大cpu占用比例
	private int max_cpu_usage;
	//当前内存占用比例
	private int current_mem_usage;
	//平均内存占用比例
	private int average_mem_usage;
	//最大内存占用比例
	private int max_mem_usage;
	
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

	public int getCurrent_cpu_usage() {
		return current_cpu_usage;
	}

	public void setCurrent_cpu_usage(int current_cpu_usage) {
		this.current_cpu_usage = current_cpu_usage;
	}

	public int getAverage_cpu_usage() {
		return average_cpu_usage;
	}

	public void setAverage_cpu_usage(int average_cpu_usage) {
		this.average_cpu_usage = average_cpu_usage;
	}

	public int getMax_cpu_usage() {
		return max_cpu_usage;
	}

	public void setMax_cpu_usage(int max_cpu_usage) {
		this.max_cpu_usage = max_cpu_usage;
	}

	public int getCurrent_mem_usage() {
		return current_mem_usage;
	}

	public void setCurrent_mem_usage(int current_mem_usage) {
		this.current_mem_usage = current_mem_usage;
	}

	public int getAverage_mem_usage() {
		return average_mem_usage;
	}

	public void setAverage_mem_usage(int average_mem_usage) {
		this.average_mem_usage = average_mem_usage;
	}

	public int getMax_mem_usage() {
		return max_mem_usage;
	}

	public void setMax_mem_usage(int max_mem_usage) {
		this.max_mem_usage = max_mem_usage;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}