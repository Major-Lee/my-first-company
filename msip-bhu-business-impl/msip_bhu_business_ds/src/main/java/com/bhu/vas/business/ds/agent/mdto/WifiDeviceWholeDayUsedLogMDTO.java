package com.bhu.vas.business.ds.agent.mdto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bhu.vas.api.dto.redis.element.DailyUsedStatisticsDTO;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * wifi设备每日全天流量统计数据
 * 此数据来源于设备使用情况
 * @author Edmond
 */
@Document(collection = "t_wifi_device_wholeday_usedlog")
public class WifiDeviceWholeDayUsedLogMDTO {
	/**
	 * 规则为 yyyy-MM-dd_mac
	 */
	@Id
	private String id;
	//yyyyMMdd
	private String date;
	private String mac;
	private int sta_max_time;
	private int sta_max_time_num;
	private int flow_max_time;
	private long flow_max_time_num;
	
	//private long time;
	private long tx_bytes;
	private long rx_bytes;
	private int sta;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public int getSta_max_time() {
		return sta_max_time;
	}
	public void setSta_max_time(int sta_max_time) {
		this.sta_max_time = sta_max_time;
	}
	public int getSta_max_time_num() {
		return sta_max_time_num;
	}
	public void setSta_max_time_num(int sta_max_time_num) {
		this.sta_max_time_num = sta_max_time_num;
	}
	public int getFlow_max_time() {
		return flow_max_time;
	}
	public void setFlow_max_time(int flow_max_time) {
		this.flow_max_time = flow_max_time;
	}
	public long getFlow_max_time_num() {
		return flow_max_time_num;
	}
	public void setFlow_max_time_num(long flow_max_time_num) {
		this.flow_max_time_num = flow_max_time_num;
	}
	/*public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}*/
	public long getTx_bytes() {
		return tx_bytes;
	}
	public void setTx_bytes(long tx_bytes) {
		this.tx_bytes = tx_bytes;
	}
	public long getRx_bytes() {
		return rx_bytes;
	}
	public void setRx_bytes(long rx_bytes) {
		this.rx_bytes = rx_bytes;
	}
	public int getSta() {
		return sta;
	}
	public void setSta(int sta) {
		this.sta = sta;
	}
	public static String generateId(String date, String mac){
		StringBuffer idstring = new StringBuffer();
		idstring.append(date).append(StringHelper.UNDERLINE_STRING_GAP).append(mac);
		return idstring.toString();
	}
	
	public static WifiDeviceWholeDayUsedLogMDTO fromDailyUsedStatisticsDTO(String date,String mac,DailyUsedStatisticsDTO dto){
		if(dto == null) return null;
		WifiDeviceWholeDayUsedLogMDTO result = new WifiDeviceWholeDayUsedLogMDTO();
		result.setId(generateId(date,mac));
		result.setDate(date);
		result.setMac(mac);
		result.setSta_max_time(Integer.parseInt(dto.getSta_max_time_num()));
		result.setSta_max_time_num(Integer.parseInt(dto.getSta_max_time_num()));
		result.setFlow_max_time(Integer.parseInt(dto.getFlow_max_time() == null?"0":dto.getFlow_max_time()));
		result.setFlow_max_time_num(Long.parseLong(dto.getFlow_max_time_num()== null?"0":dto.getFlow_max_time_num()));
		result.setSta(Integer.parseInt(dto.getSta()));
		//result.setTime(Long.parseLong(dto.getTime()));
		result.setRx_bytes(Long.parseLong(dto.getRx_bytes()));
		result.setTx_bytes(Long.parseLong(dto.getTx_bytes()));
		return result;
	}
}
