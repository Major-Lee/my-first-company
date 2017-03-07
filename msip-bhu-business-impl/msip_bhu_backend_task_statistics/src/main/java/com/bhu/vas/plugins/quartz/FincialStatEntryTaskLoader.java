package com.bhu.vas.plugins.quartz;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.dto.procedure.FincialStatEntryProcedureDTO;
import com.bhu.vas.api.rpc.charging.model.StatisticFincialMonth;
import com.bhu.vas.api.rpc.charging.vto.StatisticFincialMonthVTO;
import com.bhu.vas.business.ds.statistics.service.StatisticFincialIncomeService;
import com.bhu.vas.business.ds.statistics.service.StatisticFincialMonthService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;


/**
 * 每天0点0分1秒运行
 * @author yetao
 *
 */
public class FincialStatEntryTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(FincialStatEntryTaskLoader.class);
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	@Resource 
	private StatisticFincialMonthService statisticFincialMonthService;

	@Resource 
	private StatisticFincialIncomeService statisticFincialIncomeService;

	
	private final static String EXCEL_TITLE = "时间,用户类型,用户昵称,用户电话,期初总收益,本期增加收益,本期交易笔数,期末总收益,往期提现金额,本期申请提现金额,钱包剩余";


	public void searchResultExportFile(String message, String export_filepath,
			String[] columns, List<String> lines) {
		logger.info(String.format("ConsoleServiceHandler searchResultExportFile message[%s]", message));
//		DeviceSearchResultExportFileDTO dto = JsonHelper.getDTO(message, DeviceSearchResultExportFileDTO.class);
//		if(dto == null) return;
		
//		String exportFilePath = BusinessRuntimeConfiguration.Search_Result_Export_Dir.concat(String.valueOf(dto.getUid()))
//				.concat(File.separator).concat(dto.getExportFileName());

		logger.info(String.format("ConsoleServiceHandler searchResultExportFile successful message[%s]", message));
	}
	
	private void geneateExcel(String monthid){
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andColumnEqualTo("monthid", monthid);
		createCriteria.andColumnNotEqualTo("uid", -1);
		mc.setPageNumber(1);
		mc.setPageSize(100);
//		mc.setOrderByClause(" updated_at desc ");
	
		int pageSize = 200;
		int totalCount = statisticFincialMonthService.countByCommonCriteria(mc);
		int totalPage = (totalCount + pageSize - 1) / pageSize;
		
		if(totalCount <= 0){
			logger.info("no data found for excel");
			return;
		}
		String filePath = String.format("%s/%s.csv", BusinessRuntimeConfiguration.Search_Result_Export_Dir, monthid);
		try{
			FileHelper.deleteFile(filePath);
		}catch(Exception e){
		}
		
		BufferedWriter fw = null;
		try {
			FileHelper.makeDirectory(filePath);
			File file = new File(filePath);
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(0xEF);
			fos.write(0xBB);
			fos.write(0xBF);
			fos.flush();
			
			fw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			fw.append(EXCEL_TITLE);
			fw.newLine();

			for(int i = 0; i < totalPage; i ++){
				List<StatisticFincialMonthVTO> list = statisticFincialMonthService.findVTOByMonthId(monthid, i + 1, pageSize);
				for(StatisticFincialMonthVTO vto:list){
					UserType utype = UserType.getByIndex(vto.getUtype());
					String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", monthid,
							utype.getFname(), vto.getNick(), vto.getMobileno(), vto.getBegin_income(), vto.getMonth_income(), vto.getMonth_count(),
							vto.getEnd_income(), vto.getWithdraw_past(), vto.getWithdraw_apply(), vto.getCash()
							);
					fw.append(line);
					fw.newLine();
				}
				fw.flush(); // 全部写入缓存中的内容
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("geneateExcel exception message[%s]", e.getMessage()));
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void execute() {
		logger.info("FincialStatEntryTaskLoader start....");
		Date yesterday = DateTimeHelper.getDateDaysAgo(1);
		String monthid = DateTimeHelper.formatDate(yesterday, DateTimeHelper.FormatPattern11);
		String todaystr = DateTimeHelper.formatDate(new Date(), DateTimeHelper.FormatPattern5); //yyyy-MM-dd
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andColumnEqualTo("monthid", monthid);
		createCriteria.andColumnEqualTo("uid", -1);
		createCriteria.andColumnGreaterThanOrEqualTo("updated_at", DateTimeHelper.parseDate(todaystr, DateTimeHelper.FormatPattern5)); //是否有今日生成的数据
		mc.setPageNumber(1);
		mc.setPageSize(100);
		mc.setOrderByClause(" updated_at desc ");

		List<StatisticFincialMonth> models = statisticFincialMonthService.findModelByCommonCriteria(mc);
		if(models.size() > 0){
			logger.info("stat data already exists, exit");
			return;
		}

		logger.info(String.format("call procedure"));
		
		FincialStatEntryProcedureDTO pdto = FincialStatEntryProcedureDTO.build();
		int ret = statisticFincialMonthService.executeProcedure(pdto);
		
		logger.info(String.format("procedure execute result:%s", ret));
		
		if(ret == 1){
			logger.info(String.format("generating excel", ret));
			geneateExcel(monthid);
		}
		
		logger.info("FincialStatEntryTaskLoader end....");
	}
	
}
