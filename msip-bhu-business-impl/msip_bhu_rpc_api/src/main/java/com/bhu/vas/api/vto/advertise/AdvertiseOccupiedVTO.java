package com.bhu.vas.api.vto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdvertiseOccupiedVTO implements java.io.Serializable{
	private String date;
	private List<AdvertiseTrashPositionVTO> trashs;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<AdvertiseTrashPositionVTO> getTrashs() {
		return trashs;
	}
	public void setTrashs(List<AdvertiseTrashPositionVTO> trashs) {
		this.trashs = trashs;
	}
}
