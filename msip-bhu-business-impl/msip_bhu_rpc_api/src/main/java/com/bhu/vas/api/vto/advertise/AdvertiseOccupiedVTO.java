package com.bhu.vas.api.vto.advertise;


@SuppressWarnings("serial")
public class AdvertiseOccupiedVTO implements java.io.Serializable{
	private String date;
	private long count;
//	private List<AdvertiseTrashPositionVTO> trashs;
	
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
//	public List<AdvertiseTrashPositionVTO> getTrashs() {
//		return trashs;
//	}
//	public void setTrashs(List<AdvertiseTrashPositionVTO> trashs) {
//		this.trashs = trashs;
//	}
}
