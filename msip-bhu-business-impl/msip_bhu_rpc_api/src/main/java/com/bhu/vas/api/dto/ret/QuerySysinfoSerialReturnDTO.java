package com.bhu.vas.api.dto.ret;

/**
 * <?xml version="1.0" encoding="UTF-8"?><return>
	<ITEM index="1" cmd="sysinfo" uptime="18:15:58" memory="30244864/63139840" average_load="1.00/1.00/0.93" 
	firmware_version="AP106P06V1.2.15Build8057" sn="BN207DB900026AA" sys_name="" 
	sys_description="uRouter series wireless AP. Support 802.11b/g/n. 300Mbps, 2T2R." 
	sys_model="Urouter" ap_vendor="BHU" uplink_interface_mac="84:82:f4:19:01:0f" 
	hardware_version="B1" ip="192.168.1.100" netmask="255.255.255.0" gateway="192.168.1.1" 
	firmware_name="kochab" longitude="0.0.0E" latitude="0.0.0N" cpu_type="MIPS 74Kc V4.12" 
	mem_type="DDR2" mem_size="61660" flash_size="16384" sys_net_id="" primary_dns="127.0.0.1" 
	second_dns="0.0.0.0" location="" systime="09:27:26" saved_config="" build_info="2015-07-29-20:01 Revision: 8058" 
	config_model_ver="V3" config_mode="basic" work_mode="router-ap" bmc_status="" status="done"/>
</return>
 * @author tangzichao
 *	设备系统信息dto
 */
@SuppressWarnings("serial")
public class QuerySysinfoSerialReturnDTO extends QuerySerialReturnDTO{
	//当前设备运行时长  返回的格式时间戳[小时:分钟:秒]18:15:58 累加的
	private String uptime;
	private String tfcard_usage;
	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public String getTfcard_usage() {
		return tfcard_usage;
	}

	public void setTfcard_usage(String tfcard_usage) {
		this.tfcard_usage = tfcard_usage;
	}
	
}
