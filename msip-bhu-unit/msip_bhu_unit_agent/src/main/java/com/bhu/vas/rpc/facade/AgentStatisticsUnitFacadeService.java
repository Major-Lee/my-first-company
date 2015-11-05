package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.agent.vto.AgentDeviceStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.AgentRevenueStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementPageVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.local.serviceimpl.BusinessCacheService;
import com.bhu.vas.business.ds.agent.dto.SettlementSummaryDTO;
import com.bhu.vas.business.ds.agent.helper.AgentHelper;
import com.bhu.vas.business.ds.agent.mdto.AgentSettlementsRecordMDTO;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeDayMDTO;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeMonthMDTO;
import com.bhu.vas.business.ds.agent.mservice.AgentSettlementsRecordMService;
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
import com.smartwork.msip.cores.plugins.filterhelper.StringHelper;
import com.smartwork.msip.jdo.ResponseErrorCode;

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
	private AgentSettlementsRecordMService agentSettlementsRecordMService;
	
	@Resource
	private BusinessCacheService businessCacheService;

	/**
	 * 主页面统计数据
	 * @param uid
	 * @param enddate
	 * @return
	 */
	public RpcResponseDTO<AgentRevenueStatisticsVTO> statistics(int user, String dateEndStr) {
		try{

			User operUser = userService.getById(user);
			UserTypeValidateService.validUserType(operUser, UserType.Agent.getSname());

			AgentRevenueStatisticsVTO vto = new AgentRevenueStatisticsVTO();
			//vto.setRcm(ArithHelper.getFormatter(String.valueOf(76696999l)));
			//vto.setRlm(ArithHelper.getFormatter(String.valueOf(977906l)));
			//vto.setRyd(ArithHelper.getFormatter(String.valueOf(96998l)));
			//vto.setOd(ArithHelper.getFormatter(String.valueOf(90969)));
			//vto.setRtl(ArithHelper.getFormatter(String.valueOf(88932999l)));
			Date currentDate = DateTimeHelper.parseDate(dateEndStr, DateTimeHelper.FormatPattern5);
			//本月收入(元)  
			String currentMonth = DateTimeHelper.formatDate(currentDate, DateTimeHelper.FormatPattern11);
			AgentWholeMonthMDTO currentmonth_data = agentWholeMonthMService.getWholeMonth(currentMonth, user);
			vto.setRcm(ArithHelper.getFormatter(String.valueOf(currentmonth_data!=null?AgentHelper.currency(currentmonth_data.getDod()):0.00d)));
			//上月收入(元)  
			String previosMonth = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfMonthAgo(currentDate,1), DateTimeHelper.FormatPattern11);
			AgentWholeMonthMDTO previosmonth_data = agentWholeMonthMService.getWholeMonth(previosMonth, user);
			vto.setRlm(ArithHelper.getFormatter(String.valueOf(previosmonth_data!=null?AgentHelper.currency(previosmonth_data.getDod()):0.00d)));
			//昨日收入(元)  
			String yesterday = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(currentDate,1),DateTimeHelper.FormatPattern5);
			AgentWholeDayMDTO yesterday_data = agentWholeDayMService.getWholeDay(yesterday, user);
			vto.setRyd(ArithHelper.getFormatter(String.valueOf(yesterday_data!=null?AgentHelper.currency(yesterday_data.getDod()):0.00d)));
			//结算过的总收入(元)
			//待结算的总数 
			/*RecordSummaryDTO summary = agentWholeMonthMService.summaryAggregationTotal4User(user);
			vto.setOd(ArithHelper.getFormatter(String.valueOf(summary.getT_devices())));
			vto.setRtl(ArithHelper.getFormatter(String.valueOf(ChargingCurrencyHelper.currency(summary.getT_dod()))));*/
			pageTotalSettlements4Agent(user,vto);
			Date dateEnd = DateTimeHelper.parseDate(dateEndStr, DateTimeHelper.FormatPattern5);
			Date dateStart = DateTimeExtHelper.getFirstDateOfMonth(dateEnd);
			List<AgentWholeDayMDTO> results = agentWholeDayMService.fetchByDateBetween(user, DateTimeHelper.formatDate(dateStart, DateTimeHelper.FormatPattern5), dateEndStr);
			//vto.setCharts(new HashMap<String,Double>());
			Map<String,Double> charts = new HashMap<>();
			int max = 0;
			for(AgentWholeDayMDTO dto:results){
				int whichday = DateTimeExtHelper.getDay(DateTimeHelper.parseDate(dto.getDate(), DateTimeHelper.FormatPattern5));
				//ArithHelper.getCurrency(String.valueOf(ChargingCurrencyHelper.currency(dto.getDod())))
				//charts.put(String.format("%02d日", whichday), ArithHelper.round((double)RandomData.floatNumber(5000, 20000),2));
				charts.put(String.format("%02d日", whichday), AgentHelper.currency(dto.getDod()));
				if(whichday>max){
					max = whichday;
				}
			}
			//小于max值并且charts中不存在的数据进行补零
			for(int i=1;i<max;i++){
				String indexday = String.format("%02d日", i);
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
	 * 获取指定代理商 的所有 已结算的金额和待结算的金额
	 * @param agent
	 * @param vto
	 */
	private void pageTotalSettlements4Agent(int agent,AgentRevenueStatisticsVTO vto) {
		List<Integer> agents = new ArrayList<Integer>();
			agents.add(agent);
		if(!agents.isEmpty()){
				//取所有记录汇总
			List<SettlementSummaryDTO> mainSummary = agentSettlementsRecordMService.summaryAggregationBetween(agents, 
						null, 
						null, null, 1, agents.size());
			vto.setTr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummarySettled(String.valueOf(agent),mainSummary),2))));
			vto.setUr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummaryUnsettled(String.valueOf(agent),mainSummary),2))));
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

			User operUser = userService.getById(uid);
			UserTypeValidateService.validUserType(operUser, UserType.Agent.getSname());

			Date currentDate = DateTimeHelper.parseDate(dateEndStr, DateTimeHelper.FormatPattern5);
			//String yesterday = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(currentDate,1),DateTimeHelper.FormatPattern5);
			Date dateEnd = DateTimeHelper.getDateDaysAgo(currentDate,1);//DateTimeHelper.parseDate(dateEndStr, DateTimeHelper.FormatPattern5);
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
				
				vto.setCd(dto.getDevices());
				vto.setCsd(dto.getNewdevices());
				vto.setR(ArithHelper.getCurrency(String.valueOf(AgentHelper.currency(dto.getDod()))));
				//vto.setC("+13.7%");
				items.add(vto);
			}
			TailPage<DailyRevenueRecordVTO> result_pages = new CommonPage<DailyRevenueRecordVTO>(pageNo, pageSize,page.getTotalItemsCount(), items);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_pages);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	private double fetchSettlementSummarySettled(String agent,List<SettlementSummaryDTO> summary){
		if(summary != null && !summary.isEmpty()){
			for(SettlementSummaryDTO summaryDTO:summary){
				if(agent.equals(summaryDTO.getId())) return summaryDTO.getSdmoney();
			}
		}
		return 0.00d;
	}
	private double fetchSettlementSummaryUnsettled(String agent,List<SettlementSummaryDTO> summary){
		if(summary != null && !summary.isEmpty()){
			for(SettlementSummaryDTO summaryDTO:summary){
				if(agent.equals(summaryDTO.getId())) return summaryDTO.getMoney()-summaryDTO.getSdmoney();
			}
		}
		return 0.00d;
	}
	/**
	 * 代理商结算页面列表（管理员可以访问）
	 * 1、获取代理商列表
	 * 2、获取代理商本月以前（不包括本月）的列表记录，作为已结算金额
	 * 3、获取代理商本月的列表记录，作为未结算金额
	 * 4、获取代理商上月的列表记录，作为上月结算金额
	 * @param uid
	 * @param viewstatus -1 所有 1 settled 0 unsettled
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public RpcResponseDTO<SettlementPageVTO> pageSettlements(int operator_user,int viewstatus, int pageNo, int pageSize){
		if(viewstatus == -1){
			return pageTotalSettlements(operator_user,pageNo, pageSize);
		}else if(viewstatus == 1){
			return pageSettledSettlements(operator_user,pageNo, pageSize);
		}else{
			return pageUnSettledSettlements(operator_user,pageNo, pageSize);
		}
	}
	
	private RpcResponseDTO<SettlementPageVTO> pageTotalSettlements(int operator_user,int pageNo, int pageSize) {
		SettlementPageVTO result_page = null;
		List<SettlementVTO> settleVtos = null;
		try{
			settleVtos = new ArrayList<SettlementVTO>();
			SettlementStatisticsVTO statistics = this.statistics(-1);
			//获取主界面显示结构 agents 列表
			ModelCriteria mc_user = new ModelCriteria();
			mc_user.createCriteria().andColumnEqualTo("utype", UserType.Agent.getIndex()).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
			mc_user.setOrderByClause(" id desc ");
			mc_user.setPageNumber(pageNo);
			mc_user.setPageSize(pageSize);
			TailPage<User> userPages = userService.findModelTailPageByModelCriteria(mc_user);
			List<Integer> agents = IdHelper.getPKs(userPages.getItems(), Integer.class);
			//List<SettlementSummaryDTO> summaryMain = agentSettlementsRecordMService.summaryAggregationBetween(null, AgentSettlementsRecordMDTO.Settlement_View_All, null, null, pageNo, pageSize);
			/*for(SettlementSummaryDTO summaryDTO:summaryMain){
				agents.add(Integer.parseInt(summaryDTO.getId()));
			}*/
			result_page = new SettlementPageVTO();
			result_page.setStatistics(statistics);
			if(!agents.isEmpty()){
				//取未结清记录汇总
				List<SettlementSummaryDTO> unsettledSummary = agentSettlementsRecordMService.summaryAggregationBetween(agents, 
						new Object[]{AgentSettlementsRecordMDTO.Settlement_Bill_Created,AgentSettlementsRecordMDTO.Settlement_Bill_Parted}, 
						null, null, 1, agents.size());
				//取已结清记录汇总
				List<SettlementSummaryDTO> settledSummary = agentSettlementsRecordMService.summaryAggregationBetween(agents, 
						new Object[]{AgentSettlementsRecordMDTO.Settlement_Bill_Done,AgentSettlementsRecordMDTO.Settlement_Bill_Parted}, 
						null, null, 1, agents.size());
				//List<User> users = userService.findByIds(agents, true, true);
				SettlementVTO vto = null;
				int index = 0;
				//for(SettlementSummaryDTO summaryDTO:summaryMain){
				for(User user:userPages.getItems()){
					vto = new SettlementVTO();
					vto.setIndex(index);
					vto.setOrg(user.getOrg());
					vto.setUid(user.getId());
					//vto.setTr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(summaryDTO.getMoney(),2))));
					/*String previosMonth = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfMonthAgo(new Date(),1), DateTimeHelper.FormatPattern11);
					AgentSettlementsRecordMDTO previosMonth_settlement = agentSettlementsRecordMService.getSettlement(previosMonth, user.getId());
					if(previosMonth_settlement != null)
						vto.setLsr(ArithHelper.getFormatter(String.valueOf(previosMonth_settlement.getiSVPrice())));
					else
						vto.setLsr("0.00");*/
					vto.setTr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummarySettled(user.getId().toString(),settledSummary),2))));
					vto.setUr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummaryUnsettled(user.getId().toString(),unsettledSummary),2))));
					settleVtos.add(vto);
					index++;
				}
			}else{
				TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,0, settleVtos);
				result_page.setPages(settlement_pages);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
			}
			TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,(int)statistics.getTs(), settleVtos);
			result_page.setPages(settlement_pages);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	private RpcResponseDTO<SettlementPageVTO> pageSettledSettlements(int operator_user,int pageNo, int pageSize) {
		SettlementPageVTO result_page = null;
		List<SettlementVTO> settleVtos = null;
		try{
			settleVtos = new ArrayList<SettlementVTO>();
			SettlementStatisticsVTO statistics = this.statistics(-1);
			//去所有已结清按agent group by的记录
			List<SettlementSummaryDTO> settledSummaryMain = agentSettlementsRecordMService.summaryAggregationBetween(null, 
					new Object[]{AgentSettlementsRecordMDTO.Settlement_Bill_Done},
					null, null, pageNo, pageSize);
			List<Integer> agents = new ArrayList<Integer>();
			for(SettlementSummaryDTO summaryDTO:settledSummaryMain){
				agents.add(Integer.parseInt(summaryDTO.getId()));
			}
			result_page = new SettlementPageVTO();
			result_page.setStatistics(statistics);
			if(!agents.isEmpty()){
				//取未结清和部分结清记录汇总
				List<SettlementSummaryDTO> settledSummary = agentSettlementsRecordMService.summaryAggregationBetween(null, 
						new Object[]{AgentSettlementsRecordMDTO.Settlement_Bill_Done,AgentSettlementsRecordMDTO.Settlement_Bill_Parted},
						null, null, pageNo, pageSize);
				List<SettlementSummaryDTO> unsettledSummary = agentSettlementsRecordMService.summaryAggregationBetween(agents, 
						new Object[]{AgentSettlementsRecordMDTO.Settlement_Bill_Created,AgentSettlementsRecordMDTO.Settlement_Bill_Parted},
						null, null, 1, agents.size());
				List<User> users = userService.findByIds(agents, true, true);
				SettlementVTO vto = null;
				int index = 0;
				for(SettlementSummaryDTO summaryDTO:settledSummaryMain){
					vto = new SettlementVTO();
					User user = users.get(index);
					vto.setIndex(index);
					vto.setOrg(user != null?user.getOrg():StringHelper.EMPTY_STRING);
					vto.setUid(user.getId());
					//vto.setTr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(summaryDTO.getMoney(),2))));
					/*String previosMonth = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfMonthAgo(new Date(),1), DateTimeHelper.FormatPattern11);
					AgentSettlementsRecordMDTO previosMonth_settlement = agentSettlementsRecordMService.getSettlement(previosMonth, user.getId());
					if(previosMonth_settlement != null)
						vto.setLsr(ArithHelper.getFormatter(String.valueOf(previosMonth_settlement.getiSVPrice())));
					else
						vto.setLsr("0.00");*/
					vto.setTr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummarySettled(summaryDTO.getId(),settledSummary),2))));
					vto.setUr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummaryUnsettled(summaryDTO.getId(),unsettledSummary),2))));
					settleVtos.add(vto);
					index++;
				}
			}else{
				TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,0, settleVtos);
				result_page.setPages(settlement_pages);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
			}
			TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,(int)statistics.getSd(), settleVtos);
			result_page.setPages(settlement_pages);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	private RpcResponseDTO<SettlementPageVTO> pageUnSettledSettlements(int operator_user,int pageNo, int pageSize) {

		User operUser = userService.getById(operator_user);
		UserTypeValidateService.validUserType(operUser, UserType.Agent.getSname());

		SettlementPageVTO result_page = null;
		List<SettlementVTO> settleVtos = null;
		try{
			settleVtos = new ArrayList<SettlementVTO>();
			SettlementStatisticsVTO statistics = this.statistics(-1);
			//去所有已结清和部分结清按agent group by的记录
			List<SettlementSummaryDTO> unsettledSummaryMain = agentSettlementsRecordMService.summaryAggregationBetween(null, 
					new Object[]{AgentSettlementsRecordMDTO.Settlement_Bill_Created,AgentSettlementsRecordMDTO.Settlement_Bill_Parted}, 
					null, null, pageNo, pageSize);
			List<Integer> agents = new ArrayList<Integer>();
			for(SettlementSummaryDTO summaryDTO:unsettledSummaryMain){
				agents.add(Integer.parseInt(summaryDTO.getId()));
			}
			result_page = new SettlementPageVTO();
			result_page.setStatistics(statistics);
			if(!agents.isEmpty()){
				//取未结清记录汇总
				List<SettlementSummaryDTO> settledSummary = agentSettlementsRecordMService.summaryAggregationBetween(agents, 
						new Object[]{AgentSettlementsRecordMDTO.Settlement_Bill_Done,AgentSettlementsRecordMDTO.Settlement_Bill_Parted}, 
						null, null, 1, agents.size());
				List<User> users = userService.findByIds(agents, true, true);
				SettlementVTO vto = null;
				int index = 0;
				for(SettlementSummaryDTO summaryDTO:unsettledSummaryMain){
					vto = new SettlementVTO();
					User user = users.get(index);
					vto.setIndex(index);
					vto.setOrg(user != null?user.getOrg():StringHelper.EMPTY_STRING);
					vto.setUid(user.getId());
					//vto.setTr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(summaryDTO.getMoney(),2))));
					/*String previosMonth = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfMonthAgo(new Date(),1), DateTimeHelper.FormatPattern11);
					AgentSettlementsRecordMDTO previosMonth_settlement = agentSettlementsRecordMService.getSettlement(previosMonth, user.getId());
					if(previosMonth_settlement != null)
						vto.setLsr(ArithHelper.getFormatter(String.valueOf(previosMonth_settlement.getiSVPrice())));
					else
						vto.setLsr("0.00");*/
					vto.setTr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummarySettled(summaryDTO.getId(),settledSummary),2))));
					vto.setUr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummaryUnsettled(summaryDTO.getId(),unsettledSummaryMain),2))));
					settleVtos.add(vto);
					index++;
				}
			}else{
				TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,0, settleVtos);
				result_page.setPages(settlement_pages);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
			}
			TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,(int)statistics.getUs(), settleVtos);
			result_page.setPages(settlement_pages);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	private SettlementStatisticsVTO statistics(int agent){
		SettlementStatisticsVTO result = agentSettlementsRecordMService.statistics(agent);
		ModelCriteria mc_user = new ModelCriteria();
		mc_user.createCriteria().andColumnEqualTo("utype", UserType.Agent.getIndex()).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		int total = userService.countByModelCriteria(mc_user);
		result.setTs(total);
		return result;
	}
	
	
