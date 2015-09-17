package com.bhu.vas.business.ds.agent.mservice;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

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
	
	
	public AgentWholeMonthMDTO getWholeDay(String date, int user){
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
}
