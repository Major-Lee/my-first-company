package com.bhu.vas.api.dto.ret;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 根据vap名称获取终端列表的返回头信息
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class QueryWifiTimerSerialReturnDTO extends QuerySerialReturnDTO{
	

	private String enable;
	//设备的 "18:00:00-on,05:00:00-off"
	//设备的 "18:00:00-on,05:00:00-off,-days" 
	//设备的 "18:00:00-on,05:00:00-off,1234-days" 
	private String rule;
	
	private String start;
	private String end;
	private String days;
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
			if(array.length >= 2){
				for(String element:array){
					String[] inner_array = element.split(StringHelper.MINUS_STRING_GAP);
					if(inner_array.length == 2){
						if(WifiDeviceHelper.WifiTimer_Timer_On.equals(inner_array[1].trim())){
							this.setStart(inner_array[0]);
						}
						
						if(WifiDeviceHelper.WifiTimer_Timer_Off.equals(inner_array[1].trim())){
							this.setEnd(inner_array[0]);
						}
						if(WifiDeviceHelper.WifiTimer_Days.equals(inner_array[1].trim())){
							this.setDays(inner_array[0]);
						}
					}
				}
			}
		}
	}
	
	public boolean hasRule(){
		return StringUtils.isNotEmpty(rule);
	}
	
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	
}
