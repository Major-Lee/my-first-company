package com.bhu.vas.business.ds.agent.mservice;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.vto.SettlementStatisticsVTO;
import com.bhu.vas.business.ds.agent.dto.SettlementCountDTO;
import com.bhu.vas.business.ds.agent.dto.SettlementSummaryDTO;
import com.bhu.vas.business.ds.agent.mdao.AgentSettlementsRecordMDao;
import com.bhu.vas.business.ds.agent.mdto.AgentSettlementsRecordMDTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.page.PageHelper;

/**
 * 
 * @author Edmond
 *
 */
@Service
public class AgentSettlementsRecordMService {
	
	@Resource
	private AgentSettlementsRecordMDao agentSettlementsRecordMDao;
	
	
	public AgentSettlementsRecordMDTO settlementGen(String date,int agent,double iSVPrice){
		AgentSettlementsRecordMDTO mdto = new AgentSettlementsRecordMDTO();
		mdto.setId(AgentSettlementsRecordMDTO.generateId(date, agent));
		mdto.setDate(date);
		mdto.setAgent(agent);
		mdto.setiSVPrice(iSVPrice);
		mdto.setStatus(AgentSettlementsRecordMDTO.Settlement_Created);
		mdto.setCreated_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
		return this.save(mdto);
	}
	
	/*public AgentSettlementsRecordMDTO settlementDone(String date,int agent,double iSVPrice){
		AgentSettlementsRecordMDTO mdto = new AgentSettlementsRecordMDTO();
		mdto.setId(AgentSettlementsRecordMDTO.generateId(date, agent));
		mdto.setDate(date);
		mdto.setAgent(agent);
		mdto.setiSVPrice(iSVPrice);
		mdto.setStatus(AgentSettlementsRecordMDTO.Settlement_Created);
		mdto.setCreated_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
		return this.save(mdto);
	}*/
	
	public AgentSettlementsRecordMDTO save(AgentSettlementsRecordMDTO dto){
		return agentSettlementsRecordMDao.save(dto);
	}
	
	public AgentSettlementsRecordMDTO getSettlement(String date, int agent){
		return agentSettlementsRecordMDao.findById(AgentSettlementsRecordMDTO.generateId(date, agent));
	}
	
	public boolean hasSettlement(String date, int agent){
		AgentSettlementsRecordMDTO mdto = this.getSettlement(date, agent);
		return mdto != null;
	}
		
	/**
	 * 统计指定时间段区间内结算列表 
	 * @param mac
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<SettlementSummaryDTO> summaryAggregationBetween(List<Integer> agents,int status,
			String dateStart,String dateEnd,
			int pageNo,int pageSize
			){
		Criteria criteria = Criteria.where("_id").exists(true);
		if(agents != null && !agents.isEmpty()){
			criteria.and("agent").in(agents);
		}
		//Criteria criteria = Criteria.where("agent").in(agents);//.and("date").gte(dateStart).lte(dateEnd);
		if(status >= 0)
			criteria.and("status").is(status);
		
		boolean isStartNotEmpty = StringUtils.isNotEmpty(dateStart);
		boolean isEndNotEmpty = StringUtils.isNotEmpty(dateEnd);
		if(isStartNotEmpty && isEndNotEmpty){
			criteria.and("date").gte(dateStart).lte(dateEnd);
		}else{
			if(isStartNotEmpty){
				criteria.and("date").gte(dateStart);
			}
			if(isEndNotEmpty){
				criteria.and("date").lte(dateEnd);
			}
		}
		int startIndex = PageHelper.getStartIndexOfPage(pageNo, pageSize);
		TypedAggregation<AgentSettlementsRecordMDTO> aggregation = newAggregation(AgentSettlementsRecordMDTO.class,
				match(criteria),
			    group("agent")
			    	.sum("iSVPrice").as("money"),
			    sort(Direction.ASC, "money"),
			    skip(startIndex),
			    limit(pageSize)
			);
		List<SettlementSummaryDTO> aggregate = agentSettlementsRecordMDao.aggregate(aggregation, SettlementSummaryDTO.class);
		return aggregate;
	}
	/**
	 * 统计代理商管理页面中的 所有、未结算、已经结算的统计数据
	 * 如果agent>0 则是所有用户汇总统计
	 * @param agent
	 * @return
	 */
	public SettlementStatisticsVTO statistics(int agent){
		SettlementStatisticsVTO result = new SettlementStatisticsVTO();
		Criteria unsettled_criteria = Criteria.where("status").is(AgentSettlementsRecordMDTO.Settlement_Created);
		Criteria settled_criteria = Criteria.where("status").is(AgentSettlementsRecordMDTO.Settlement_Done);
		if(agent>0){
			unsettled_criteria.and("agent").is(agent);
			settled_criteria.and("agent").is(agent);
		}
		
		/*TypedAggregation<AgentSettlementsRecordMDTO> total_aggregation = newAggregation(AgentSettlementsRecordMDTO.class,
				group("agent"),//.count().as("count1"),
				group().count().as("count")
			    //group("status","agent")
			    	//.count().as("count")
			);
		List<SettlementCountDTO> total_aggregate = agentSettlementsRecordMDao.aggregate(total_aggregation, SettlementCountDTO.class);
		if(total_aggregate != null && !total_aggregate.isEmpty()){
			long total = total_aggregate.get(0).getCount();
			result.setTs(total);
		}*/
		TypedAggregation<AgentSettlementsRecordMDTO> unsettled_aggregation = newAggregation(AgentSettlementsRecordMDTO.class,
				match(unsettled_criteria),
				group("agent"),//.count().as("count1"),
				group().count().as("count")
			    //group("status","agent")
			    	//.count().as("count")
			);
		List<SettlementCountDTO> unsettled_aggregate = agentSettlementsRecordMDao.aggregate(unsettled_aggregation, SettlementCountDTO.class);
		if(unsettled_aggregate != null && !unsettled_aggregate.isEmpty()){
			long us = unsettled_aggregate.get(0).getCount();
			result.setUs(us);
		}
		
		TypedAggregation<AgentSettlementsRecordMDTO> settled_aggregation = newAggregation(AgentSettlementsRecordMDTO.class,
				match(settled_criteria),
			    group("agent"),//..count().as("count1"),
			    group().count().as("count")
			    
			);
		List<SettlementCountDTO> settled_aggregate = agentSettlementsRecordMDao.aggregate(settled_aggregation, SettlementCountDTO.class);
		if(settled_aggregate != null && !settled_aggregate.isEmpty()){
			long sd = settled_aggregate.get(0).getCount();
			result.setSd(sd);
		}
		
		result.setU(agent);
		result.setC_at(DateTimeHelper.formatDate(DateTimeHelper.DefalutFormatPattern));
		return result;
	}
	
	public long countAll(){
		return agentSettlementsRecordMDao.count(new Query());
	}
}
