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
import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.mdao.WifiDeviceWholeDayMDao;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeDayMDTO;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceRelationMDTO;

/**
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
		return wifiDeviceWholeDayMDao.findById(WifiHandsetDeviceRelationMDTO.generateId(date, mac));
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
	public List<RecordSummaryDTO> summaryAggregation(List<String> macs,String dateStart,String dateEnd){
		TypedAggregation<WifiDeviceWholeDayMDTO> aggregation = newAggregation(WifiDeviceWholeDayMDTO.class,
				match(Criteria.where("mac").in(macs)),//.and("appId").is(appId)),
			    group("mac").sum("onlinetime").as("total_onlinetime").sum("connecttimes").as("total_connecttimes"),
			    sort(Direction.ASC, "total_onlinetime", "total_connecttimes")
			);
		List<RecordSummaryDTO> aggregate = wifiDeviceWholeDayMDao.aggregate(aggregation, RecordSummaryDTO.class);
		return aggregate;
		/*for(Sumary sumary:aggregate){
			System.out.println("~~~~~~~~~sum");
			System.out.println(sumary.getId());
			System.out.println(sumary.getTotal_amount());
			System.out.println(sumary.getTotal_money());
		}*/
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
