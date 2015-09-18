package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.ChargingCurrencyHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.agent.vto.AgentDeviceStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementVTO;
import com.bhu.vas.api.rpc.agent.vto.StatisticsVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.local.serviceimpl.BusinessCacheService;
import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeDayMDTO;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeMonthMDTO;
import com.bhu.vas.business.ds.agent.mservice.AgentWholeDayMService;
import com.bhu.vas.business.ds.agent.mservice.AgentWholeMonthMService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.IdHelper;
import com.smartwork.msip.cores.helper.comparator.SortMapHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
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
	private WifiDeviceService wifiDeviceService;

	@Resource
	private AgentWholeDayMService agentWholeDayMService;
	
	@Resource
	private AgentWholeMonthMService agentWholeMonthMService;
	
	@Resource
	private BusinessCacheService businessCacheService;

	/**
	 * 主页面统计数据
	 * @param uid
	 * @param enddate
	 * @return
	 */
	public RpcResponseDTO<StatisticsVTO> statistics(int uid, String dateEndStr) {
		try{
			StatisticsVTO vto = new StatisticsVTO();
			vto.setRcm(ArithHelper.getFormatter(String.valueOf(76696999l)));
			vto.setRlm(ArithHelper.getFormatter(String.valueOf(977906l)));
			vto.setRyd(ArithHelper.getFormatter(String.valueOf(96998l)));
			vto.setOd(ArithHelper.getFormatter(String.valueOf(90969)));
			vto.setRtl(ArithHelper.getFormatter(String.valueOf(88932999l)));
			Date dateEnd = DateTimeHelper.parseDate(dateEndStr, DateTimeHelper.FormatPattern5);
			Date dateStart = DateTimeExtHelper.getFirstDateOfMonth(dateEnd);
			List<AgentWholeDayMDTO> results = agentWholeDayMService.fetchByDateBetween(uid, DateTimeHelper.formatDate(dateStart, DateTimeHelper.FormatPattern5), dateEndStr);
			//vto.setCharts(new HashMap<String,Double>());
			Map<String,Double> charts = new HashMap<>();
			int max = 0;
			for(AgentWholeDayMDTO dto:results){
				int whichday = DateTimeExtHelper.getDay(DateTimeHelper.parseDate(dto.getDate(), DateTimeHelper.FormatPattern5));
				charts.put(String.valueOf(whichday), ArithHelper.round((double)RandomData.floatNumber(5000, 20000),2));
				if(whichday>max){
					max = whichday;
				}
			}
			//小于max值并且charts中不存在的数据进行补零
			for(int i=1;i<max;i++){
				String indexday = String.valueOf(i);
				if(!charts.containsKey(indexday)){
					charts.put(indexday, 0d);
				}
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
			DailyRevenueRecordVTO vto = null;
			for(AgentWholeDayMDTO dto : page.getItems()){
				vto = new DailyRevenueRecordVTO();
				vto.setIndex(++startIndex);
				vto.setDate(dto.getDate());
				vto.setOd(dto.getDevices());
				vto.setOh(dto.getHandsets());
				vto.setR(ArithHelper.getCurrency(String.valueOf(ChargingCurrencyHelper.currency(dto.getOnlineduration()))));
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
	
	/**
	 * 代理商结算页面列表（管理员可以访问）
	 * 1、获取代理商列表
	 * 2、获取代理商本月以前（不包括本月）的列表记录，作为已结算金额
	 * 3、获取代理商本月的列表记录，作为未结算金额
	 * 4、获取代理商上月的列表记录，作为上月结算金额
	 * @param uid
	 * @param monthDateEnd 截止时间点 yyyy-MM-dd 其中的月份代表本月
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public RpcResponseDTO<TailPage<SettlementVTO>> pageSettlements(int uid,String dateCurrent,int pageNo, int pageSize) {
		
		List<SettlementVTO> settleVtos;
		try{
			settleVtos = new ArrayList<SettlementVTO>();
			ModelCriteria mc_user = new ModelCriteria();
			mc_user.createCriteria().andColumnEqualTo("utype", User.Agent_User).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
			mc_user.setPageNumber(pageNo);
			mc_user.setPageSize(pageSize);
			TailPage<User> userPages = userService.findModelTailPageByModelCriteria(mc_user);
			if(userPages.getItems().isEmpty()){
				TailPage<SettlementVTO> result_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,0, new ArrayList<SettlementVTO>());
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_pages);
			}
			int startIndex = PageHelper.getStartIndexOfPage(pageNo, pageSize);
			Date certainDate = DateTimeHelper.parseDate(dateCurrent, DateTimeHelper.FormatPattern5);
			String currentMonth = DateTimeHelper.formatDate(certainDate, DateTimeHelper.FormatPattern11);
			String previosMonth = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfMonthAgo(certainDate,1), DateTimeHelper.FormatPattern11);
			List<Integer> users = IdHelper.getPKs(userPages.getItems(), Integer.class);
			
			List<RecordSummaryDTO> summary = agentWholeMonthMService.summaryAggregationBetween(users, null, previosMonth);
			SettlementVTO vto = null;
			for(User user:userPages.getItems()){
				vto = new SettlementVTO();
				vto.setIndex(++startIndex);
				vto.setUid(user.getId());
				vto.setOrg(user.getOrg());
				RecordSummaryDTO rsd = distillRecordSummaryDTO(summary,user.getId());
				if(rsd != null){
					vto.setTr(ArithHelper.getFormatter(String.valueOf(ChargingCurrencyHelper.currency(rsd.getTotal_onlineduration()))));
				}else{
					vto.setTr("0.00");
				}
				AgentWholeMonthMDTO preMonth = agentWholeMonthMService.getWholeMonth(previosMonth, user.getId());
				if(preMonth != null)
					vto.setLsr(ArithHelper.getFormatter(String.valueOf(ChargingCurrencyHelper.currency(preMonth.getOnlineduration()))));
				else
					vto.setLsr("0.00");
				AgentWholeMonthMDTO curMonth = agentWholeMonthMService.getWholeMonth(currentMonth, user.getId());
				if(curMonth != null)
					vto.setUr(ArithHelper.getFormatter(String.valueOf(ChargingCurrencyHelper.currency(curMonth.getOnlineduration()))));
				else
					vto.setUr("0.00");
				settleVtos.add(vto);
			}
			
			TailPage<SettlementVTO> result_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,userPages.getTotalItemsCount(), settleVtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_pages);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	private RecordSummaryDTO distillRecordSummaryDTO(List<RecordSummaryDTO> summary,int user){
		for(RecordSummaryDTO dto:summary){
			if(dto.getId().equals(String.valueOf(user)))
				return dto;
		}
		return null;
	}
	
	public RpcResponseDTO<AgentDeviceStatisticsVTO> fetchAgentDeviceStatistics(int agentuser){
		if(agentuser <= 0){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		AgentDeviceStatisticsVTO statistics = businessCacheService.getAgentDSCacheByUser(agentuser);
		if(statistics == null){
			ModelCriteria mc_total = new ModelCriteria();
			mc_total.createCriteria().andColumnEqualTo("agentuser", agentuser).andSimpleCaulse(" 1=1 ");
	        int total = wifiDeviceService.countByModelCriteria(mc_total);
	        
	        ModelCriteria mc_online = new ModelCriteria();
	        mc_online.createCriteria().andColumnEqualTo("online", 1).andColumnEqualTo("agentuser", agentuser).andSimpleCaulse(" 1=1 ");
			int online = wifiDeviceService.countByModelCriteria(mc_online);
			
			statistics = new AgentDeviceStatisticsVTO();
			statistics.setU(agentuser);
			statistics.setTd(total);
			statistics.setOd(online);
			statistics.setFd(total-online);
			statistics.setC_at(DateTimeHelper.formatDate(DateTimeHelper.DefalutFormatPattern));
			businessCacheService.storeAgentDSCacheResult(agentuser, statistics);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(statistics);
	}
}
