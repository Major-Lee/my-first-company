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
import com.bhu.vas.business.ds.agent.mdao.WifiDeviceWholeMonthMDao;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeMonthMDTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 *
 */
@Service
public class WifiDeviceWholeMonthMService {
	
	@Resource
	private WifiDeviceWholeMonthMDao wifiDeviceWholeMonthMDao;
	
	public WifiDeviceWholeMonthMDTO save(WifiDeviceWholeMonthMDTO dto){
		return wifiDeviceWholeMonthMDao.save(dto);
	}
	
	
	public WifiDeviceWholeMonthMDTO getWholeMonth(String date, String mac){
		return wifiDeviceWholeMonthMDao.findById(WifiDeviceWholeMonthMDTO.generateId(date, mac));
	}
	
	/**
	 * 分页获得指定时间区间内的设备每日汇总列表数据
	 * @param user
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public TailPage<WifiDeviceWholeMonthMDTO> pageByDateBetween(String mac,String dateStart,String dateEnd,int pageNo,int pageSize){
		Query query = Query.query(Criteria.where("mac").is(mac).and("date").gte(dateStart).lte(dateEnd)).with(new Sort(Direction.DESC,"date"));
		return wifiDeviceWholeMonthMDao.findTailPage(pageNo, pageSize, query);
		//return new CommonPage<AgentWholeDayMDTO>(pageNo, pageSize, total, vtos);
		//return agentWholeDayMDao.find(query);
	}
	
	public List<WifiDeviceWholeMonthMDTO> fetchByDateBetween(String mac,String dateStart,String dateEnd){
		Query query = Query.query(Criteria.where("mac").is(mac).and("date").gte(dateStart).lte(dateEnd)).with(new Sort(Direction.DESC,"date"));
		return wifiDeviceWholeMonthMDao.find(query);
	}
	
	
	/**
	 * 获取指定日期的所有的mac地址的汇总数据
	 * @param macs
	 * @param date
	 * @return
	 */
	public List<RecordSummaryDTO> summaryAggregationBetween(List<String> macs,String dateStart,String dateEnd){
		TypedAggregation<WifiDeviceWholeMonthMDTO> aggregation = newAggregation(WifiDeviceWholeMonthMDTO.class,
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
			    	//.sum("handsets").as("total_handsets"),
			    sort(Direction.ASC, "t_dod", "t_dct")
			);
		List<RecordSummaryDTO> aggregate = wifiDeviceWholeMonthMDao.aggregate(aggregation, RecordSummaryDTO.class);
		return aggregate;
	}
}