/*	public RpcResponseDTO<SettlementPageVTO> pageSettlements(int operator_user,String dateCurrent,int pageNo, int pageSize) {
		SettlementPageVTO result_page = null;
		List<SettlementVTO> settleVtos = null;
		try{
			settleVtos = new ArrayList<SettlementVTO>();
			ModelCriteria mc_user = new ModelCriteria();
			mc_user.createCriteria().andColumnEqualTo("utype", User.Agent_User).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
			mc_user.setPageNumber(pageNo);
			mc_user.setPageSize(pageSize);
			TailPage<User> userPages = userService.findModelTailPageByModelCriteria(mc_user);
			if(userPages.getItems().isEmpty()){
				result_page = new SettlementPageVTO();
				result_page.setStatistics(fetchAgentSettlementStatistics(0));
				TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,0, new ArrayList<SettlementVTO>());
				result_page.setPages(settlement_pages);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
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
					vto.setTr(ArithHelper.getFormatter(String.valueOf(ChargingCurrencyHelper.currency(rsd.getT_dod()))));
				}else{
					vto.setTr("0.00");
				}
				AgentWholeMonthMDTO preMonth = agentWholeMonthMService.getWholeMonth(previosMonth, user.getId());
				if(preMonth != null)
					vto.setLsr(ArithHelper.getFormatter(String.valueOf(ChargingCurrencyHelper.currency(preMonth.getDod()))));
				else
					vto.setLsr("0.00");
				AgentWholeMonthMDTO curMonth = agentWholeMonthMService.getWholeMonth(currentMonth, user.getId());
				if(curMonth != null)
					vto.setUr(ArithHelper.getFormatter(String.valueOf(ChargingCurrencyHelper.currency(curMonth.getDod()))));
				else
					vto.setUr("0.00");
				settleVtos.add(vto);
			}
			
			result_page = new SettlementPageVTO();
			result_page.setStatistics(fetchAgentSettlementStatistics(operator_user));
			
			TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,userPages.getTotalItemsCount(), settleVtos);
			result_page.setPages(settlement_pages);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	private SettlementStatisticsVTO fetchAgentSettlementStatistics(int operator_user){
		SettlementStatisticsVTO result = new SettlementStatisticsVTO();
		if(operator_user > 0){
			result.setU(operator_user);
			int ts = RandomData.intNumber(260,320);
			int sd = RandomData.intNumber(240,250);
			int us = ts-sd;
			result.setTs(ts);
			result.setSd(sd);
			result.setUs(us);
			result.setC_at(DateTimeHelper.formatDate(DateTimeHelper.DefalutFormatPattern));
		}
		return result;
	}
	
	private RecordSummaryDTO distillRecordSummaryDTO(List<RecordSummaryDTO> summary,int user){
		for(RecordSummaryDTO dto:summary){
			if(dto.getId().equals(String.valueOf(user)))
				return dto;
		}
		return null;
	}*/
	
	public RpcResponseDTO<AgentDeviceStatisticsVTO> fetchAgentDeviceStatistics(int agentuser){
		if(agentuser <= 0){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}

		User operUser = userService.getById(agentuser);
		UserTypeValidateService.validUserType(operUser, UserType.WarehouseManager.getSname());


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
