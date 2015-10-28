package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * 每月1号进行上月数据结算，并生成代理商结算记录到AgentSettlementsRecordMDTO
 * 对于终端则无需这样
 * @author Edmond
 *
 */
public class MonthlyChargingSettlementGen4AgentOper {

	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		if(argv == null || argv.length < 3){
			System.out.println("参数不全 $dates(yyyy-MM-dd) $simulatelogpath $charginglogpath ");
			return;
		}
		String[] dates = argv[0].split(",");
		String chargingsimulogs = argv[1];//BHUData/bulogs/copylogs/%s/chargingsimulogs/
		String charginglogs = argv[2];//BHUData/bulogs/copylogs/%s/charginglogs/
		
		System.out.println("----------ParamsStart------------");
		for(String date:dates){
			System.out.println("日期参数:"+date);
		}
		System.out.println("模拟登录日志参数:"+chargingsimulogs);
		System.out.println("登录登出日志参数:"+charginglogs);
		System.out.println("----------ParamsEnd------------");
		
		//String date = "2015-09-10";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
	}
}
