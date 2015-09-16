package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.StatisticsVTO;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeDayMDTO;
import com.bhu.vas.business.ds.agent.mservice.AgentWholeDayMService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.localunit.RandomData;

@Service
public class AgentStatisticsUnitFacadeService {
	@Resource
	private UserService userService;

	@Resource
	private AgentWholeDayMService agentWholeDayMService;
	/**
	 * 主页面统计数据
	 * @param uid
	 * @param enddate
	 * @return
	 */
	public RpcResponseDTO<StatisticsVTO> statistics(int uid, String dateEndStr) {
		try{
			StatisticsVTO vto = new StatisticsVTO();
			vto.setRcm(76696999l);
			vto.setRlm(977906l);
			vto.setRyd(96998l);
			vto.setOd(90969);
			vto.setRtl(88932999l);
			Date dateEnd = DateTimeHelper.parseDate(dateEndStr, DateTimeHelper.FormatPattern5);
			Date dateStart = DateTimeExtHelper.getFirstDateOfMonth(dateEnd);
			List<AgentWholeDayMDTO> results = agentWholeDayMService.fetchByDateBetween(uid, DateTimeHelper.formatDate(dateStart, DateTimeHelper.FormatPattern5), dateEndStr);
			vto.setCharts(new HashMap<String,Double>());
			for(AgentWholeDayMDTO dto:results){
				vto.getCharts().put(dto.getDate(), (double)RandomData.floatNumber(5000, 20000));
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 分页取enddate包括enddate前90天的数据，构建出60条数据
	 * 临时实现 只分页，返回数据中没有相较同月的比较值
	 * @param uid
	 * @param enddate yyyy-MM-dd
	 * @return
	 */
	public RpcResponseDTO<TailPage<DailyRevenueRecordVTO>> pageHistoryRecords(int uid,String dateEndStr,int pageNo, int pageSize) {
		try{
			Date dateEnd = DateTimeHelper.parseDate(dateEndStr, DateTimeHelper.FormatPattern5);
			Date dateStart = DateTimeHelper.getDateDaysAgo(dateEnd,180);
			TailPage<AgentWholeDayMDTO> page = agentWholeDayMService.pageByDateBetween(uid, DateTimeHelper.formatDate(dateStart, DateTimeHelper.FormatPattern5), dateEndStr, pageNo, pageSize);
			List<DailyRevenueRecordVTO> items = new ArrayList<>();
			for(AgentWholeDayMDTO dto : page.getItems()){
				DailyRevenueRecordVTO vto = new DailyRevenueRecordVTO();
				vto.setDate(dto.getDate());
				vto.setOd(dto.getDevices());
				vto.setOh(dto.getHandsets());
				vto.setR(0.00d);
				vto.setC("+13.7%");
				items.add(vto);
			}
			TailPage<DailyRevenueRecordVTO> result_pages = new CommonPage<DailyRevenueRecordVTO>(pageNo, pageSize,page.getTotalItemsCount(), items);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_pages);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
