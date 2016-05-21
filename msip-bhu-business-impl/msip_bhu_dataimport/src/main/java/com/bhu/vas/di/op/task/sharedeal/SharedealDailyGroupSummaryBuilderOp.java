package com.bhu.vas.di.op.task.sharedeal;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.tag.model.TagGroup;
import com.bhu.vas.api.rpc.user.dto.ShareDealDailyGroupSummaryProcedureVTO;
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
		if(argv == null || argv.length <1){
			System.out.println("参数不全 $dates");
			return;
		}
		System.out.println("----------ParamsStart------------");
		//String user = argv[0];
		String[] dates = argv[1].split(StringHelper.COMMA_STRING_GAP);
		System.out.println("----------ParamsEnd------------");
		//System.out.println("User参数:"+user);
		System.out.println("修复日期参数:"+argv[1]);
		try{
			String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
			final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG, SharedealDailyGroupSummaryBuilderOp.class);
			context.start();
			//ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
			long t0 = System.currentTimeMillis();
			UserWalletFacadeService userWalletFacadeService = context.getBean("userWalletFacadeService",UserWalletFacadeService.class);
			TagGroupService tagGroupService = context.getBean("tagGroupService",TagGroupService.class);
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andSimpleCaulse(" 1=1 ");
	    	mc.setPageNumber(1);
	    	mc.setPageSize(500);
			EntityIterator<Integer, TagGroup> it_taggroup = new KeyBasedEntityBatchIterator<Integer,TagGroup>(Integer.class,TagGroup.class, tagGroupService.getEntityDao(), mc);
			while(it_taggroup.hasNext()){
				List<TagGroup> next = it_taggroup.next();
				for(TagGroup group:next){
					int creator = group.getCreator();
					if(creator == 0) continue;
					String gpath = group.getPath();
					if(StringUtils.isEmpty(gpath)) continue;
					for(String cdate:dates){
						ShareDealDailyGroupSummaryProcedureVTO groupDailySummary = userWalletFacadeService.sharedealDailyGroupSummaryWithProcedure(creator, gpath, cdate);
						System.out.println( String.format("daily[%s] gid[%s] creator[%s] gpath[%s] totalcash[%s] totalnums[%s]",
								groupDailySummary.getCdate(),group.getId(),groupDailySummary.getUserid(),groupDailySummary.getGpath(),
								groupDailySummary.getTotal_cash(),groupDailySummary.getTotal_nums()));
					}
					//Total
					//ShareDealDailyGroupSummaryProcedureVTO groupTotalSummary = userWalletFacadeService.sharedealDailyGroupSummaryWithProcedure(creator, gpath, null);
					//System.out.println( String.format("total gid[%s] creator[%s] gpath[%s]",group.getId(),groupTotalSummary.getUserid(),groupTotalSummary.getGpath()));//"daily:"+JsonHelper.getJSONString(groupDailySummary));
				}
			}
			
			System.out.println("数据全量更新，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");

		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{

		}
		System.exit(1);
	}
}
