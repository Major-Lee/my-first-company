package com.bhu.vas.api.rpc.unifyStatistics.vto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import com.bhu.vas.api.rpc.unifyStatistics.dto.OnlineStatisticsDTO;

@SuppressWarnings("serial")
public class OnlineStatisticsVTO implements Serializable{
	private String name;
	private ArrayList<OnlineStatisticsDTO> list ;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<OnlineStatisticsDTO> getList() {
		return list;
	}
	public void setList(ArrayList<OnlineStatisticsDTO> list) {
		this.list = list;
	}
}
