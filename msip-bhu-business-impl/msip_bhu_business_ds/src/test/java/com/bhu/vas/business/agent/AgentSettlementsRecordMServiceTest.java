package com.bhu.vas.business.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.agent.vto.SettlementStatisticsVTO;
import com.bhu.vas.business.ds.agent.mdto.AgentSettlementsRecordMDTO;
import com.bhu.vas.business.ds.agent.mservice.AgentSettlementsRecordMService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;

/**
 * 
 * @author Edmond
 *
 */
public class AgentSettlementsRecordMServiceTest extends BaseTest {

    @Resource
    public AgentSettlementsRecordMService agentSettlementsRecordMService;

    public static List<Integer> agents = new ArrayList<Integer>();
    static{
    	agents.add(100025);
    	agents.add(100083);
    	agents.add(100084);
    	agents.add(100085);
		agents.add(100089);
		agents.add(100090);
		agents.add(100091);
		agents.add(100092);
		agents.add(100093);
		agents.add(100094);
    }
    
    //@Test
    public void statisticsTest(){
    	SettlementStatisticsVTO statistics = agentSettlementsRecordMService.statistics(0);
    	System.out.println(statistics.getTs());
    	System.out.println(statistics.getSd());
    	System.out.println(statistics.getUs());
    	System.out.println(statistics.getU());
    	System.out.println(statistics.getC_at());
    }
    
   // @Test
    /*public void summartAggregationBetweenTest(){
    	List<Integer> agents = new ArrayList<Integer>();
    	agents.add(100093);
    	List<SettlementSummaryDTO> summary = agentSettlementsRecordMService.summaryAggregationBetween(null, 1, null, "2015-03", 1, 20);
    	System.out.println("result size:"+summary.size());
    	for(SettlementSummaryDTO dto:summary){
    		System.out.println(String.format("id[%s] money[%s]", dto.getId(),dto.getMoney()));
    	}
    	SettlementStatisticsVTO statistics = agentSettlementsRecordMService.statistics(0);
    	System.out.println(statistics.getTs());
    	System.out.println(statistics.getSd());
    	System.out.println(statistics.getUs());
    	System.out.println(statistics.getU());
    	System.out.println(statistics.getC_at());
    }*/
    
    //@Test
    public void A_fetchBillsByAgent(){
    	List<AgentSettlementsRecordMDTO> bills = agentSettlementsRecordMService.fetchBillsByAgent(100084, AgentSettlementsRecordMDTO.Settlement_Bill_Created,AgentSettlementsRecordMDTO.Settlement_Bill_Parted);
		for(AgentSettlementsRecordMDTO dto:bills){
			System.out.println(dto.getAgent()+"  "+dto.getDate()+"  "+dto.getId()+" "+dto.getiSVPrice()+" "+dto.getSdPrice()+"  "+dto.getStatus());
		}
    }
    @Test
    public void B_iterateSettleBills(){
    	A_fetchBillsByAgent();
    	
    	String result = agentSettlementsRecordMService.iterateSettleBills(3,"李文华",100084,2000);//ArithHelper.sub(1891.85,80.49));
		System.out.println(result);
		
		A_fetchBillsByAgent();
    }
    
    //@Test
    public void batchCreate() {
    	Date current = new Date();
    	AgentSettlementsRecordMDTO record = null;
    	//创建3年的数据
    	for(int ago=0;ago<=36;ago++){
    		Date monthAgo = DateTimeHelper.getDateFirstDayOfMonthAgo(current,ago);
    		String monthly = DateTimeHelper.formatDate(monthAgo, DateTimeHelper.FormatPattern11);
    		for(Integer agent:agents){
    			record = new AgentSettlementsRecordMDTO();
    			record.setId(AgentSettlementsRecordMDTO.generateId(monthly, agent));
    			record.setAgent(agent);
    			record.setDate(monthly);
    			record.setiSVPrice(ArithHelper.round(RandomData.floatNumber(800,2000), 2));
    			if(ago == 0){
    				record.setStatus(AgentSettlementsRecordMDTO.Settlement_Bill_Created);
    				record.setCreated_at(DateTimeHelper.formatDate(monthAgo,DateTimeHelper.FormatPattern1));
    			}else{
    				if(RandomData.flag()){//此种情况下结算一部分或者不结算
    					if(RandomData.flag()){//结算一部分
    						record.setSdPrice(ArithHelper.div(record.getiSVPrice(), 2, 2));//都结算清楚了 sdPrice = iSVPrice
        					record.setStatus(AgentSettlementsRecordMDTO.Settlement_Bill_Parted);
    						record.setReckoner(3);
            				record.setCreated_at(DateTimeHelper.formatDate(monthAgo,DateTimeHelper.FormatPattern1));
            				record.setSettled_at(DateTimeHelper.formatDate(monthAgo,DateTimeHelper.FormatPattern1));
    					}else{//不结算
    						record.setStatus(AgentSettlementsRecordMDTO.Settlement_Bill_Created);
    	    				record.setCreated_at(DateTimeHelper.formatDate(monthAgo,DateTimeHelper.FormatPattern1));
    					}
    				}else{
    					record.setSdPrice(record.getiSVPrice());//都结算清楚了 sdPrice = iSVPrice
    					record.setStatus(AgentSettlementsRecordMDTO.Settlement_Bill_Done);
        				record.setReckoner(3);
        				record.setCreated_at(DateTimeHelper.formatDate(monthAgo,DateTimeHelper.FormatPattern1));
        				record.setSettled_at(DateTimeHelper.formatDate(monthAgo,DateTimeHelper.FormatPattern1));
    				}
    			}
    			agentSettlementsRecordMService.save(record);
    		}
    	}
    }

}
