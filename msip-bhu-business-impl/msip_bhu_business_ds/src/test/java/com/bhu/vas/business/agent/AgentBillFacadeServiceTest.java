package com.bhu.vas.business.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.business.ds.agent.facade.AgentBillFacadeService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AgentBillFacadeServiceTest extends BaseTest {
    @Resource
    public AgentBillFacadeService agentBillFacadeService;
    
    @Test
  	public void test001BatchCreate(){
		List<Integer> agents = new ArrayList<Integer>();
    	agents.add(100119);
		agents.add(100118);
	   	Date current = new Date();
    	//创建1年的数据
    	for(int ago=0;ago<=12;ago++){
    		Date monthAgo = DateTimeHelper.getDateFirstDayOfMonthAgo(current,ago);
    		String monthly = DateTimeHelper.formatDate(monthAgo, DateTimeHelper.FormatPattern11);
    		for(Integer agent:agents){
    			agentBillFacadeService.newBillCreated(agent, monthly, ArithHelper.round(RandomData.floatNumber(200,500), 2));
    		}
    	}
    	
    	for(Integer agent:agents){
    		agentBillFacadeService.billSummaryViewGen(agent);
		}
  	}
    
    @Test
    public void test002IterateSettleBills(){
    	agentBillFacadeService.iterateSettleBills(90021, "财务2", 100118, 2000);
    	
    	agentBillFacadeService.billSummaryViewGen(100118);
    }
}
