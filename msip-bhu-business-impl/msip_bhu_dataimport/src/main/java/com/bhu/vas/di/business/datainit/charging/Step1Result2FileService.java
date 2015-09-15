package com.bhu.vas.di.business.datainit.charging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.agent.mdto.LineRecord;
import com.bhu.vas.business.ds.agent.mdto.LineRecords;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;

@Service
public class Step1Result2FileService {
	public void records2File(String date,Map<String, LineRecords> lineDeviceRecordsMap,Map<String,Map<String,LineRecords>> lineHandsetRecordsMap) throws UnsupportedEncodingException, IOException{
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, LineRecords>> iter = lineDeviceRecordsMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, LineRecords> next = iter.next();
			String key = next.getKey();
			LineRecords  val = next.getValue();
			sb.append("mac:"+key).append(String.format("[%s]", MacDictParserFilterHelper.prefixMactch(key,false,false))).append('\n');
			int times = 0;
			long total_online_duration = 0l;
			for(LineRecord record:val.getRecords()){
				sb.append("		"+record).append('\n');
				times++;
				total_online_duration += record.gaps();
			}
			int handsets = 0;
			Map<String, LineRecords> map = lineHandsetRecordsMap.get(key);
			if(map != null){
				handsets = map.size();
			}
			sb.append(String.format("      统计 次数[%s] 时长[%s] 终端数[%s]", times,DateTimeHelper.getTimeDiff(total_online_duration),handsets)).append('\n');
		}
		
		sb.append("~~~~~~~~~~~~~~~~~~~~~~~gap line~~~~~~~~~~~~~~~~~~\n");
		Iterator<Entry<String, Map<String, LineRecords>>> iter_first = lineHandsetRecordsMap.entrySet().iterator();
		
		while (iter_first.hasNext()) {
			Entry<String, Map<String,LineRecords>> next = iter_first.next();
			String dmac = next.getKey();
			sb.append("dmac:"+dmac).append(String.format("[%s]", MacDictParserFilterHelper.prefixMactch(dmac,false,false))).append('\n');
			Map<String, LineRecords> value = next.getValue();
			Iterator<Entry<String, LineRecords>> iter_second = value.entrySet().iterator();
			while(iter_second.hasNext()){
				Entry<String, LineRecords> next2 = iter_second.next();
				String hmac = next2.getKey();
				sb.append("      hmac:"+hmac).append(String.format("[%s]", MacDictParserFilterHelper.prefixMactch(hmac,false,false))).append('\n');
				LineRecords val = next2.getValue();
				int times = 0;
				long total_online_time = 0l;
				for(LineRecord record:val.getRecords()){
					sb.append("		      "+record).append('\n');
					times++;
					total_online_time += record.gaps();
				}
				sb.append(String.format("		      统计 次数[%s] 时长[%s]", times,DateTimeHelper.getTimeDiff(total_online_time))).append('\n');
			}
		}
		
		FileHelper.generateFile("/BHUData/data/parser/"+date+".txt", new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));
	}
}
