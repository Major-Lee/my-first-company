package com.bhu.vas.business.ds.agent.mservice;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.agent.mdao.AgentWholeDayMDao;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeDayMDTO;

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
	 * 获得指定区间内的代理商每日汇总列表数据
	 * @param user
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<AgentWholeDayMDTO> fetchByDateBetween(int user,String dateStart,String dateEnd){
		Query query=Query.query(Criteria.where("user").is(user).and("date").gte(dateStart).lte(dateEnd)).with(new Sort(Direction.DESC,"date"));
		return agentWholeDayMDao.find(query);
	}
}
