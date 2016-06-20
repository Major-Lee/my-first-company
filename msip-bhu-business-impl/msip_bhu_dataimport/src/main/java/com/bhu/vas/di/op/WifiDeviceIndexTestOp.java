package com.bhu.vas.di.op;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.domain.Page;

import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
/**
 * 全量创建wifiDevice的索引数据
 * @author lawliet
 *
 */
public class WifiDeviceIndexTestOp {

	
	//public static WifiDeviceIndexService wifiDeviceIndexService = null;
	private static WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	public static void main(String[] argv) throws IOException, ParseException{
		
		try{
			
			ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
	
			//wifiDeviceIndexService = (WifiDeviceIndexService)ctx.getBean("wifiDeviceIndexService");
			String message = "{\"search_t\":1,\"search_cs\":[{\"cs\":[{\"key\":\"t_uc_extension\",\"pattern\":\"seq\",\"payload\":\"g_120025\"}]}]}";
			wifiDeviceDataSearchService = (WifiDeviceDataSearchService)ctx.getBean("wifiDeviceDataSearchService");
			
			wifiDeviceDataSearchService.iteratorAll(message,100,
					new IteratorNotify<Page<WifiDeviceDocument>>() {
						@Override
						public void notifyComming(Page<WifiDeviceDocument> pages) {
							System.out.println("------>"+pages.getTotalElements());
						}
			});
			
			//System.out.println("数据全量导入，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");

		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{

		}
		System.exit(1);
	}

}
