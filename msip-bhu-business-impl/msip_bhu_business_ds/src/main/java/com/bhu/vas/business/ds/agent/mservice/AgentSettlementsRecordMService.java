package com.bhu.vas.business.ds.agent.mservice;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
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
import com.smartwork.msip.cores.helper.ArithHelper;
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
		mdto.setStatus(AgentSettlementsRecordMDTO.Settlement_Bill_Created);
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
	 * 对指定代理商进行结算 指定金额 对未结算的bills列表进行结算
	 * @param agent
	 * @param price
	 */
	public String iterateSettleBills(int operator,String operNick,int agent,double price){
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("结算总金额[%s]<BR/>\n", price));
		if(agent >0 && price>0){
			List<AgentSettlementsRecordMDTO> fetchBillsByAgent = fetchBillsByAgent(agent, AgentSettlementsRecordMDTO.Settlement_Bill_Created,AgentSettlementsRecordMDTO.Settlement_Bill_Parted);
			Iterator<AgentSettlementsRecordMDTO> iter = fetchBillsByAgent.iterator();
			String settled_at =  DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1);
			sb.append(String.format("结算日期[%s]<BR/>\n", settled_at));
			while(iter.hasNext()){//逐条进行结算
				AgentSettlementsRecordMDTO bill = iter.next();
				double takeoff = ArithHelper.sub(bill.getiSVPrice(),bill.getSdPrice()); 
				if(takeoff > 0){
					double old_sdPrice = bill.getSdPrice();
					if(price >= takeoff){
						bill.setReckoner(operator);
						bill.setSdPrice(bill.getiSVPrice());
						bill.setStatus(AgentSettlementsRecordMDTO.Settlement_Bill_Done);
						bill.setSettled_at(settled_at);
						price = ArithHelper.sub(price , takeoff);
					}else{
						//price = 0;
						bill.setReckoner(operator);
						bill.setSdPrice(ArithHelper.add(bill.getSdPrice(),price));
						bill.setStatus(AgentSettlementsRecordMDTO.Settlement_Bill_Parted);
						bill.setSettled_at(settled_at);
						price = 0;
					}
					sb.append(String.format("明细 流水[%s] 结算人[%s] 金额[%s] 曾经结算[%s] 当前结算[%s] 最后结算日期[%s] 状态[%s]<BR/>\n", 
								bill.getId(),operNick,bill.getiSVPrice(),old_sdPrice,ArithHelper.sub(bill.getSdPrice(),old_sdPrice),settled_at,bill.getStatus()));
					save(bill);
				}
				if(price <= 0) break;
			}
		}
		sb.append(String.format("剩余金额[%s]<BR/>\n", price));
		return sb.toString();
	}
	
	/**
	 * 获取指定代理商指定状态的所有bills列表
	 * @param agent
	 * @param status
	 * @return
	 */
	public List<AgentSettlementsRecordMDTO> fetchBillsByAgent(int agent,Object... status){
		Query query = Query.query(Criteria.where("agent").is(agent).and("status").in(status)).with(new Sort(Direction.ASC,"date"));
		return agentSettlementsRecordMDao.find(query);
	}
	
	/**
	 * 统计指定时间段区间内结算列表 
	 * @param mac
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<SettlementSummaryDTO> summaryAggregationBetween(List<Integer> agents,Object[] status,
			String dateStart,String dateEnd,
			int pageNo,int pageSize
			){
		Criteria criteria = Criteria.where("_id").exists(true);
		if(agents != null && !agents.isEmpty()){
			criteria.and("agent").in(agents);
		}
		//Criteria criteria = Criteria.where("agent").in(agents);//.and("date").gte(dateStart).lte(dateEnd);
		if(status != null && status.length >0)
			criteria.and("status").in(status);
		
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
			    	.sum("sdPrice").as("sdmoney")
			    	.sum("iSVPrice").as("money"),
			    sort(Direction.ASC, "money"),
			    skip(startIndex),
			    limit(pageSize)
			);
		List<SettlementSummaryDTO> aggregate = agentSettlementsRecordMDao.aggregate(aggregation, SettlementSummaryDTO.class);
		return aggregate;
	}
	/**
	 * 统计代理商管理页面中的 所有、未结算、已经结算的统计数量数据
	 * 如果agent>0 则是所有用户汇总统计
	 * @param agent
	 * @return
	 */
	public SettlementStatisticsVTO statistics(int agent){
		SettlementStatisticsVTO result = new SettlementStatisticsVTO();
		Criteria unsettled_criteria = Criteria.where("status").in(new Object[]{AgentSettlementsRecordMDTO.Settlement_Bill_Created,AgentSettlementsRecordMDTO.Settlement_Bill_Parted});
		Criteria settled_criteria = Criteria.where("status").in(new Object[]{AgentSettlementsRecordMDTO.Settlement_Bill_Done});
		if(agent>0){
			unsettled_criteria.and("agent").is(agent);
			settled_criteria.and("agent").is(agent);
		}
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
