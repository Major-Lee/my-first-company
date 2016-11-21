package com.bhu.vas.api.vto.advertise;

import java.util.List;


@SuppressWarnings("serial")
public class AdvertiseOccupiedVTO implements java.io.Serializable{
	private String date;
	private int count;
	private List<AdvertiseTrashPositionVTO> trashs;
	
	public int getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = (int) (count*1.1);
	}
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
