package com.bhu.vas.di.op.batchimport.frommanufacturing;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.di.op.batchimport.frommanufacturing.notify.ExcelElementNotify;


/**
 * 出厂设备清单数据导入程序
 * @author Edmond
 *
 */
public class ManufacturerDeviceDataImportOP {
	///Users/Edmond/gospace
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		if(argv.length < 2) {
			System.err.println(String.format("pls input xlsx filepath[%s] and origswver[%s]", "/BHUData/batchimport/manufacturer/uRouter-20160426-双翼.xlsx","AP106P06V1.5.6Build9011_TU"));
			return;
		}
		final String filepath = argv[0];
		final String orig_swver = argv[1];
		//final String filepath = "/BHUData/batchimport/manufacturer/uRouter-20160426-双翼.xlsx";
		//final String orig_swver = "AP106P06V1.5.6Build9011_TU";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		//UserService userService = (UserService)ctx.getBean("userService");
		final WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		final AtomicInteger atomic_failed = new AtomicInteger(0);
		final AtomicInteger atomic_successed = new AtomicInteger(0);
		ManufacturerExcelImport.excelImport(filepath, new ExcelElementNotify(){
			@Override
			public void elementNotify(String mac, String sn) {
				//System.out.println(String.format("mac[%s] sn[%s]", mac,sn));
				if(!mac.startsWith("84:82:f4"))	return;
				DeviceUnitType dut = VapEnumType.DeviceUnitType.fromDeviceSN(sn);
				if(dut != null){
					WifiDevice wifiDevice = wifiDeviceService.getById(mac);
					if(wifiDevice != null) {
						System.err.println(String.format("mac[%s] sn[%s] dbsn[%s] already existed! ", mac,sn,wifiDevice.getSn()));
						atomic_failed.incrementAndGet();
						return;
					}
					wifiDevice = new WifiDevice();
					wifiDevice.setId(mac);
					wifiDevice.setHdtype(DeviceUnitType.parserIndex(dut.getIndex())[1]);
					wifiDevice.setOrig_vendor("BHU");
					wifiDevice.setOrig_model(dut.getName());
					wifiDevice.setOrig_hdver("Z01");
					wifiDevice.setOrig_swver(orig_swver);
					wifiDevice.setOem_vendor("BHU");
					wifiDevice.setOem_model(dut.getName());
					wifiDevice.setOem_hdver("Z01");
					wifiDevice.setOem_swver(orig_swver);
					wifiDevice.setConfig_sequence("0");
					wifiDevice.setOnline(false);
					wifiDevice.setLast_reged_at(null);
					wifiDevice.setLast_logout_at(null);
					wifiDeviceService.insert(wifiDevice);
					atomic_successed.incrementAndGet();
					System.out.println(String.format("mac[%s] sn[%s] insert successfully! ", mac,sn));
				}
			}
		});
		
		System.out.println(String.format("filepath[%s] import successfully! successed[%s] failed[%s]", filepath,atomic_successed.get(),atomic_failed.get()));
	}
}
