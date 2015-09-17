package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.StatisticsVTO;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeDayMDTO;
import com.bhu.vas.business.ds.agent.mservice.AgentWholeDayMService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.comparator.SortMapHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
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
			//vto.setCharts(new HashMap<String,Double>());
			Map<String,Double> charts = new HashMap<>();
			for(AgentWholeDayMDTO dto:results){
				int day = DateTimeExtHelper.getDay(DateTimeHelper.parseDate(dto.getDate(), DateTimeHelper.FormatPattern5));
				charts.put(String.valueOf(day), ArithHelper.round((double)RandomData.floatNumber(5000, 20000),2));
			}
			vto.setCharts(SortMapHelper.sortMapByKey(charts));
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
			int startIndex = PageHelper.getStartIndexOfPage(pageNo, pageSize);
			TailPage<AgentWholeDayMDTO> page = agentWholeDayMService.pageByDateBetween(uid, DateTimeHelper.formatDate(dateStart, DateTimeHelper.FormatPattern5), dateEndStr, pageNo, pageSize);
			List<DailyRevenueRecordVTO> items = new ArrayList<>();
			for(AgentWholeDayMDTO dto : page.getItems()){
				DailyRevenueRecordVTO vto = new DailyRevenueRecordVTO();
				vto.setIndex(startIndex++);
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
