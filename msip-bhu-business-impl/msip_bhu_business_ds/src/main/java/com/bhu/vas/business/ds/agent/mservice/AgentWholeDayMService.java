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
import com.bhu.vas.business.ds.agent.mdao.AgentWholeDayMDao;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeDayMDTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 *
 */
@Service
public class AgentWholeDayMService {
	
	@Resource
	private AgentWholeDayMDao agentWholeDayMDao;
	
	public AgentWholeDayMDTO save(AgentWholeDayMDTO dto){
		return agentWholeDayMDao.save(dto);
	}
	
	
	public AgentWholeDayMDTO getWholeDay(String date, int user){
		return agentWholeDayMDao.findById(AgentWholeDayMDTO.generateId(date, user));
	}
	
	/**
	 * 分页获得指定区间内的代理商每日汇总列表数据
	 * @param user
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public TailPage<AgentWholeDayMDTO> pageByDateBetween(int user,String dateStart,String dateEnd,int pageNo,int pageSize){
		Query query = Query.query(Criteria.where("user").is(user).and("date").gte(dateStart).lte(dateEnd)).with(new Sort(Direction.DESC,"date"));
		return agentWholeDayMDao.findTailPage(pageNo, pageSize, query);
		//return new CommonPage<AgentWholeDayMDTO>(pageNo, pageSize, total, vtos);
		//return agentWholeDayMDao.find(query);
	}
	
	public List<AgentWholeDayMDTO> fetchByDateBetween(int user,String dateStart,String dateEnd){
		Query query = Query.query(Criteria.where("user").is(user).and("date").gte(dateStart).lte(dateEnd)).with(new Sort(Direction.DESC,"date"));
		return agentWholeDayMDao.find(query);
	}
	
	
	/**
	 * 获取指定日期的所有的mac地址的汇总数据
	 * @param macs
	 * @param date
	 * @return
	 */
	public List<RecordSummaryDTO> summaryAggregationBetween(List<Integer> users,String dateStart,String dateEnd){
		TypedAggregation<AgentWholeDayMDTO> aggregation = newAggregation(AgentWholeDayMDTO.class,
				match(Criteria.where("user").in(users).and("date").gte(dateStart).lte(dateEnd)),
			    group("user")
			    	.sum("onlineduration").as("total_onlineduration")
			    	.sum("connecttimes").as("total_connecttimes")
			    	.sum("tx_bytes").as("total_tx_bytes")
			    	.sum("rx_bytes").as("total_rx_bytes"),
			    	//.sum("handsets").as("total_handsets"),
			    sort(Direction.ASC, "total_onlineduration", "total_connecttimes")
			);
		List<RecordSummaryDTO> aggregate = agentWholeDayMDao.aggregate(aggregation, RecordSummaryDTO.class);
		return aggregate;
	}
}
