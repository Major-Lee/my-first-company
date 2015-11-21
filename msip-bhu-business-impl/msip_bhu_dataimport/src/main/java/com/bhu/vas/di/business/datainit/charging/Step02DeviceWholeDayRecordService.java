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
import com.bhu.vas.business.ds.agent.facade.AgentBillFacadeService;
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
	
	@Resource
	private AgentBillFacadeService agentBillFacadeService;
	public void deviceRecord2Mongo(String date,Map<String, LineRecords> lineDeviceRecordsMap,Map<String,Map<String,LineRecords>> lineHandsetRecordsMap){
		/*List<String> massAps = new ArrayList<String>();
		massAps.add("84:82:f4:19:01:74");
		massAps.add("84:82:f4:19:01:dc");
		massAps.add("84:82:f4:19:02:00");
		massAps.add("84:82:f4:19:02:20");
		massAps.add("84:82:f4:19:02:bc");
		massAps.add("84:82:f4:19:02:ec");
		massAps.add("84:82:f4:19:03:18");
		massAps.add("84:82:f4:19:03:24");
		massAps.add("84:82:f4:19:03:8c");
		massAps.add("84:82:f4:19:03:b0");
		System.out.println("~~~~~~~~~~gogoo~~~~~~~~");*/
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
						if(record.getRx_bytes()>0)
							h_rx_bytes += record.getRx_bytes();
						if(record.getTx_bytes() >0)
							h_tx_bytes += record.getTx_bytes();
					}
				}
			}
			WifiDeviceWholeDayMDTO dto = new WifiDeviceWholeDayMDTO();
			dto.setId(WifiDeviceWholeDayMDTO.generateId(date, key));
			dto.setMac(key);
			dto.setDate(date);
			dto.setDct(times);//.setConnecttimes(times);
			dto.setDod(AgentHelper.millisecond2Minute(total_online_duration));//.setOnlineduration(total_online_duration);
			dto.setRecords(val.getRecords());
			
			dto.setHandsets(handsets);
			dto.setHct(h_ct);
			dto.setHod(AgentHelper.millisecond2Minute(h_od));
			dto.setHrx_bytes(AgentHelper.flowByte2Megabyte(h_rx_bytes));
			dto.setHtx_bytes(AgentHelper.flowByte2Megabyte(h_tx_bytes));
			//dto.setCashback(AgentHelper.validateCashback(dto)?1:0);
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
		int total = dtos.size();
		int count = 0;
		System.out.println("total:"+total);
		Date currentDate = DateTimeHelper.parseDate(date, DateTimeHelper.FormatPattern5);
		if(!dtos.isEmpty()){
			int pagesize = 50;
			int pageno_max = PageHelper.computeLastPageNumber(total, pagesize);
			for(int i = 1;i<= pageno_max;i++){
				List<WifiDeviceWholeDayMDTO> page = PageHelper.pageList(dtos, i, pagesize);
				List<String> macs = new ArrayList<String>();
				for(WifiDeviceWholeDayMDTO dto:page){
					macs.add(dto.getMac());
				}
				if(!macs.isEmpty()){
					System.out.println(macs);
					List<WifiDevice> devices = wifiDeviceService.findByIds(macs, true, true);
					//System.out.println("devices~~~~~~:"+devices.size()+" macs:"+macs);
					int index = 0;
					for(WifiDeviceWholeDayMDTO dto:page){
						WifiDevice device = devices.get(index);
						if(device != null){
							//System.out.println(device.getId() +"    "+massAps.contains(device.getId()) +"  index:"+index);
							//System.out.println(device.getHdtype() + " ~~~~~~ "+device.getAgentuser());
							//非认领成功的设备不进行mongo数据的录入
							//非认领成功的设备不进行mongo数据的录入
							if(AgentHelper.validateDeviceCashbackSupported(device.getHdtype()) && device.getAgentuser() > 0){
								boolean ret = agentBillFacadeService.markFirstCashBack(device.getId(), date);
								System.out.println("markFirstCashBack mac:"+device.getId()+" date:"+date);
								if(ret){
									dto.setSameday(1);
								}
								dto.setCashback(AgentHelper.validateCashback(dto)?1:0);
								wifiDeviceWholeDayMService.save(dto);
								count++;
							}
						}
						
						index++;
					}
				}
			}
		}
		System.out.println("count:"+count);
	}
	
	/*public void deviceRecord2Mongoaaa(String date,Map<String, LineRecords> lineDeviceRecordsMap,Map<String,Map<String,LineRecords>> lineHandsetRecordsMap){
		
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
			
			WifiDevice device = wifiDeviceService.getById(key);
			if(device != null){
				if(AgentHelper.validateDeviceCashbackSupported(device.getHdtype())){
					if(device.getAgentuser() > 0){//认领成功的设备
						boolean ret = agentBillFacadeService.markFirstCashBack(key, date);
						System.out.println(ret);
						if(ret){
							dto.setSameday(1);
						}
						dto.setCashback(AgentHelper.validateCashback(dto)?1:0);
					}
				}
			}
			//TODO:获取此日的设备使用情况流量
			wifiDeviceWholeDayMService.save(dto);
			//System.out.println(dto.getId());
		}
	}*/
}
