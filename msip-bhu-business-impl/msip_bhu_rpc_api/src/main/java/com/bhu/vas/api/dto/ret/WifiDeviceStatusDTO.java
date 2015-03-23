package com.bhu.vas.api.dto.ret;

/**
 * wifi设备的状态DTO (cpu,内存利用率)
 * <return>
        <ITEM index="1" cmd="sysperf" current_cpu_usage="4" average_cpu_usage="3" max_cpu_usage="4" 
        current_mem_usage="79" average_mem_usage="79" max_mem_usage="79" status="done" />
   </return>
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceStatusDTO extends QuerySerialReturnDTO{
	private long index;
	//当前cpu占用
	private String current_cpu_usage;
	//平均cpu占用
	private String average_cpu_usage;
	//最大cpu占用
	private String max_cpu_usage;
	//当前内存占用
	private String current_mem_usage;
	//平均内存占用
	private String average_mem_usage;
	//最大内存占用
	private String max_mem_usage;
	
	public long getIndex() {
		return index;
	}
	public void setIndex(long index) {
		this.index = index;
	}
	public String getCurrent_cpu_usage() {
		return current_cpu_usage;
	}
	public void setCurrent_cpu_usage(String current_cpu_usage) {
		this.current_cpu_usage = current_cpu_usage;
	}
	public String getAverage_cpu_usage() {
		return average_cpu_usage;
	}
	public void setAverage_cpu_usage(String average_cpu_usage) {
		this.average_cpu_usage = average_cpu_usage;
	}
	public String getMax_cpu_usage() {
		return max_cpu_usage;
	}
	public void setMax_cpu_usage(String max_cpu_usage) {
		this.max_cpu_usage = max_cpu_usage;
	}
	public String getCurrent_mem_usage() {
		return current_mem_usage;
	}
	public void setCurrent_mem_usage(String current_mem_usage) {
		this.current_mem_usage = current_mem_usage;
	}
	public String getAverage_mem_usage() {
		return average_mem_usage;
	}
	public void setAverage_mem_usage(String average_mem_usage) {
		this.average_mem_usage = average_mem_usage;
	}
	public String getMax_mem_usage() {
		return max_mem_usage;
	}
	public void setMax_mem_usage(String max_mem_usage) {
		this.max_mem_usage = max_mem_usage;
	}
}