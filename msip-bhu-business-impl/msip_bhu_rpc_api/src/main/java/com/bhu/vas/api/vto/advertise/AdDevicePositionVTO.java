package com.bhu.vas.api.vto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdDevicePositionVTO implements java.io.Serializable{
	private List<String> positions;
	private List<AdvertiseOccupiedVTO> occupyAds;
	private int mobilenos;
	
	public List<String> getPositions() {
		return positions;
	}
	public void setPositions(List<String> positions) {
		this.positions = positions;
	}
	public List<AdvertiseOccupiedVTO> getOccupyAds() {
		return occupyAds;
	}
	public void setOccupyAds(List<AdvertiseOccupiedVTO> occupyAds) {
		this.occupyAds = occupyAds;
	}
	
	public int getMobilenos() {
		return mobilenos;
	}
	public void setMobilenos(int mobilenos) {
		this.mobilenos = mobilenos;
	}
	public static boolean isFilter(String str){
		String reg = "^[a-zA-Z]+$";
		return str.trim().substring(0, 1).matches(reg);
	}
}
