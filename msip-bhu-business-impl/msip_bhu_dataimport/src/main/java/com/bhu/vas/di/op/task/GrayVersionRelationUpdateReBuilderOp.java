package com.bhu.vas.di.op.task;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.business.ds.device.facade.WifiDeviceGrayFacadeService;
import com.bhu.vas.di.op.migrate.UserSharedNetworksRepairEnvOp;
/**
 * 全量创建wifiDevice的索引数据
 * @author lawliet
 *
 */
public class GrayVersionRelationUpdateReBuilderOp {

	public static void main(String[] argv) throws IOException, ParseException{
		try{
			String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
			final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworksRepairEnvOp.class);
			ctx.start();
			//ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
			WifiDeviceGrayFacadeService wifiDeviceGrayFacadeService = (WifiDeviceGrayFacadeService)ctx.getBean("wifiDeviceGrayFacadeService");
			long t0 = System.currentTimeMillis();
			wifiDeviceGrayFacadeService.updateRelatedFieldAction();
			wifiDeviceGrayFacadeService.updateRelatedDevice4GrayVersion();
			System.out.println("数据全量更新，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");
			ctx.stop();
			ctx.close();
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{

		}
		System.exit(1);
	}
}
