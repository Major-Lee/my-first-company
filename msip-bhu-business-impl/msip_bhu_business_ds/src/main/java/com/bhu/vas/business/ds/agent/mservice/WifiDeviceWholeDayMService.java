package com.bhu.vas.business.ds.agent.mservice;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.mdao.WifiDeviceWholeDayMDao;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeDayMDTO;
import com.mongodb.WriteResult;

/**
 * 
 * @author Edmond
 *
 */
@Service
public class WifiDeviceWholeDayMService {
	
	@Resource
	private WifiDeviceWholeDayMDao wifiDeviceWholeDayMDao;
	
	
	public WifiDeviceWholeDayMDTO save(WifiDeviceWholeDayMDTO dto){
		return wifiDeviceWholeDayMDao.save(dto);
	}
	
	public WifiDeviceWholeDayMDTO getWholeDay(String date, String mac){
		return wifiDeviceWholeDayMDao.findById(WifiDeviceWholeDayMDTO.generateId(date, mac));
	}
	
	public WifiDeviceWholeDayMDTO findAndModifyFlowBytes(String date, String mac,long tx_bytes,long rx_bytes){
		Query query=Query.query(Criteria.where("id").is(WifiDeviceWholeDayMDTO.generateId(date, mac)));
		Update update = new Update();
        update.set("d_tx_bytes", tx_bytes);
        update.set("d_rx_bytes", rx_bytes);
		return wifiDeviceWholeDayMDao.findAndModify(query, update);//Update.update("tx_bytes",tx_bytes).set("rx_bytes", rx_bytes));
	}
	
	public WriteResult upsertFlowBytes(String date, String mac,long tx_bytes,long rx_bytes){
		Query query=Query.query(Criteria.where("id").is(WifiDeviceWholeDayMDTO.generateId(date, mac)));
		Update update = new Update();
        update.set("d_tx_bytes", tx_bytes);
        update.set("d_rx_bytes", rx_bytes);
        return wifiDeviceWholeDayMDao.upsert(query, update);//Update.update("tx_bytes",tx_bytes).set("rx_bytes", rx_bytes));
	}
	
	public boolean hasCertainWholeDay(String date, String mac){
		WifiDeviceWholeDayMDTO mdto = this.getWholeDay(date, mac);
		return mdto != null;
	}
	
	public List<WifiDeviceWholeDayMDTO> findAllByDeviceBetween(String mac,String dateStart,String dateEnd){
		Query query=Query.query(Criteria.where("mac").is(mac).and("date").gte(dateStart).lte(dateEnd)).with(new Sort(Direction.DESC,"date"));
		return wifiDeviceWholeDayMDao.find(query);
	}
	
	public List<WifiDeviceWholeDayMDTO> findAllByDevice(String mac){
		Query query=Query.query(Criteria.where("mac").is(mac)).with(new Sort(Direction.DESC,"date"));
		return wifiDeviceWholeDayMDao.find(query);
	}
	
	/**
	 * 统计指定时间段内 
	 * @param mac
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<RecordSummaryDTO> summaryAggregationBetween(List<String> macs,String dateStart,String dateEnd){
		TypedAggregation<WifiDeviceWholeDayMDTO> aggregation = newAggregation(WifiDeviceWholeDayMDTO.class,
				match(Criteria.where("mac").in(macs).and("date").gte(dateStart).lte(dateEnd)),
			    group("mac")
			    	.sum("dod").as("t_dod")
			    	.sum("dct").as("t_dct")
			    	.sum("dtx_bytes").as("t_dtx_bytes")
			    	.sum("drx_bytes").as("t_drx_bytes")
			    	//.sum("handsets").as("t_handsets")
			    	.sum("hod").as("t_hod")
			    	.sum("hct").as("t_hct")
			    	.sum("htx_bytes").as("t_htx_bytes")
			    	.sum("hrx_bytes").as("t_hrx_bytes")
			    	.sum("handsets").as("t_handsets"),
			    sort(Direction.ASC, "t_dod", "t_dct")
			);
		List<RecordSummaryDTO> aggregate = wifiDeviceWholeDayMDao.aggregate(aggregation, RecordSummaryDTO.class);
		return aggregate;
	}
	
	/**
	 * 获取指定日期的所有的mac地址的汇总数据
	 * @param macs
	 * @param date
	 * @return
	 */
	public List<RecordSummaryDTO> summaryAggregationWith(List<String> macs,String date){
		TypedAggregation<WifiDeviceWholeDayMDTO> aggregation = newAggregation(WifiDeviceWholeDayMDTO.class,
				match(Criteria.where("mac").in(macs).and("date").is(date)),
			    group("date")
			    	.sum("dod").as("t_dod")
			    	.sum("dct").as("t_dct")
			    	.sum("dtx_bytes").as("t_dtx_bytes")
			    	.sum("drx_bytes").as("t_drx_bytes")
			    	//.sum("handsets").as("t_handsets")
			    	.sum("hod").as("t_hod")
			    	.sum("hct").as("t_hct")
			    	.sum("htx_bytes").as("t_htx_bytes")
			    	.sum("hrx_bytes").as("t_hrx_bytes")
			    	.sum("handsets").as("t_handsets"),
			    sort(Direction.ASC, "t_dod", "t_dct")
			);
		List<RecordSummaryDTO> aggregate = wifiDeviceWholeDayMDao.aggregate(aggregation, RecordSummaryDTO.class);
		return aggregate;
	}
	
	public long countAll(){
		return wifiDeviceWholeDayMDao.count(new Query());
	}
	/*
	public void incrCount(String wifiId, long incr){
		Query query = new Query(Criteria.where("_id").is(wifiId));
		Update update = new Update();
		update.inc("count", incr);
		wifiHandsetDeviceLoginCountMDao.upsert(query, update);
	}
	
	*//**
	 * 按照count从大到小排序
	 * @param pageNo
	 * @param pageSize
	 * @return
	 *//*
	public Pagination<WifiHandsetDeviceLoginCountMDTO> findWifiDevicesOrderMaxHandset(int pageNo, int pageSize){
		Query query = new Query();
		query.with(new Sort(Direction.DESC,"count"));
		return wifiHandsetDeviceLoginCountMDao.findPagination(pageNo, pageSize, query);
	}*/
	
	
}
