package com.bhu.vas.api.rpc.unifyStatistics.vto;

import java.io.Serializable;
import java.util.Map;
@SuppressWarnings("serial")
public class SsidStatisticsOutLineVTO implements Serializable{
	//出货渠道信息
	private Map<String,SsidOutLine> channelInfos;
	//打赏方式数目统计
	private Map<String,Integer> methodStatistics;

	public Map<String, SsidOutLine> getChannelInfos() {
		return channelInfos;
	}

	public void setChannelInfos(Map<String, SsidOutLine> channelInfos) {
		this.channelInfos = channelInfos;
	}

	public Map<String, Integer> getMethodStatistics() {
		return methodStatistics;
	}

	public void setMethodStatistics(Map<String, Integer> methodStatistics) {
		this.methodStatistics = methodStatistics;
	}
	
}
