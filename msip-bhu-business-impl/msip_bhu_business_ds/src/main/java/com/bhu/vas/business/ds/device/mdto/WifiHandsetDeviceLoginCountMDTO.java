package com.bhu.vas.business.ds.device.mdto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * 接入wifi设备的移动设备数量
 * 一个移动设备接入同一个wifi设备多次，只增量1次
 * id = wifiId
 * @author tangzichao
 *
 */
@Document(collection = "t_wifi_handset_login_count")
public class WifiHandsetDeviceLoginCountMDTO{
	@Id
	private String id;
	//@Field
	private long count;
	
	public WifiHandsetDeviceLoginCountMDTO(){
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
