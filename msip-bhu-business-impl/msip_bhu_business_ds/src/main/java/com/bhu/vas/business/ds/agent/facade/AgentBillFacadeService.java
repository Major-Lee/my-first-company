package com.bhu.vas.business.ds.agent.facade;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.model.AgentBillSettlements;
import com.bhu.vas.api.rpc.agent.model.AgentBillSummaryView;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceMark;
import com.bhu.vas.api.rpc.agent.vto.SettlementStatisticsVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.agent.mdto.AgentSettlementsRecordMDTO;
import com.bhu.vas.business.ds.agent.service.AgentBillSettlementsService;
import com.bhu.vas.business.ds.agent.service.AgentBillSummaryViewService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceMarkService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class AgentBillFacadeService {
	@Resource
	private AgentBillSettlementsService agentBillSettlementsService;
	
	@Resource
	private AgentBillSummaryViewService agentBillSummaryViewService;
	
	@Resource
	private AgentDeviceMarkService agentDeviceMarkService;
	
	@Resource
	private UserService userService;
	
	public AgentBillSettlementsService getAgentBillSettlementsService() {
		return agentBillSettlementsService;
	}

	public AgentBillSummaryViewService getAgentBillSummaryViewService() {
		return agentBillSummaryViewService;
	}

	/**
	 * 统计代理商管理页面中的 
	 * 所有 所有代理商
	 * 如果agent>0 则是所有用户汇总统计
	 * @param agent
	 * @return
	 */
	public SettlementStatisticsVTO statistics(){
		SettlementStatisticsVTO result = new SettlementStatisticsVTO();
		ModelCriteria mc_total = new ModelCriteria();
		mc_total.createCriteria().andSimpleCaulse(" 1=1 ");
		int total = agentBillSummaryViewService.countByModelCriteria(mc_total);
		ModelCriteria mc_sd = new ModelCriteria();
		mc_sd.createCriteria().andColumnEqualTo("status", AgentBillSummaryView.SummaryView_Settled).andSimpleCaulse(" 1=1 ");
		int total_sd = agentBillSummaryViewService.countByModelCriteria(mc_sd); 
		result.setTs(total);
		result.setSd(total_sd);
		result.setUs(total- total_sd);
		result.setU(-1);
		result.setC_at(DateTimeHelper.formatDate(DateTimeHelper.DefalutFormatPattern));
		return result;
	}
	
	/**
	 * 获取系统中所有的单据数量
	 * @return
	 */
	public long countAllBills(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause(" id asc ");
		return agentBillSettlementsService.countByModelCriteria(mc);
	}
	
	/**
	 * 获取系统中指定代理商 指定状态的单据数量
	 * @param agent
	 * @param status
	 * @return
	 */
	public long countBillsOfAgent(int agent,Integer... status){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("agent", agent).andColumnIn("status", ArrayHelper.toList(status)).andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause(" id asc ");
		return agentBillSettlementsService.countByModelCriteria(mc);
	}
	
	/**
	 * 对指定代理商进行结算 指定金额 对未结算的bills列表进行结算
	 * @param agent
	 * @param price
	 */
	public String iterateSettleBills(int operator,String operNick,int agent,double inputprice){
		if(inputprice <= 0)
			throw new BusinessI18nCodeException(ResponseErrorCode.AGENT_SETTLEMENT_ACTION_SETTLEBILLS_PRINCE_MUST_LARGERTHENZERO);
		AgentBillSummaryView sview = agentBillSummaryViewService.getById(agent);
		if(sview == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST);
		if(sview.getStatus() == AgentBillSummaryView.SummaryView_Settled)
			throw new BusinessI18nCodeException(ResponseErrorCode.AGENT_SETTLEMENT_ACTION_SETTLEBILLS_AGENT_ALREADY_SETTLED_DONE);
		if(inputprice > ArithHelper.sub(sview.getT_price(), sview.getSd_t_price()))
			throw new BusinessI18nCodeException(ResponseErrorCode.AGENT_SETTLEMENT_ACTION_SETTLEBILLS_PRINCE_MUST_EQUALORLESSTHENSUB);
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("结算总金额[%s]<BR/>\n", inputprice));
		if(agent >0 && inputprice>0){
			double price = inputprice;
			List<AgentBillSettlements> bills = fetchBillsByAgent(agent, AgentBillSettlements.Bill_Created,AgentBillSettlements.Bill_Parted);
			Iterator<AgentBillSettlements> iter = bills.iterator();
			String settled_at =  DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1);
			sb.append(String.format("结算日期[%s]<BR/>\n", settled_at));
			while(iter.hasNext()){//逐条进行结算
				AgentBillSettlements bill = iter.next();
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
					agentBillSettlementsService.update(bill);
				}
				if(price <= 0) break;
			}
			//重新生成SummaryView
			//方式1：重新全部计算
			//billSummaryViewGen(agent);
			//方式2：直接更新SummaryView
			billSummaryViewDecrease(operator,agent,inputprice);
			sb.append(String.format("剩余金额[%s]<BR/>\n", price));
		}
		return sb.toString();
	}
	
	
	/**
	 * 获取指定代理商指定状态的所有bills列表
	 * @param agent
	 * @param status
	 * @return
	 */
	public List<AgentBillSettlements> fetchBillsByAgent(int agent,Integer... status){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("agent", agent).andColumnIn("status", ArrayHelper.toList(status)).andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause(" id asc ");
		return agentBillSettlementsService.findModelByModelCriteria(mc);
	}
	
	private void billSummaryViewDecrease(int operator,int agent,double price){//real_sd_price){
		AgentBillSummaryView sview = agentBillSummaryViewService.getById(agent);
		if(sview != null){
			double need_sd_price = ArithHelper.sub(sview.getT_price(), sview.getSd_t_price());
			if(need_sd_price<=price){
				sview.setSd_t_price(sview.getT_price());
				sview.setStatus(AgentBillSummaryView.SummaryView_Settled);
			}else{
				sview.setSd_t_price(ArithHelper.add(sview.getSd_t_price(), price));
				sview.setStatus(AgentBillSummaryView.SummaryView_UnSettled);
			}
			sview.setLast_reckoner(operator);
			sview.setSettled_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
			agentBillSummaryViewService.update(sview);
			//sview.setSd_t_price(ArithHelper.add(v1, v2)real_sd_price+sview.getSd_t_price());
		}
	}
	/**
	 * 代理商单据汇总记录生成
	 * @param agent
	 */
	public void billSummaryViewGen(int agent){
		if(agent <=0) return;
		double t_price = 0d;
		double sd_t_price = 0d;
		int last_reckoner = 0;
		String last_settled_at = null;
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("agent", agent).andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause(" id asc ");
    	mc.setPageNumber(1);
    	mc.setPageSize(300);
		EntityIterator<String, AgentBillSettlements> it = new KeyBasedEntityBatchIterator<String,AgentBillSettlements>(String.class,AgentBillSettlements.class, agentBillSettlementsService.getEntityDao(), mc);
		while(it.hasNext()){
			List<AgentBillSettlements> bills = it.next();
			for(AgentBillSettlements bill:bills){
				t_price = ArithHelper.add(t_price, bill.getiSVPrice());
				sd_t_price = ArithHelper.add(sd_t_price, bill.getSdPrice());
				if(bill.getStatus() != AgentBillSettlements.Bill_Created){
					last_reckoner = bill.getReckoner();
				}
				if(last_settled_at == null) last_settled_at = bill.getSettled_at();
				else{
					if(bill.getSettled_at() != null) {
						if(last_settled_at.compareTo(bill.getSettled_at()) < 0){
							last_settled_at = bill.getSettled_at();
						}
					}
				}
			}
		}
		boolean billsExist = it.getTotalItemsCount()>0;
		AgentBillSummaryView sview = agentBillSummaryViewService.getById(agent);
		User agentUser = userService.getById(agent);
		if(sview != null){
			sview.setT_price(t_price);
			sview.setSd_t_price(sd_t_price);
			if(billsExist)
				sview.setStatus(sd_t_price>=t_price?AgentBillSummaryView.SummaryView_Settled:AgentBillSummaryView.SummaryView_UnSettled);
			else
				sview.setStatus(AgentBillSummaryView.SummaryView_Empty);
			sview.setLast_reckoner(last_reckoner);
			sview.setSettled_at(last_settled_at);
			sview.setOrg(agentUser!=null?agentUser.getOrg():null);
			agentBillSummaryViewService.update(sview);
		}else{
			sview = new AgentBillSummaryView();
			sview.setId(agent);
			sview.setT_price(t_price);
			sview.setSd_t_price(sd_t_price);
			if(billsExist)
				sview.setStatus(sd_t_price>=t_price?AgentBillSummaryView.SummaryView_Settled:AgentBillSummaryView.SummaryView_UnSettled);
			else
				sview.setStatus(AgentBillSummaryView.SummaryView_Empty);
			sview.setLast_reckoner(last_reckoner);
			sview.setSettled_at(last_settled_at);
			sview.setOrg(agentUser!=null?agentUser.getOrg():null);
			agentBillSummaryViewService.insert(sview);
		}
	}
	
	/**
	 * 单据记录生成
	 * @param agent
	 * @param date
	 * @param iSVPrice
	 */
	public void newBillCreated(int agent,String date,double iSVPrice){
		String bill_id = AgentBillSettlements.generateId(date, agent);
		AgentBillSettlements billSettlements = agentBillSettlementsService.getById(bill_id);
		if(billSettlements != null){//这种情况理论是不应该存在，存在于重复生成数据的情况下
			//如果发生这种情况则覆盖记录，覆盖规则为如下
			double old_iSVPrice = billSettlements.getiSVPrice();
			int old_status = billSettlements.getStatus();
			billSettlements.setiSVPrice(iSVPrice);
			if(iSVPrice<=old_iSVPrice){
				billSettlements.setStatus(old_status);
			}/*else if(old_iSVPrice < iSVPrice){
				billSettlements.setStatus(old_status);
			}*/else{//iSVPrice > old_iSVPrice
				if(old_status == AgentBillSettlements.Bill_Done || old_status == AgentBillSettlements.Bill_Parted){
					billSettlements.setStatus(AgentBillSettlements.Bill_Parted);
				}else{
					billSettlements.setStatus(old_status);
				}
			}
			agentBillSettlementsService.update(billSettlements);
		}else{
			billSettlements = new AgentBillSettlements();
			billSettlements.setId(bill_id);
			billSettlements.setiSVPrice(iSVPrice);
			billSettlements.setAgent(agent);
			billSettlements.setDate(date);
			billSettlements.setReckoner(0);
			billSettlements.setSdPrice(0d);
			billSettlements.setStatus(AgentBillSettlements.Bill_Created);
			billSettlements.setSettled_at(null);
			agentBillSettlementsService.insert(billSettlements);
		}
	}
	
	
	/**
     * 标记设备FirstCashBack状态
     * 如果表中不存在mac数据，则录入数据need_afcb true firstcb true并标记日期 并返回true
     * 如果表中存在mac数据，
     * 		判定firstcb =true 则返回false 
     * 		判定firstcb = false 则update firstcb true返回true
     * 特例，可能有部分设备是不需要进行第一次返现，这部分设备会初始录入此表，并且need_afcb=false
     * @param sn
     * @return
     */
    public boolean markFirstCashBack(String sn,String date){
    	AgentDeviceMark mark = agentDeviceMarkService.getById(sn);
    	if(mark == null){
    		mark = new AgentDeviceMark();
    		mark.setId(sn);
    		mark.setNeed_afcb(true);
    		mark.setAfcb(true);
    		mark.setAfcb_date(date);
    		agentDeviceMarkService.insert(mark);
    		return true;
    	}else{
    		if(mark.isNeed_afcb()){
    			if(mark.isAfcb()){
        			return false;
        		}else{
        			mark.setAfcb(true);
            		mark.setAfcb_date(date);
            		agentDeviceMarkService.update(mark);
            		return true;
        		}
    		}else{
    			return false;
    		}
    	}
    }
}
