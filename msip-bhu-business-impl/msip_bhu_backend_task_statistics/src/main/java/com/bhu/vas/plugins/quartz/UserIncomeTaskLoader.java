package com.bhu.vas.plugins.quartz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.CommonTimeHelper;
import com.bhu.vas.api.rpc.charging.model.GpathIncome;
import com.bhu.vas.api.rpc.charging.model.MacIncome;
import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
/**
 * 每天一点零四分运行
 * @author dell
 *
 */
public class UserIncomeTaskLoader {
	
	public static void main(String[] args) {
		System.out.println(CommonTimeHelper.GetDateStr(-1));
		System.out.println(CommonTimeHelper.GetDateStr(0));
	}
	
	private static Logger logger = LoggerFactory.getLogger(UserIncomeTaskLoader.class);
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	public void execute(){
		logger.info("UserIncomeTaskLoader start...");
		userIncom();
		logger.info("UserIncomeTaskLoader end...");
	}
	//统计前一日用户收益
	public void userIncom(){
		String preDayDate =CommonTimeHelper.GetDateStr(-1);
        String today = CommonTimeHelper.GetDateStr(0);
        System.out.println("preDate:"+preDayDate+"today:"+today);
        //统计昨日用户收益userIncome
        List<Object> userIncomeList = userWalletFacadeService.getUserWalletLogService().findUserIncomeListByTime(preDayDate,today);
        for (Object object : userIncomeList) {
        	UserIncome userIncomeVTO = (UserIncome) object;
        	System.out.println(userIncomeVTO.getUid());
    		userIncomeVTO.setTime(preDayDate);
    		userIncomeVTO.setUpdated_at(new Date());
			List<UserIncome> income=userWalletFacadeService.getUserIncomeService().findListByUid(userIncomeVTO.getUid(), preDayDate);
			if(income.size()<1){
				userWalletFacadeService.getUserIncomeService().insert(userIncomeVTO);
			}
		}
        //统计昨日用户收益macIncome
        List<Object> macIncomeList = userWalletFacadeService.getUserWalletLogService().findMacIncomeListByTime(preDayDate,today);
        for (Object object : macIncomeList) {
        	MacIncome macIncomeVTO = (MacIncome) object;
        	macIncomeVTO.setTime(preDayDate);
        	macIncomeVTO.setUpdated_at(new Date());
        	List<MacIncome> macIncomes=userWalletFacadeService.getMacIncomeService().findListByMac(macIncomeVTO.getMac(), preDayDate);
        	if(macIncomes.size()<1){
				userWalletFacadeService.getMacIncomeService().insert(macIncomeVTO);
			}
		}
        
        //统计昨日用户收益gpathIncome
        List<Object> gpathIncomeList = userWalletFacadeService.getUserWalletLogService().findGpathIncomeListByTime(preDayDate,today);
        for (Object object : gpathIncomeList) {
        	GpathIncome gpathIncomeVTO = (GpathIncome) object;
        	System.out.println(gpathIncomeVTO.getGpath());
        	gpathIncomeVTO.setTime(preDayDate);
        	gpathIncomeVTO.setUpdated_at(new Date());
        	List<GpathIncome> gpathIncomes=userWalletFacadeService.getGpathIncomeService().findListByGpath(gpathIncomeVTO.getGpath(), preDayDate);
        	if(gpathIncomes.size()<1){
        		userWalletFacadeService.getGpathIncomeService().insert(gpathIncomeVTO);
			}
		}
    	/*List<UserWalletLog> userWalletLogs= userWalletFacadeService.getUserWalletLogService().findListByTime(time);
    	for(UserWalletLog i:userWalletLogs){
			UserIncome userIncome=new UserIncome();
			userIncome.setTime(time);
			userIncome.setUid(i.getUid());
			userIncome.setIncome(i.getCash().substring(1));
			userIncome.setTimes(1);
			List<UserIncome> income=userWalletFacadeService.getUserIncomeService().findListByUid(i.getUid(), time);
			if(income==null||income.size()<1){
				userWalletFacadeService.getUserIncomeService().insert(userIncome);
			}else{
				int num=income.get(0).getTimes();
				num++;
				userIncome.setTimes(num);
				//ArithHelper.round(v, scale);
				userIncome.setIncome(String.valueOf(round(Double.valueOf(income.get(0).getIncome())+Double.valueOf(userIncome.getIncome()),4)));
				userIncome.setId(income.get(0).getId());
				userWalletFacadeService.getUserIncomeService().update(userIncome);
			}
			if(StringUtils.isNoneBlank(i.getMac())&&!StringUtils.equals("-", i.getMac())){
				MacIncome macIncome=new MacIncome();
				macIncome.setTime(time);
				macIncome.setMac(i.getMac());
				macIncome.setUid(i.getUid());
				macIncome.setTimes(1);
				macIncome.setIncome(i.getCash().substring(1));
				List<MacIncome> macIncomes=userWalletFacadeService.getMacIncomeService().findListByMac(i.getMac(), time);
				if(macIncomes==null||macIncomes.size()<1){
					userWalletFacadeService.getMacIncomeService().insert(macIncome);
				}else{
					int num=macIncomes.get(0).getTimes();
 					num++;
 					macIncome.setTimes(num);
					macIncome.setId(macIncomes.get(0).getId());
					macIncome.setIncome(String.valueOf(round(Double.valueOf(macIncomes.get(0).getIncome())+Double.valueOf(macIncome.getIncome()),2)));
					userWalletFacadeService.getMacIncomeService().update(macIncome);
				}
			}
			if(StringUtils.isNoneBlank(i.getCurrent_gpath())&&!StringUtils.equals("-", i.getCurrent_gpath())){
				GpathIncome gpathIncome=new GpathIncome();
				gpathIncome.setGpath(i.getCurrent_gpath());
				gpathIncome.setTime(time);
				gpathIncome.setIncome(i.getCash().substring(1));
				gpathIncome.setTimes(1);
				List<GpathIncome> gpathIncomes=userWalletFacadeService.getGpathIncomeService().findListByGpath(i.getCurrent_gpath(), time);
				if(gpathIncomes==null||gpathIncomes.size()<1){
					userWalletFacadeService.getGpathIncomeService().insert(gpathIncome);
				}else{
					int num=gpathIncomes.get(0).getTimes();
 					num++;
 					gpathIncome.setTimes(num);
					gpathIncome.setId(gpathIncomes.get(0).getId());
					gpathIncome.setIncome(String.valueOf(round(Double.valueOf(gpathIncomes.get(0).getIncome())+Double.valueOf(gpathIncome.getIncome()),2)));
					userWalletFacadeService.getGpathIncomeService().update(gpathIncome);
				}
			}
    	}*/
	}
	
	/**      
	    * 提供精确的小数位四舍五入处理。      
	     * @param v 需要四舍五入的数字      
	     * @param scale 小数点后保留几位      
	     * @return 四舍五入后的结果      
	    */         
	public static double round(double v,int scale){         
		if(scale<0){         
	           throw new IllegalArgumentException("The scale must be a positive integer or zero");         
	    }         
	    BigDecimal b = new BigDecimal(Double.toString(v));         
	    BigDecimal one = new BigDecimal("1");         
	    return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();         
	}      
}
