package com.bhu.vas.di.business.datainit.charging;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.mdto.LineRecords;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeMonthMDTO;
import com.bhu.vas.business.ds.agent.mservice.WifiDeviceWholeDayMService;
import com.bhu.vas.business.ds.agent.mservice.WifiDeviceWholeMonthMService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;

/**
 * 根据日志分析的结果得到设备日汇总数据后，可以对代理商代理的设备进行日汇总
 * @author Edmond
 *
 */
@Service
public class Step04DeviceWholeMonthRecordService {
	
	@Resource
	private UserService userSerivce;
	
	@Resource
	private WifiDeviceWholeDayMService wifiDeviceWholeDayMService;

	@Resource
	private WifiDeviceWholeMonthMService wifiDeviceWholeMonthMService;
	
	private int batch_size = 100;
	/**
	 * 对于解析日志中出现的设备进行月度汇总
	 * @param date
	 * @param lineDeviceRecordsMap
	 * @param lineHandsetRecordsMap
	 */
	public void deviceMonthlyRecord2Mongo(String date,Map<String, LineRecords> lineDeviceRecordsMap){
		Iterator<Entry<String, LineRecords>> iter = lineDeviceRecordsMap.entrySet().iterator();
		List<String> macs = new ArrayList<String>();
		int i = 0;
		while (iter.hasNext()) {
			i++;
			Entry<String, LineRecords> next = iter.next();
			macs.add(next.getKey());
			if(i < batch_size){
				doDeviceWholeCurrentMonth(date,macs);
				macs.clear();
				i=0;
			}
		}
	}
	
	private void doDeviceWholeCurrentMonth(String date,List<String> macs){
		Date certainDate = DateTimeHelper.parseDate(date, DateTimeHelper.FormatPattern5);
		Date monthStartDate = DateTimeExtHelper.getFirstDateOfMonth(certainDate);
		Date monthEndDate = DateTimeExtHelper.getLastDateOfMonth(certainDate);
		
		String monthKey = DateTimeHelper.formatDate(certainDate, DateTimeHelper.FormatPattern11);
		List<RecordSummaryDTO> summary = wifiDeviceWholeDayMService.summaryAggregationBetween(macs, 
				DateTimeHelper.formatDate(monthStartDate, DateTimeHelper.FormatPattern5), 
				DateTimeHelper.formatDate(monthEndDate, DateTimeHelper.FormatPattern5));
		for(RecordSummaryDTO dto:summary){
			WifiDeviceWholeMonthMDTO monthdto = new WifiDeviceWholeMonthMDTO();
			monthdto.setId(WifiDeviceWholeMonthMDTO.generateId(monthKey, dto.getId()));
			monthdto.setDate(monthKey);
			monthdto.setMac(dto.getId());
			monthdto.setDod(dto.getT_dod());
			monthdto.setDct(dto.getT_dct());
			monthdto.setDrx_bytes(dto.getT_drx_bytes());
			monthdto.setDtx_bytes(dto.getT_dtx_bytes());
			monthdto.setHandsets(dto.getT_handsets());
			monthdto.setHct(dto.getT_hct());
			monthdto.setHod(dto.getT_hod());
			monthdto.setHtx_bytes(dto.getT_htx_bytes());
			monthdto.setHrx_bytes(dto.getT_hrx_bytes());
			monthdto.setUpdated_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
			wifiDeviceWholeMonthMService.save(monthdto);
		}
	}
}
