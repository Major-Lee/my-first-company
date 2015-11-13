package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.agent.facade.AgentBillFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.RandomData;

/**
 * 对指定月进行结算
 * 每月1号进行上月数据结算，并生成代理商结算记录到AgentSettlementsRecordMDTO
 * 结算过程中 可能需要对已经结算过的月进行重新结算，所有对于记录需要判定是否存在，存在的话，只更新需要结算的金额后再保存
 * 对于终端则无需这样
 * @author Edmond
 *
 */
public class MonthlyChargingSettlementTestGenOper {

	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
		/*AgentSettlementsRecordMService agentSettlementsRecordMService = (AgentSettlementsRecordMService)ctx.getBean("agentSettlementsRecordMService");
		
    	Date current = new Date();
    	AgentSettlementsRecordMDTO record = null;
    	//创建1年的数据
    	for(int ago=0;ago<=12;ago++){
    		Date monthAgo = DateTimeHelper.getDateFirstDayOfMonthAgo(current,ago);
    		String monthly = DateTimeHelper.formatDate(monthAgo, DateTimeHelper.FormatPattern11);
    		for(Integer agent:agents){
    			record = new AgentSettlementsRecordMDTO();
    			record.setId(AgentSettlementsRecordMDTO.generateId(monthly, agent));
    			record.setAgent(agent);
    			record.setDate(monthly);
    			record.setiSVPrice(ArithHelper.round(RandomData.floatNumber(200,500), 2));
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
    	}*/
		AgentBillFacadeService agentBillFacadeService = (AgentBillFacadeService)ctx.getBean("agentBillFacadeService");
		UserService userService = (UserService)ctx.getBean("userService");
    	
    	List<Integer> agents = new ArrayList<Integer>();
		
		ModelCriteria mc_user = new ModelCriteria();
		mc_user.createCriteria().andColumnEqualTo("utype", UserType.Agent.getIndex()).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc_user.setPageNumber(1);
		mc_user.setPageSize(100);
		EntityIterator<Integer, User> it_user = new KeyBasedEntityBatchIterator<Integer,User>(Integer.class,User.class, userService.getEntityDao(), mc_user);
		while(it_user.hasNext()){
			List<Integer> uids = it_user.nextKeys();
			agents.addAll(uids);
		}
		if(agents.isEmpty()){
			System.out.println("没有发现代理商用户");
		}
	   	Date current = new Date();
    	//创建2年的数据
    	for(int ago=0;ago<=24;ago++){
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
}
