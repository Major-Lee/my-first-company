package com.bhu.vas.business.ds.builder;

import java.util.Date;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceAlarmDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceStatusDTO;
import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceAlarm;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceStatus;
/**
 * 用于dto和model之间的转换builder
 * @author tangzichao
 *
 */
public class BusinessModelBuilder {
	
	/*----------------------------DTO to Entity-----------------------------------*/
	
	public static WifiDevice wifiDeviceDtoToEntity(WifiDeviceDTO dto){
		WifiDevice wifi_device_entity = new WifiDevice();
		wifi_device_entity.setId(dto.getMac().toLowerCase());
		wifi_device_entity.setHdtype(dto.getHdtype());
		wifi_device_entity.setOrig_vendor(dto.getOrig_vendor());
		wifi_device_entity.setOrig_model(dto.getOrig_model());
		wifi_device_entity.setOrig_hdver(dto.getOrig_hdver());
		wifi_device_entity.setOrig_swver(dto.getOrig_swver());
		wifi_device_entity.setOem_vendor(dto.getOem_vendor());
		wifi_device_entity.setOem_model(dto.getOem_model());
		wifi_device_entity.setOem_hdver(dto.getOem_hdver());
		wifi_device_entity.setOem_swver(dto.getOem_swver());
		wifi_device_entity.setSn(dto.getSn());
		wifi_device_entity.setIp(dto.getIp());
		wifi_device_entity.setWan_ip(dto.getWan_ip());
		wifi_device_entity.setConfig_sequence(dto.getConfig_sequence());
		wifi_device_entity.setBuild_info(dto.getBuild_info());
		wifi_device_entity.setConfig_model_ver(dto.getConfig_model_ver());
		wifi_device_entity.setConfig_mode(dto.getConfig_mode());
		wifi_device_entity.setWork_mode(dto.getWork_mode());
		wifi_device_entity.setConfig_mode(dto.getConfig_mode());
		wifi_device_entity.setLast_reged_at(new Date());
		wifi_device_entity.setOnline(true);
		return wifi_device_entity;
	}
	
	public static WifiDeviceAlarm wifiDeviceAlarmDtoToEntity(WifiDeviceAlarmDTO dto){
		WifiDeviceAlarm wifi_device_alarm_entity = new WifiDeviceAlarm();
		wifi_device_alarm_entity.setName(dto.getName());
		wifi_device_alarm_entity.setTrapname(dto.getTrapname());
		wifi_device_alarm_entity.setSerial_number(dto.getSerial_number());
		wifi_device_alarm_entity.setNe_name(dto.getNe_name());
		wifi_device_alarm_entity.setMac_addr(dto.getMac_addr().toLowerCase());
		wifi_device_alarm_entity.setAlarm_level(dto.getAlarm_level());
		wifi_device_alarm_entity.setAlarm_type(dto.getAlarm_type());
		wifi_device_alarm_entity.setAlarm_cause(dto.getAlarm_cause());
		wifi_device_alarm_entity.setAlarm_reason(dto.getAlarm_reason());
		wifi_device_alarm_entity.setAlarm_event_time(dto.getAlarm_event_time());
		wifi_device_alarm_entity.setAlarm_status(dto.getAlarm_status());
		wifi_device_alarm_entity.setAlarm_title(dto.getAlarm_title());
		wifi_device_alarm_entity.setAlarm_content(dto.getAlarm_content());
		wifi_device_alarm_entity.setAlarm_serial_id(dto.getAlarm_serial_id());
		return wifi_device_alarm_entity;
	}
	
	public static HandsetDevice handsetDeviceDtoToEntity(HandsetDeviceDTO dto){
		HandsetDevice wifi_device_entity = new HandsetDevice();
		wifi_device_entity.setId(dto.getMac().toLowerCase());
		wifi_device_entity.setPhy_tx_rate(dto.getPhy_tx_rate());
		wifi_device_entity.setPhy_rx_rate(dto.getPhy_rx_rate());
		wifi_device_entity.setData_tx_rate(dto.getData_tx_rate());
		wifi_device_entity.setData_rx_rate(dto.getData_rx_rate());
		wifi_device_entity.setPhy_rate(dto.getPhy_rate());
		wifi_device_entity.setTx_power(dto.getTx_power());
		wifi_device_entity.setRx_chain_num(dto.getRx_chain_num());
		wifi_device_entity.setRssi(dto.getRssi());
		wifi_device_entity.setSnr(dto.getSnr());
		wifi_device_entity.setIdle(dto.getIdle());
		wifi_device_entity.setState(dto.getState());
		wifi_device_entity.setUptime(dto.getUptime());
		wifi_device_entity.setRx_pkts(dto.getRx_pkts());
		wifi_device_entity.setRx_bytes(dto.getRx_bytes());
		wifi_device_entity.setTx_pkts(dto.getTx_pkts());
		wifi_device_entity.setTx_bytes(dto.getTx_bytes());
		wifi_device_entity.setRx_unicast(dto.getRx_unicast());
		wifi_device_entity.setTx_assoc(dto.getTx_assoc());
		wifi_device_entity.setSsid(dto.getSsid());
		wifi_device_entity.setBssid(dto.getBssid().toLowerCase());
		wifi_device_entity.setLocation(dto.getLocation());
		wifi_device_entity.setChannel(dto.getChannel());
		wifi_device_entity.setLast_login_at(new Date());
		wifi_device_entity.setLast_wifi_id(dto.getBssid().toLowerCase());
		wifi_device_entity.setOnline(true);
		return wifi_device_entity;
	}
	
	public static WifiDeviceStatus wifiDeviceStatusDtoToEntity(WifiDeviceStatusDTO dto){
		WifiDeviceStatus wifi_device_status_entity = new WifiDeviceStatus();
		wifi_device_status_entity.setCurrent_cpu_usage(Integer.parseInt(dto.getCurrent_cpu_usage()));
		wifi_device_status_entity.setAverage_cpu_usage(Integer.parseInt(dto.getAverage_cpu_usage()));
		wifi_device_status_entity.setMax_cpu_usage(Integer.parseInt(dto.getMax_cpu_usage()));
		wifi_device_status_entity.setCurrent_mem_usage(Integer.parseInt(dto.getCurrent_mem_usage()));
		wifi_device_status_entity.setAverage_mem_usage(Integer.parseInt(dto.getAverage_mem_usage()));
		wifi_device_status_entity.setMax_mem_usage(Integer.parseInt(dto.getMax_mem_usage()));
		return wifi_device_status_entity;
	}
	
}
