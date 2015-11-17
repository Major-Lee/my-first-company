package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.agent.model.AgentBillSummaryView;
import com.bhu.vas.api.rpc.agent.vto.AgentDeviceStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.AgentRevenueStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementPageVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.local.serviceimpl.BusinessCacheService;
import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.facade.AgentBillFacadeService;
import com.bhu.vas.business.ds.agent.helper.AgentHelper;
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
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
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
	private AgentBillFacadeService agentBillFacadeService;
	//@Resource
	//private AgentSettlementsRecordMService agentSettlementsRecordMService;
	
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
			vto.setRcm(ArithHelper.getFormatter(String.valueOf(currentmonth_data!=null?AgentHelper.currency(currentmonth_data.getDod(),currentmonth_data.getNewdevices()):0.00d)));
			//上月收入(元)  
			Date previousMonthDate = DateTimeHelper.getDateFirstDayOfMonthAgo(currentDate,1);
			String previosMonth = DateTimeHelper.formatDate(previousMonthDate, DateTimeHelper.FormatPattern11);
			AgentWholeMonthMDTO previosmonth_data = agentWholeMonthMService.getWholeMonth(previosMonth, user);
			vto.setRlm(ArithHelper.getFormatter(String.valueOf(previosmonth_data!=null?AgentHelper.currency(previosmonth_data.getDod(),previosmonth_data.getNewdevices()):0.00d)));
			//昨日收入(元)  
			Date yesterdayMonthDate = DateTimeHelper.getDateDaysAgo(currentDate, 1);//(currentDate,1);
			String yesterday = DateTimeHelper.formatDate(yesterdayMonthDate,DateTimeHelper.FormatPattern5);
			AgentWholeDayMDTO yesterday_data = agentWholeDayMService.getWholeDay(yesterday, user);
			vto.setRyd(ArithHelper.getFormatter(String.valueOf(yesterday_data!=null?AgentHelper.currency(yesterday_data.getDod(),yesterday_data.getNewdevices()):0.00d)));
			//结算过的总收入(元)
			//所有设备产生收益的总数 -取agentWholeMonth此用户的所有在线时长
			RecordSummaryDTO summary = agentWholeMonthMService.summaryAggregationTotal4User(user);
			//vto.setOd(ArithHelper.getFormatter(String.valueOf(summary.getT_devices())));
			//vto.setRtl(ArithHelper.getFormatter(String.valueOf(ChargingCurrencyHelper.currency(summary.getT_dod()))));
			vto.setTr(ArithHelper.getFormatter(String.valueOf(AgentHelper.currency(summary.getT_dod(),summary.getT_newdevices()))));
			pageTotalSettlements4Agent(user,vto);
			/*Date dateEnd = DateTimeHelper.parseDate(dateEndStr, DateTimeHelper.FormatPattern5);
			Date dateStart = DateTimeExtHelper.getFirstDateOfMonth(dateEnd);
			List<AgentWholeDayMDTO> results = agentWholeDayMService.fetchByDateBetween(user, DateTimeHelper.formatDate(dateStart, DateTimeHelper.FormatPattern5), dateEndStr);
			//vto.setCharts(new HashMap<String,Double>());
			Map<String,Double> charts = new HashMap<>();
			int max = 0;
			for(AgentWholeDayMDTO dto:results){
				int whichday = DateTimeExtHelper.getDay(DateTimeHelper.parseDate(dto.getDate(), DateTimeHelper.FormatPattern5));
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
			vto.setCharts(SortMapHelper.sortMapByKey(charts));*/
			
			Map<String,Object> charts = new HashMap<>();
			//本月charts
			String yesterdayMonth = DateTimeHelper.formatDate(yesterdayMonthDate, DateTimeHelper.FormatPattern11);
			charts.put(yesterdayMonth, buildChartsData(user,yesterdayMonthDate,true));
			//上月charts
			//获取yesterdayMonthDate的上一个月的日期
			Date previousMonthDateOfYesterday = DateTimeHelper.getDateFirstDayOfMonthAgo(yesterdayMonthDate,1);
			String previousMonthOfYesterday = DateTimeHelper.formatDate(previousMonthDateOfYesterday, DateTimeHelper.FormatPattern11);
			charts.put(previousMonthOfYesterday, buildChartsData(user,previousMonthDateOfYesterday,false));
			vto.setCharts(charts);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	private Map<String,List<Object>> buildChartsData(int agent,Date date,boolean isSameMonth){
		
		Map<String,List<Object>> result = new HashMap<>();
		List<Object> days = new ArrayList<Object>();
		List<Object> dayvals = new ArrayList<Object>();
		{//初始化数据
			int passDayOfMonth = 0;
			if(isSameMonth){//当前月
				passDayOfMonth = DateTimeExtHelper.getPassDayOfMonth(date);
			}else{
				passDayOfMonth = DateTimeExtHelper.getDayOfMonth(date);
			}
			for(int i=1;i<=passDayOfMonth;i++){
				days.add(String.format("%02d日", i));
				dayvals.add(ArithHelper.round(0.00d, 2));
			}
		}
		//String dateKey = DateTimeHelper.formatDate(date, DateTimeHelper.FormatPattern11);
		Date firstDate = DateTimeExtHelper.getFirstDateOfMonth(date);
		Date lastDate = DateTimeExtHelper.getLastDateOfMonth(date);
		List<AgentWholeDayMDTO> results = agentWholeDayMService.fetchByDateBetween(agent, 
				DateTimeHelper.formatDate(firstDate, DateTimeHelper.FormatPattern5),
				DateTimeHelper.formatDate(lastDate, DateTimeHelper.FormatPattern5));
		for(AgentWholeDayMDTO dto:results){
			int whichday = DateTimeExtHelper.getDay(DateTimeHelper.parseDate(dto.getDate(), DateTimeHelper.FormatPattern5));
			dayvals.set(whichday-1, AgentHelper.currency(dto.getDod(),dto.getNewdevices()));
		}
		result.put("days", days);
		result.put("dayvals", dayvals);
		return result;
		//Date dateEnd = DateTimeHelper.parseDate(dateEndStr, DateTimeHelper.FormatPattern5);
		//Date dateStart = DateTimeExtHelper.getFirstDateOfMonth(dateEnd);
	}

	/**
	 * 获取指定代理商 的所有 已结算的金额和待结算的金额
	 * @param agent
	 * @param vto
	 */
	private void pageTotalSettlements4Agent(int agent,AgentRevenueStatisticsVTO vto) {
		AgentBillSummaryView sview = agentBillFacadeService.getAgentBillSummaryViewService().getById(agent);
		if(sview != null){
			vto.setSr(ArithHelper.getFormatter(String.valueOf(sview.getSd_t_price())));
			vto.setUr(ArithHelper.getFormatter(String.valueOf(ArithHelper.sub(sview.getT_price(), sview.getSd_t_price()))));
		}else{
			vto.setSr(ArithHelper.getFormatter("0.00d"));
			vto.setUr(ArithHelper.getFormatter("0.00d"));
		}
		//vto.setSr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummarySettled(String.valueOf(agent),mainSummary),2))));
		//vto.setUr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummaryUnsettled(String.valueOf(agent),mainSummary),2))));

		/*List<Integer> agents = new ArrayList<Integer>();
			agents.add(agent);
		if(!agents.isEmpty()){
				//取所有记录汇总
			List<SettlementSummaryDTO> mainSummary = agentSettlementsRecordMService.summaryAggregationBetween(agents, 
						null, 
						null, null, 1, agents.size());
			vto.setSr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummarySettled(String.valueOf(agent),mainSummary),2))));
			vto.setUr(ArithHelper.getFormatter(String.valueOf(ArithHelper.round(fetchSettlementSummaryUnsettled(String.valueOf(agent),mainSummary),2))));
		}*/
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
				vto.setR(ArithHelper.getCurrency(String.valueOf(AgentHelper.currency(dto.getDod(),dto.getNewdevices()))));
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
	/*private double fetchSettlementSummarySettled(String agent,List<SettlementSummaryDTO> summary){
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
	}*/
	/**
	 * 代理商结算页面列表（管理员可以访问）
	 * 1、获取代理商列表
	 * 2、获取代理商本月以前（不包括本月）的列表记录，作为已结算金额
	 * 3、获取代理商本月的列表记录，作为未结算金额
	 * 4、获取代理商上月的列表记录，作为上月结算金额
	 * @param uid
	 * @param viewstatus 
	 * 			-1 所有			所有代理商列表
	 * 			 1 settled 		所有已结清的代理商列表
	 * 			 0 unsettled	所有未结清的代理商列表
	 * @param q
	 * @param q
	 * @param q
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public RpcResponseDTO<SettlementPageVTO> pageSettlements(int operator_user,int viewstatus,String q,String sort_field,boolean desc, int pageNo, int pageSize){
		try{
			return pageAgentBillsSettlements(operator_user,viewstatus,q,sort_field,desc, pageNo, pageSize);
			//获取主界面显示结构 agents 列表
			/*if(viewstatus == -1){
				return pageTotalSettlements(operator_user, pageNo, pageSize);
				//mc_view.createCriteria().andColumnEqualTo("status", AgentBillSummaryView.SummaryView_Settled).andSimpleCaulse(" 1=1 ");
			}else{
				return pageAgentBillsSettlements(operator_user,viewstatus, pageNo, pageSize);
			}*/
			/*mc_view.setOrderByClause(" id desc ");
			mc_view.setPageNumber(pageNo);
			mc_view.setPageSize(pageSize);
			TailPage<AgentBillSummaryView> pages = agentBillFacadeService.getAgentBillSummaryViewService().findModelTailPageByModelCriteria(mc_view);
			result_page = new SettlementPageVTO();
			result_page.setStatistics(statistics);
			if(!pages.isEmpty()){
				SettlementVTO vto = null;
				int index = 0;
				//for(SettlementSummaryDTO summaryDTO:summaryMain){
				for(AgentBillSummaryView sview:pages.getItems()){
					vto = new SettlementVTO();
					vto.setIndex(index);
					User user = userService.getById(sview.getId());
					vto.setOrg(user != null?user.getOrg():"未知");
					vto.setUid(sview.getId());
					vto.setTr(ArithHelper.getFormatter(String.valueOf(sview.getSd_t_price())));
					vto.setUr(ArithHelper.getFormatter(String.valueOf(sview.getT_price() - sview.getSd_t_price())));
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
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);*/
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/*private RpcResponseDTO<SettlementPageVTO> pageTotalSettlements(int operator_user,int pageNo, int pageSize) {
		SettlementPageVTO result_page = null;
		List<SettlementVTO> settleVtos = null;
		try{
			settleVtos = new ArrayList<SettlementVTO>();
			SettlementStatisticsVTO statistics = this.statistics();
			result_page = new SettlementPageVTO();
			result_page.setStatistics(statistics);
			//获取主界面显示结构 agents 列表
			ModelCriteria mc_user = new ModelCriteria();
			mc_user.createCriteria().andColumnEqualTo("utype", UserType.Agent.getIndex()).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
			mc_user.setOrderByClause(" id desc ");
			mc_user.setPageNumber(pageNo);
			mc_user.setPageSize(pageSize);
			TailPage<User> userPages = userService.findModelTailPageByModelCriteria(mc_user);
			if(!userPages.isEmpty()){
				List<Integer> agents = IdHelper.getPKs(userPages.getItems(), Integer.class);
				List<AgentBillSummaryView> sviews = agentBillFacadeService.getAgentBillSummaryViewService().findByIds(agents, true, true);
				SettlementVTO vto = null;
				int index = 0;
				for(User user:userPages.getItems()){
					vto = new SettlementVTO();
					vto.setIndex(index);
					vto.setOrg(user.getOrg());
					vto.setUid(user.getId());
					vto.setOrg(user.getOrg());
					AgentBillSummaryView sview = sviews.get(index);
					if(sview != null){
						vto.setTr(ArithHelper.getFormatter(String.valueOf(sview.getSd_t_price())));
						vto.setUr(ArithHelper.getFormatter(String.valueOf(sview.getT_price() - sview.getSd_t_price())));
					}else{
						vto.setTr(ArithHelper.getFormatter("0.00"));
						vto.setUr(ArithHelper.getFormatter("0.00"));
					}
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
	}*/
	
	/**
	 * 代理商结算页面列表（管理员可以访问）
	 * 1、获取代理商列表
	 * 2、获取代理商本月以前（不包括本月）的列表记录，作为已结算金额
	 * 3、获取代理商本月的列表记录，作为未结算金额
	 * 4、获取代理商上月的列表记录，作为上月结算金额
	 * @param uid
	 * @param viewstatus 
	 * 			-1 所有			所有存在过单据的代理商列表
	 * 			 1 settled 		所有存在过单据的已结清的代理商列表
	 * 			 0 unsettled	所有存在过单据的未结清的代理商列表
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	private RpcResponseDTO<SettlementPageVTO> pageAgentBillsSettlements(int operator_user,int viewstatus,String q,String sort_field,boolean desc, int pageNo, int pageSize){
		SettlementPageVTO result_page = null;
		List<SettlementVTO> settleVtos = null;
		try{
			settleVtos = new ArrayList<SettlementVTO>();
			SettlementStatisticsVTO statistics = this.statistics(q);
			result_page = new SettlementPageVTO();
			result_page.setStatistics(statistics);
			//获取主界面显示结构 agents 列表
			/*ModelCriteria mc_view = new ModelCriteria();
			if(viewstatus == 1){
				mc_view.createCriteria().andColumnEqualTo("status", AgentBillSummaryView.SummaryView_Settled).andSimpleCaulse(" 1=1 ");
			}else if(viewstatus == 0){
				mc_view.createCriteria().andColumnEqualTo("status", AgentBillSummaryView.SummaryView_UnSettled).andSimpleCaulse(" 1=1 ");
			}else{//viewstatus == -1
				mc_view.createCriteria().andSimpleCaulse(" 1=1 ");
			}
			mc_view.setOrderByClause(" id desc ");
			mc_view.setPageNumber(pageNo);
			mc_view.setPageSize(pageSize);*/
			ModelCriteria mc_view = buildBlurModelCriteria(viewstatus,q,sort_field,desc,pageNo,pageSize);
			TailPage<AgentBillSummaryView> pages = agentBillFacadeService.getAgentBillSummaryViewService().findModelTailPageByModelCriteria(mc_view);
			if(!pages.isEmpty()){
				List<Integer> agents = IdHelper.getPKs(pages.getItems(), Integer.class);
				List<User> users = userService.findByIds(agents, true, true);
				SettlementVTO vto = null;
				int index = 0;
				for(AgentBillSummaryView sview:pages.getItems()){
					vto = new SettlementVTO();
					vto.setIndex(index);
					User user = users.get(index);//User user = userService.getById(sview.getId());
					vto.setOrg(user != null?user.getOrg():"未知");
					vto.setUid(sview.getId());
					//vto.setTr(ArithHelper.getFormatter(String.valueOf(sview.getSd_t_price())));
					vto.setTr(ArithHelper.getFormatter(String.valueOf(sview.getT_price())));
					vto.setUr(ArithHelper.getFormatter(String.valueOf(sview.getT_price() - sview.getSd_t_price())));
					settleVtos.add(vto);
					index++;
				}
			}else{
				TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,0, settleVtos);
				result_page.setPages(settlement_pages);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
			}
			TailPage<SettlementVTO> settlement_pages = new CommonPage<SettlementVTO>(pageNo, pageSize,pages.getTotalItemsCount(), settleVtos);
			result_page.setPages(settlement_pages);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_page);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	private static final String sortSqlFragmentTemplate = " (%s) %s ";
	
	private SettlementStatisticsVTO statistics(String q){
		SettlementStatisticsVTO result = new SettlementStatisticsVTO();
		ModelCriteria mc_total = new ModelCriteria();
		Criteria createCriteria = mc_total.createCriteria();
		if(StringUtils.isNotEmpty(q)){
			createCriteria.andColumnLike("org", "%"+q+"%");
		}
		createCriteria.andSimpleCaulse(" 1=1 ");
		int total = agentBillFacadeService.getAgentBillSummaryViewService().countByModelCriteria(mc_total);
		
		ModelCriteria mc_sd = new ModelCriteria();
		Criteria createCriteria1 = mc_sd.createCriteria();
		if(StringUtils.isNotEmpty(q)){
			createCriteria1.andColumnLike("org", "%"+q+"%");
		}
		createCriteria1.andColumnEqualTo("status", AgentBillSummaryView.SummaryView_Settled).andSimpleCaulse(" 1=1 ");
		int total_sd = agentBillFacadeService.getAgentBillSummaryViewService().countByModelCriteria(mc_sd); 
		result.setTs(total);
		result.setSd(total_sd);
		result.setUs(total- total_sd);
		result.setU(-1);
		result.setC_at(DateTimeHelper.formatDate(DateTimeHelper.DefalutFormatPattern));
		return result;
		
		
		//SettlementStatisticsVTO result = agentBillFacadeService.statistics();
		/*ModelCriteria mc_user = new ModelCriteria();
		mc_user.createCriteria().andColumnEqualTo("utype", UserType.Agent.getIndex()).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		int total = userService.countByModelCriteria(mc_user);
		result.setTs(total);*/
	}
	
	private ModelCriteria buildBlurModelCriteria(int viewstatus,String q,String sort_field,boolean desc,int pageNo, int pageSize){
		System.out.println(String.format("viewstatus[%s] q[%s] sort_field[%s] desc[%s]", viewstatus,q,sort_field,desc));
		ModelCriteria mc_view = new ModelCriteria();
		Criteria createCriteria = mc_view.createCriteria();
		if(viewstatus == 1){
			createCriteria.andColumnEqualTo("status", AgentBillSummaryView.SummaryView_Settled);
		}else if(viewstatus == 0){
			createCriteria.andColumnEqualTo("status", AgentBillSummaryView.SummaryView_UnSettled);
		}else{//viewstatus == -1
			createCriteria.andSimpleCaulse(" 1=1 ");
		}
		if(StringUtils.isNotEmpty(q)){
			createCriteria.andColumnLike("org", "%"+q+"%");
		}
		validateSortField(sort_field);
		if(SettlementVTO.Sort_Field_ORG.equals(sort_field)){
			mc_view.setOrderByClause(String.format(sortSqlFragmentTemplate, "org",desc?"DESC":"ASC"));
		}
		if(SettlementVTO.Sort_Field_TR.equals(sort_field)){
			mc_view.setOrderByClause(String.format(sortSqlFragmentTemplate, "t_price",desc?"DESC":"ASC"));
		}
		if(SettlementVTO.Sort_Field_UR.equals(sort_field)){
			mc_view.setOrderByClause(String.format(sortSqlFragmentTemplate, "t_price - sd_t_price",desc?"DESC":"ASC"));
		}
		mc_view.setPageNumber(pageNo);
		mc_view.setPageSize(pageSize);
		return mc_view;
	}
	
	private boolean validateSortField(String sort_field){
		if(StringUtils.isNotEmpty(sort_field)) sort_field = SettlementVTO.Sort_Field_UR;
		if(SettlementVTO.Sort_Field_ORG.equals(sort_field) || SettlementVTO.Sort_Field_TR.equals(sort_field) || SettlementVTO.Sort_Field_UR.equals(sort_field)){
			return true;
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.AGENT_SETTLEMENT_ACTION_FINANCE_SEARCH_SORTFIELD_PARAM_ERROR);
		}
	}
	
	/*private RpcResponseDTO<SettlementPageVTO> pageTotalSettlements(int operator_user,int pageNo, int pageSize) {
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
			for(SettlementSummaryDTO summaryDTO:summaryMain){
				agents.add(Integer.parseInt(summaryDTO.getId()));
			}
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
					String previosMonth = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfMonthAgo(new Date(),1), DateTimeHelper.FormatPattern11);
					AgentSettlementsRecordMDTO previosMonth_settlement = agentSettlementsRecordMService.getSettlement(previosMonth, user.getId());
					if(previosMonth_settlement != null)
						vto.setLsr(ArithHelper.getFormatter(String.valueOf(previosMonth_settlement.getiSVPrice())));
					else
						vto.setLsr("0.00");
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
	}*/
	
	/*private RpcResponseDTO<SettlementPageVTO> pageSettledSettlements(int operator_user,int pageNo, int pageSize) {
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
	}*/
	
	/*private RpcResponseDTO<SettlementPageVTO> pageUnSettledSettlements(int operator_user,int pageNo, int pageSize) {

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
