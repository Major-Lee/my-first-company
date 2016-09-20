package com.bhu.vas.di.op.batchimport.shipmentlist;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.di.op.batchimport.shipmentlist.callback.ExcelElementCallback;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;


/**
 * 出库设备清单数据导入程序
 * @author Edmond
 *
 */
public class ShipmentDataImportOP {
	///Users/Edmond/gospace
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		/*if(argv.length < 2) {
			System.err.println(String.format("pls input xlsx filepath[%s] and origswver[%s]", "/BHUData/batchimport/manufacturer/uRouter-20160426-双翼.xlsx","AP106P06V1.5.6Build9011_TU"));
			return;
		}
		final String filepath = argv[0];
		final String orig_swver = argv[1];*/
		//final String filepath = "/BHUData/batchimport/manufacturer/uRouter-20160426-双翼.xlsx";
		//final String orig_swver = "AP106P06V1.5.6Build9011_TU";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		//UserService userService = (UserService)ctx.getBean("userService");
		final WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		final AtomicInteger atomic_failed = new AtomicInteger(0);
		final AtomicInteger atomic_successed = new AtomicInteger(0);
		String filepath = "/Users/Edmond/gospace/库房出库清单-20160426-0001.xlsx";
		ShipmentExcelImport.excelImport(filepath, new ExcelElementCallback(){
			@Override
			public WifiDevice elementCallback(String sn) {
				ModelCriteria mc = new ModelCriteria();
		        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("sn", sn);
		        List<WifiDevice> models = wifiDeviceService.findModelByModelCriteria(mc);
		        if(models.isEmpty()) return null;
		        else return models.get(0);
			}
		});
		
		System.out.println(String.format("filepath[%s] import successfully! successed[%s] failed[%s]", filepath,atomic_successed.get(),atomic_failed.get()));
	}
}
