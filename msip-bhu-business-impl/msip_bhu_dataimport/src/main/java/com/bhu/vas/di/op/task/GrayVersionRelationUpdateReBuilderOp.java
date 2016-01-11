package com.bhu.vas.di.op.task;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.ds.device.facade.WifiDeviceGrayFacadeService;
/**
 * 全量创建wifiDevice的索引数据
 * @author lawliet
 *
 */
public class GrayVersionRelationUpdateReBuilderOp {

	public static void main(String[] argv) throws IOException, ParseException{
		try{
			ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
			WifiDeviceGrayFacadeService wifiDeviceGrayFacadeService = (WifiDeviceGrayFacadeService)ctx.getBean("wifiDeviceGrayFacadeService");
			long t0 = System.currentTimeMillis();
			wifiDeviceGrayFacadeService.updateRelatedFieldAction();
			wifiDeviceGrayFacadeService.updateRelatedDevice4GrayVersion();
			System.out.println("数据全量更新，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");

		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{

		}
		System.exit(1);
	}
}
