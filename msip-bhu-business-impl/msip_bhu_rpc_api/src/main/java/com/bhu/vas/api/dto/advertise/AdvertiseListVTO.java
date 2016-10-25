package com.bhu.vas.api.dto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdvertiseListVTO implements java.io.Serializable{
	private List<AdvertiseVTO> advertiseList;

	public List<AdvertiseVTO> getAdvertiseList() {
		return advertiseList;
	}

	public void setAdvertiseList(List<AdvertiseVTO> advertiseList) {
		this.advertiseList = advertiseList;
	}
	
}
