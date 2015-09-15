package com.bhu.vas.di.business.datainit.charging;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.agent.mdto.LineRecord;
import com.bhu.vas.business.ds.agent.mdto.LineRecords;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeDayMDTO;
import com.bhu.vas.business.ds.agent.mservice.WifiDeviceWholeDayMService;
@Service
public class Step2DeviceWholeDayRecordService {
	@Resource
	private WifiDeviceWholeDayMService wifiDeviceWholeDayMService;
	public void deviceRecord2Mongo(String date,Map<String, LineRecords> lineDeviceRecordsMap,Map<String,Map<String,LineRecords>> lineHandsetRecordsMap){
		Iterator<Entry<String, LineRecords>> iter = lineDeviceRecordsMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, LineRecords> next = iter.next();
			String key = next.getKey();
			LineRecords  val = next.getValue();
			int times = 0;
			long total_online_duration = 0l;
			for(LineRecord record:val.getRecords()){
				times++;
				total_online_duration += record.gaps();
			}
			int handsets = 0;
			Map<String, LineRecords> map = lineHandsetRecordsMap.get(key);
			if(map != null){
				handsets = map.size();
			}
			WifiDeviceWholeDayMDTO dto = new WifiDeviceWholeDayMDTO();
			dto.setId(WifiDeviceWholeDayMDTO.generateId(date, key));
			dto.setMac(key);
			dto.setDate(date);
			dto.setConnecttimes(times);
			dto.setOnlineduration(total_online_duration);
			dto.setHandsets(handsets);
			dto.setRecords(val.getRecords());
			//TODO:获取此日的设备使用情况流量
			wifiDeviceWholeDayMService.save(dto);
			System.out.println(dto.getId());
		}
	}
}
