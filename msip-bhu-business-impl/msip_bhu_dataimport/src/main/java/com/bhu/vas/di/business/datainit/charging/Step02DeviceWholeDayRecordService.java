package com.bhu.vas.di.business.datainit.charging;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.agent.helper.AgentHelper;
import com.bhu.vas.business.ds.agent.mdto.LineRecord;
import com.bhu.vas.business.ds.agent.mdto.LineRecords;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeDayMDTO;
import com.bhu.vas.business.ds.agent.mservice.WifiDeviceWholeDayMService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
@Service
public class Step02DeviceWholeDayRecordService {
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceWholeDayMService wifiDeviceWholeDayMService;
	
	public void deviceRecord2Mongo(String date,Map<String, LineRecords> lineDeviceRecordsMap,Map<String,Map<String,LineRecords>> lineHandsetRecordsMap){
		List<WifiDeviceWholeDayMDTO> dtos = new ArrayList<WifiDeviceWholeDayMDTO>();
		
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
			int h_ct = 0;
			long h_od = 0l;
			long h_rx_bytes = 0l;
			long h_tx_bytes = 0l;
			Map<String, LineRecords> map = lineHandsetRecordsMap.get(key);
			if(map != null){
				handsets = map.size();
				Iterator<Entry<String, LineRecords>> iter_handset = map.entrySet().iterator();
				while (iter_handset.hasNext()) {
					Entry<String, LineRecords> next_handset = iter_handset.next();
					//String h_mac = next_handset.getKey();
					LineRecords h_records = next_handset.getValue();
					for(LineRecord record:h_records.getRecords()){
						h_ct++;
						h_od += record.gaps();
						h_rx_bytes += record.getRx_bytes();
						h_tx_bytes += record.getTx_bytes();
					}
				}
			}
			WifiDeviceWholeDayMDTO dto = new WifiDeviceWholeDayMDTO();
			dto.setId(WifiDeviceWholeDayMDTO.generateId(date, key));
			dto.setMac(key);
			dto.setDate(date);
			dto.setDct(times);//.setConnecttimes(times);
			dto.setDod(total_online_duration);//.setOnlineduration(total_online_duration);
			dto.setRecords(val.getRecords());
			
			dto.setHandsets(handsets);
			dto.setHct(h_ct);
			dto.setHod(h_od);
			dto.setHrx_bytes(h_rx_bytes);
			dto.setHtx_bytes(h_tx_bytes);
			dto.setCashback(AgentHelper.validateCashback(dto)?1:0);
			dtos.add(dto);
			
			/*WifiDevice device = wifiDeviceService.getById(key);
			if(device != null){
				Date currentDate = DateTimeHelper.parseDate(date, DateTimeHelper.FormatPattern5);
				dto.setSameday(AgentHelper.sameday(device.getCreated_at(), currentDate)?1:0);
			}else{
				dto.setSameday(0);
			}
			//TODO:获取此日的设备使用情况流量
			wifiDeviceWholeDayMService.save(dto);*/
			//System.out.println(dto.getId());
		}
		
		if(!dtos.isEmpty()){
			int total = dtos.size();
			int pagesize = 50;
			int computeLastPageNumber = PageHelper.computeLastPageNumber(total, pagesize);
		}
		
	}
	
	public void deviceRecord2Mongoaaa(String date,Map<String, LineRecords> lineDeviceRecordsMap,Map<String,Map<String,LineRecords>> lineHandsetRecordsMap){
		
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
			int h_ct = 0;
			long h_od = 0l;
			long h_rx_bytes = 0l;
			long h_tx_bytes = 0l;
			Map<String, LineRecords> map = lineHandsetRecordsMap.get(key);
			if(map != null){
				handsets = map.size();
				Iterator<Entry<String, LineRecords>> iter_handset = map.entrySet().iterator();
				while (iter_handset.hasNext()) {
					Entry<String, LineRecords> next_handset = iter_handset.next();
					//String h_mac = next_handset.getKey();
					LineRecords h_records = next_handset.getValue();
					for(LineRecord record:h_records.getRecords()){
						h_ct++;
						h_od += record.gaps();
						h_rx_bytes += record.getRx_bytes();
						h_tx_bytes += record.getTx_bytes();
					}
				}
			}
			WifiDeviceWholeDayMDTO dto = new WifiDeviceWholeDayMDTO();
			dto.setId(WifiDeviceWholeDayMDTO.generateId(date, key));
			dto.setMac(key);
			dto.setDate(date);
			dto.setDct(times);//.setConnecttimes(times);
			dto.setDod(total_online_duration);//.setOnlineduration(total_online_duration);
			dto.setRecords(val.getRecords());
			
			dto.setHandsets(handsets);
			dto.setHct(h_ct);
			dto.setHod(h_od);
			dto.setHrx_bytes(h_rx_bytes);
			dto.setHtx_bytes(h_tx_bytes);
			dto.setCashback(AgentHelper.validateCashback(dto)?1:0);
			WifiDevice device = wifiDeviceService.getById(key);
			if(device != null){
				Date currentDate = DateTimeHelper.parseDate(date, DateTimeHelper.FormatPattern5);
				dto.setSameday(AgentHelper.sameday(device.getCreated_at(), currentDate)?1:0);
			}else{
				dto.setSameday(0);
			}
			
			//TODO:获取此日的设备使用情况流量
			wifiDeviceWholeDayMService.save(dto);
			//System.out.println(dto.getId());
		}
	}
}
