package com.bhu.vas.business.ds.agent.mservice;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.mdao.AgentWholeMonthMDao;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeMonthMDTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 *
 */
@Service
public class AgentWholeMonthMService {
	
	@Resource
	private AgentWholeMonthMDao agentWholeMonthMDao;
	
	public AgentWholeMonthMDTO save(AgentWholeMonthMDTO dto){
		return agentWholeMonthMDao.save(dto);
	}
	
	
	public AgentWholeMonthMDTO getWholeMonth(String date, int user){
		return agentWholeMonthMDao.findById(AgentWholeMonthMDTO.generateId(date, user));
	}
	
	/**
	 * 分页获得指定区间内的代理商每日汇总列表数据
	 * @param user
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public TailPage<AgentWholeMonthMDTO> pageByDateBetween(int user,String dateStart,String dateEnd,int pageNo,int pageSize){
		Query query = Query.query(Criteria.where("user").is(user).and("date").gte(dateStart).lte(dateEnd)).with(new Sort(Direction.DESC,"date"));
		return agentWholeMonthMDao.findTailPage(pageNo, pageSize, query);
		//return new CommonPage<AgentWholeDayMDTO>(pageNo, pageSize, total, vtos);
		//return agentWholeDayMDao.find(query);
	}
	
	public List<AgentWholeMonthMDTO> fetchByDateBetween(int user,String dateStart,String dateEnd){
		Query query = Query.query(Criteria.where("user").is(user).and("date").gte(dateStart).lte(dateEnd)).with(new Sort(Direction.DESC,"date"));
		return agentWholeMonthMDao.find(query);
	}
	
	public RecordSummaryDTO summaryAggregationTotal4User(int user){
		List<Integer> users = new ArrayList<Integer>();
		users.add(user);
		List<RecordSummaryDTO> summaryResult = this.summaryAggregationBetween(users, null, null);
		if(summaryResult != null && !summaryResult.isEmpty()){
			return summaryResult.get(0);
		}else{
			RecordSummaryDTO dto = new RecordSummaryDTO();
			dto.setId(String.valueOf(user));
			return dto;
		}
	}
	
	public List<RecordSummaryDTO> summaryAggregationBetween(List<Integer> users,String dateStart,String dateEnd){
		Criteria criteria = Criteria.where("user").in(users);//.and("date").gte(dateStart).lte(dateEnd);
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
		
		TypedAggregation<AgentWholeMonthMDTO> aggregation = newAggregation(AgentWholeMonthMDTO.class,
				match(criteria),
			    group("user")
			    	.sum("dod").as("t_dod")
			    	.sum("dct").as("t_dct")
			    	.sum("dtx_bytes").as("t_dtx_bytes")
			    	.sum("drx_bytes").as("t_drx_bytes")
			    	.avg("devices").as("t_devices")
			    	.sum("hod").as("t_hod")
			    	.sum("hct").as("t_hct")
			    	.sum("htx_bytes").as("t_htx_bytes")
			    	.sum("hrx_bytes").as("t_hrx_bytes")
			    	.avg("handsets").as("t_handsets"),
			    	//.sum("handsets").as("total_handsets"),
			    sort(Direction.ASC, "t_dod", "t_dct")
			);
		List<RecordSummaryDTO> aggregate = agentWholeMonthMDao.aggregate(aggregation, RecordSummaryDTO.class);
		return aggregate;
	}
}
