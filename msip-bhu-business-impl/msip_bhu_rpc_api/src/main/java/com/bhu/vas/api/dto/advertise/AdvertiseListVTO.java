package com.bhu.vas.api.dto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdvertiseListVTO implements java.io.Serializable{
	private List<AdvertiseVTO> advertiseList;
	private int pageNumber;
	private int pageSize;
	private int totalPageCount;
	
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public List<AdvertiseVTO> getAdvertiseList() {
		return advertiseList;
	}

	public void setAdvertiseList(List<AdvertiseVTO> advertiseList) {
		this.advertiseList = advertiseList;
	}
	
}
