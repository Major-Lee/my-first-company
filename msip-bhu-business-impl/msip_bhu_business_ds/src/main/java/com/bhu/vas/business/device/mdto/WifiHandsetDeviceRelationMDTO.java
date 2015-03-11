package com.bhu.vas.business.device.mdto;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * 移动设备接入wifi设备的流水记录
 * @author tangzichao
 *
 */
@Document(collection = "t_wifi_handset_relation")
public class WifiHandsetDeviceRelationMDTO{
	@Id
	private long id;
	//@Field
	//wifi id
	private String wifiId;
	//handset id
	private String handsetId;
	//登录wifi的时间
	private Date login_at;
	
	//getter, setter, toString, Constructors
	public long getId() {
		return id;
	}
	public void setId(long id) {
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
	public Date getLogin_at() {
		return login_at;
	}
	public void setLogin_at(Date login_at) {
		this.login_at = login_at;
	}
}
