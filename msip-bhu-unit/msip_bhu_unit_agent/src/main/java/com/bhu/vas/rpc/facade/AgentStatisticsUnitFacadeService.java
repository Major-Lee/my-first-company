package com.bhu.vas.rpc.facade;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.StatisticsVTO;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;

@Service
public class AgentStatisticsUnitFacadeService {
	@Resource
	private UserService userService;
	@Resource
	private UserTokenService userTokenService;

	public RpcResponseDTO<StatisticsVTO> statistics(int uid, String enddate) {
		return null;
	}

	/**
	 * 取enddate包括enddate前90天的数据，构建出60条数据
	 * @param uid
	 * @param enddate
	 * @return
	 */
	public RpcResponseDTO<List<DailyRevenueRecordVTO>> historyrecords(int uid,String enddate) {
		return null;
	}
}
