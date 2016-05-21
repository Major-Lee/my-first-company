package com.bhu.vas.di.op.task.sharedeal;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.tag.model.TagGroup;
import com.bhu.vas.api.rpc.user.dto.ShareDealDailyGroupSummaryProcedureVTO;
import com.bhu.vas.business.ds.charging.facade.ChargingStatisticsFacadeService;
import com.bhu.vas.business.ds.tag.service.TagGroupService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 全量创建wifiDevice的索引数据
 * @author lawliet
 *
 */
public class SharedealDailyGroupSummaryBuilderOp {

	public static void main(String[] argv) throws IOException, ParseException{
		argv =new String[]{"2016-05-21"};
		if(argv == null || argv.length <1){
			System.out.println("参数不全 $dates");
			return;
		}
		System.out.println("----------ParamsStart------------");
		//String user = argv[0];
		String[] dates = argv[0].split(StringHelper.COMMA_STRING_GAP);
		System.out.println("----------ParamsEnd------------");
		//System.out.println("User参数:"+user);
		System.out.println("修复日期参数:"+argv[0]);
		ClassPathXmlApplicationContext context = null;
		try{
			String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
			context = new ClassPathXmlApplicationContext(CONFIG, SharedealDailyGroupSummaryBuilderOp.class);
			context.start();
			//ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
			UserWalletFacadeService userWalletFacadeService = context.getBean("userWalletFacadeService",UserWalletFacadeService.class);
			TagGroupService tagGroupService = context.getBean("tagGroupService",TagGroupService.class);
			ChargingStatisticsFacadeService chargingStatisticsFacadeService = context.getBean("chargingStatisticsFacadeService",ChargingStatisticsFacadeService.class);
			//ChargingStatisticsFacadeService.deviceGroupPaymentTotalWithProcedure
			long t0 = System.currentTimeMillis();
			Set<Integer> users = new HashSet<Integer>();
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andSimpleCaulse(" 1=1 ");
	    	mc.setPageNumber(1);
	    	mc.setPageSize(500);
			EntityIterator<Integer, TagGroup> it_taggroup = new KeyBasedEntityBatchIterator<Integer,TagGroup>(Integer.class,TagGroup.class, tagGroupService.getEntityDao(), mc);
			while(it_taggroup.hasNext()){
				List<TagGroup> next = it_taggroup.next();
				for(TagGroup group:next){
					int creator = group.getCreator();
					if(creator <= 0) continue;
					users.add(new Integer(creator));
					String gpath = group.getPath();
					if(StringUtils.isEmpty(gpath)) continue;
					for(String cdate:dates){
						ShareDealDailyGroupSummaryProcedureVTO groupDailySummary = userWalletFacadeService.sharedealDailyGroupSummaryWithProcedure(creator, gpath, cdate);
						System.out.println( String.format("daily[%s] gid[%s] creator[%s] gpath[%s] totalcash[%s] totalnums[%s]",
								groupDailySummary.getCdate(),group.getId(),groupDailySummary.getUserid(),groupDailySummary.getGpath(),
								groupDailySummary.getTotal_cash(),groupDailySummary.getTotal_nums()));
						chargingStatisticsFacadeService.gainDeviceGroupPaymentStatistics(group.getCreator(), String.valueOf(group.getId()), groupDailySummary.getCdate(), 
								String.valueOf(groupDailySummary.getTotal_cash()), groupDailySummary.getTotal_nums());
						Thread.sleep(100);
					}
					chargingStatisticsFacadeService.deviceGroupPaymentTotalWithProcedure(group.getCreator(), String.valueOf(group.getId()));
					//Total
					//ShareDealDailyGroupSummaryProcedureVTO groupTotalSummary = userWalletFacadeService.sharedealDailyGroupSummaryWithProcedure(creator, gpath, null);
					//System.out.println( String.format("total gid[%s] creator[%s] gpath[%s]",group.getId(),groupTotalSummary.getUserid(),groupTotalSummary.getGpath()));//"daily:"+JsonHelper.getJSONString(groupDailySummary));
				}
			}
			System.out.println("数据分组收益更新成功，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");
			
			System.out.println("数据默认分组收益开始计算，共计："+users.size()+"个用户");
			long t1 = System.currentTimeMillis();
			for(Integer user:users){
				for(String cdate:dates){
					ShareDealDailyGroupSummaryProcedureVTO groupDailySummary = userWalletFacadeService.sharedealDailyGroupSummaryWithProcedure(user.intValue(), null, cdate);
					System.out.println( String.format("daily[%s] gid[%s] creator[%s] gpath[%s] totalcash[%s] totalnums[%s]",
							groupDailySummary.getCdate(),"默认群组",groupDailySummary.getUserid(),StringHelper.MINUS_STRING_GAP,
							groupDailySummary.getTotal_cash(),groupDailySummary.getTotal_nums()));
					chargingStatisticsFacadeService.gainDeviceGroupPaymentStatistics(user.intValue(), null, groupDailySummary.getCdate(), 
							String.valueOf(groupDailySummary.getTotal_cash()), groupDailySummary.getTotal_nums());
					Thread.sleep(100);
				}
				
			}
			
			System.out.println("数据默认分组收益更新成功，总耗时"+((System.currentTimeMillis()-t1)/1000)+"s");
			

		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{
			if(context != null){
				context.close();
				context = null;
			}
		}
		System.exit(1);
	}
}
