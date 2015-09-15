package com.bhu.vas.di.business.datainit.charging;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.agent.mdto.LineRecords;

/**
 * 根据日志分析的结果得到设备日汇总数据后，可以对代理商代理的设备进行日汇总
 * @author Edmond
 *
 */
@Service
public class Step4AgentWholeDayRecordService {
	public void handsetRecord2Mongo(String date,Map<String,Map<String,LineRecords>> lineHandsetRecordsMap){
		
	}
}
