package com.bhu.vas.api.vto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdDevicePositionVTO implements java.io.Serializable{
	private List<String> positions;
	private List<AdvertiseOccupiedVTO> occupyAds;
	
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
}
