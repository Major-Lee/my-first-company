package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class GeoMapVTO implements Serializable{
	private String lat;//wifi id
	private String lng;//handset id
	private List<GeoMapDeviceVTO> rows;
	
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public List<GeoMapDeviceVTO> getRows() {
		return rows;
	}
	public void setRows(List<GeoMapDeviceVTO> rows) {
		this.rows = rows;
	}
}
