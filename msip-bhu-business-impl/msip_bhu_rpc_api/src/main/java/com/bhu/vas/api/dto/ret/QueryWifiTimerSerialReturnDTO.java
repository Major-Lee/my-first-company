package com.bhu.vas.api.dto.ret;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 根据vap名称获取终端列表的返回头信息
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class QueryWifiTimerSerialReturnDTO extends QuerySerialReturnDTO{
	
	private static final String Timer_On = "on";
	private static final String Timer_Off = "off";
	private String enable;
	//设备的 "18:00:00-on,05:00:00-off" 
	private String rule;
	
	private String start_time;
	private String stop_time;
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
		
		if(StringUtils.isNotEmpty(rule)){
			String[] array = rule.split(StringHelper.COMMA_STRING_GAP);
			if(array.length == 2){
				String[] inner_array = array[0].split(StringHelper.MINUS_STRING_GAP);
				if(inner_array.length == 2){
					if(Timer_On.equals(inner_array[1])){
						this.setStart_time(inner_array[0]);
					}
					
					if(Timer_Off.equals(inner_array[1])){
						this.setStart_time(inner_array[0]);
					}
				}
			}
		}
	}
	
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getStop_time() {
		return stop_time;
	}
	public void setStop_time(String stop_time) {
		this.stop_time = stop_time;
	}
	
}
