package com.bhu.vas.api.rpc.unifyStatistics.vto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bhu.vas.api.rpc.unifyStatistics.dto.OnlineStatisticsDTO;

@SuppressWarnings("serial")
public class OnlineStatisticsVTO implements Serializable{
	private String name;
	private List <OnlineStatisticsDTO> list;
	
	public List<OnlineStatisticsDTO> getList() {
		return list;
	}
	public void setList(List<OnlineStatisticsDTO> list) {
		this.list = list;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
