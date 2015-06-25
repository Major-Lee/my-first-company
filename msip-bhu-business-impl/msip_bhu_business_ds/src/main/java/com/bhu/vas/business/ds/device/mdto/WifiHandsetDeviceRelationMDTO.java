package com.bhu.vas.business.ds.device.mdto;

import com.bhu.vas.api.mdto.WifiHandsetDeviceItemDetailMDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.smartwork.msip.cores.helper.StringHelper;

import java.util.List;
import java.util.Map;

/**
 * 移动设备接入wifi设备的接入记录
 * 一个移动设备接入同一个wifi设备多次，只有一条记录
 * id = wifiId_handsetId
 * @author tangzichao
 *
 */
@Document(collection = "t_wifi_handset_relation")
public class WifiHandsetDeviceRelationMDTO{
	@Id
	private String id;
	//@Field
	//wifi id
	private String wifiId;
	//handset id
	private String handsetId;
	//最后登录wifi的时间 用string存储格式化好的日期 因为直接用date，mongodb就会用标准时间存储
	private String last_login_at;

	//todo(bluesand): 累加终端的下载字节数会越来越大
	private long total_rx_bytes;

	private Map<String, List<WifiHandsetDeviceItemDetailMDTO>> items;

	public WifiHandsetDeviceRelationMDTO(){
		
	}
	
	public WifiHandsetDeviceRelationMDTO(String wifiId, String handsetId){
		this.id = generateId(wifiId, handsetId);
		this.wifiId = wifiId;
		this.handsetId = handsetId;
	}
	
	public static String generateId(String wifiId, String handsetId){
		StringBuffer idstring = new StringBuffer();
		idstring.append(wifiId).append(StringHelper.UNDERLINE_STRING_GAP).append(handsetId);
		return idstring.toString();
	}
	
	//getter, setter, toString, Constructors
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWifiId() {
		return wifiId;
	}
	public void setWifiId(String wifiId) {
		this.wifiId = wifiId;
	}
	public String getHandsetId() {
		return handsetId;
	}
	public void setHandsetId(String handsetId) {
		this.handsetId = handsetId;
	}
	public String getLast_login_at() {
		return last_login_at;
	}
	public void setLast_login_at(String last_login_at) {
		this.last_login_at = last_login_at;
	}

	public Map<String, List<WifiHandsetDeviceItemDetailMDTO>> getItems() {
		return items;
	}

	public void setItems(Map<String, List<WifiHandsetDeviceItemDetailMDTO>> items) {
		this.items = items;
	}

	public long getTotal_rx_bytes() {
		return total_rx_bytes;
	}

	public void setTotal_rx_bytes(long total_rx_bytes) {
		this.total_rx_bytes = total_rx_bytes;
	}
}
