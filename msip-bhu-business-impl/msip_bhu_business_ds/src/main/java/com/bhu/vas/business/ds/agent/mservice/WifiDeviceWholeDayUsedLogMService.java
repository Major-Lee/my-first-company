package com.bhu.vas.business.ds.agent.mservice;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.agent.mdao.WifiDeviceWholeDayUsedLogMDao;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeDayUsedLogMDTO;
import com.mongodb.WriteResult;

/**
 *
 */
@Service
public class WifiDeviceWholeDayUsedLogMService {
	
	@Resource
	private WifiDeviceWholeDayUsedLogMDao wifiDeviceWholeDayUsedLogMDao;
	
	
	public WifiDeviceWholeDayUsedLogMDTO save(WifiDeviceWholeDayUsedLogMDTO dto){
		return wifiDeviceWholeDayUsedLogMDao.save(dto);
	}
	
	public WifiDeviceWholeDayUsedLogMDTO getWholeDay(String date, String mac){
		return wifiDeviceWholeDayUsedLogMDao.findById(WifiDeviceWholeDayUsedLogMDTO.generateId(date, mac));
	}
	
	public WriteResult upsertUsedStatus(String date, String mac,WifiDeviceWholeDayUsedLogMDTO dto){
		if(dto == null) return null;
		Query query=Query.query(Criteria.where("id").is(WifiDeviceWholeDayUsedLogMDTO.generateId(date, mac)));
		Update update = new Update();
		update.set("date", date);
		update.set("mac", mac);
        update.set("sta_max_time", dto.getSta_max_time());
        update.set("sta_max_time_num", dto.getSta_max_time_num());
        update.set("flow_max_time", dto.getFlow_max_time());
        update.set("flow_max_time_num", dto.getFlow_max_time_num());
        update.set("time", dto.getTime());
        update.set("tx_bytes", dto.getTx_bytes());
        update.set("rx_bytes", dto.getRx_bytes());
        update.set("sta", dto.getSta());
        return wifiDeviceWholeDayUsedLogMDao.upsert(query, update);//Update.update("tx_bytes",tx_bytes).set("rx_bytes", rx_bytes));
	}
	
	public boolean hasCertainWholeDay(String date, String mac){
		WifiDeviceWholeDayUsedLogMDTO mdto = this.getWholeDay(date, mac);
		return mdto != null;
	}
}
