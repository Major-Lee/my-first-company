package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.agent.facade.AgentBillFacadeService;
import com.bhu.vas.business.ds.agent.helper.AgentHelper;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeMonthMDTO;
import com.bhu.vas.business.ds.agent.mservice.AgentWholeMonthMService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 对指定月进行结算
 * 每月1号进行上月数据结算，并生成代理商结算记录到AgentSettlementsRecordMDTO
 * 结算过程中 可能需要对已经结算过的月进行重新结算，所有对于记录需要判定是否存在，存在的话，只更新需要结算的金额后再保存
 * 对于终端则无需这样
 * @author Edmond
 *
 */
public class MonthlyChargingSettlementGen4AgentOper {

	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		if(argv == null || argv.length ==0){
			System.out.println("参数不全 $dates(yyyy-MM,yyyy-MM)");
			return;
		}
		String[] dates = argv[0].split(",");
		//String chargingsimulogs = argv[1];//BHUData/bulogs/copylogs/%s/chargingsimulogs/
		//String charginglogs = argv[2];//BHUData/bulogs/copylogs/%s/charginglogs/
		
		System.out.println("多月结算----------ParamsStart------------");
		for(String date:dates){
			System.out.println("日期参数:"+date);
		}
		System.out.println("多月结算----------ParamsEnd------------");
		
		//String date = "2015-09-10";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		UserService userService = (UserService)ctx.getBean("userService");
		
		AgentWholeMonthMService agentWholeMonthMService = (AgentWholeMonthMService)ctx.getBean("agentWholeMonthMService");
		AgentBillFacadeService agentBillFacadeService = (AgentBillFacadeService)ctx.getBean("agentBillFacadeService");
		//AgentSettlementsRecordMService agentSettlementsRecordMService = (AgentSettlementsRecordMService)ctx.getBean("agentSettlementsRecordMService");
		List<Integer> agentUsers = new ArrayList<Integer>();
		
		ModelCriteria mc_user = new ModelCriteria();
		mc_user.createCriteria().andColumnEqualTo("utype", UserType.Agent.getIndex()).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc_user.setPageNumber(1);
		mc_user.setPageSize(100);
		EntityIterator<Integer, User> it_user = new KeyBasedEntityBatchIterator<Integer,User>(Integer.class,User.class, userService.getEntityDao(), mc_user);
		while(it_user.hasNext()){
			List<Integer> uids = it_user.nextKeys();
			agentUsers.addAll(uids);
		}
		if(agentUsers.isEmpty()){
			System.out.println("没有发现代理商用户");
		}
		for(String date:dates){
			for(Integer user:agentUsers){
				AgentWholeMonthMDTO wholeMonth = agentWholeMonthMService.getWholeMonth(date, user);
				if(wholeMonth != null){
					agentBillFacadeService.newBillCreated(user, date, AgentHelper.currency(wholeMonth.getDod()));
					/*AgentSettlementsRecordMDTO settlement = agentSettlementsRecordMService.getSettlement(date, user);
					if(settlement != null){//已经存在的单据，覆盖部分值
						settlement.setiSVPrice(AgentHelper.currency(wholeMonth.getDod()));
					}else{
						settlement = new AgentSettlementsRecordMDTO();
						settlement.setId(AgentSettlementsRecordMDTO.generateId(date, user));
						settlement.setAgent(user);
						settlement.setDate(date);
						settlement.setiSVPrice(ArithHelper.round(RandomData.floatNumber(800,2000), 2));
						settlement.setStatus(AgentSettlementsRecordMDTO.Settlement_Bill_Created);
					}
					settlement.setCreated_at(DateTimeHelper.formatDate(new Date(),DateTimeHelper.FormatPattern1));
					agentSettlementsRecordMService.save(settlement);*/
				}
				//生成summaryView
				agentBillFacadeService.billSummaryViewGen(user);
			}
			System.out.println(date+" 结算成功");
		}
	}
}
