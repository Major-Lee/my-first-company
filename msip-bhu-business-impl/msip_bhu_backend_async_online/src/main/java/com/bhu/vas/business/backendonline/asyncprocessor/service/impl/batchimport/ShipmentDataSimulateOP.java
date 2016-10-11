package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.backendonline.BackendAsyncOnlineMain;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback.ImportElementCallback;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.dto.DeviceCallbackDTO;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.PageHelper;


/**
 * 出库设备清单数据导入程序
 * @author Edmond
 *
 */
public class ShipmentDataSimulateOP {
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
		String[] CONFIG = {"/spring/appCtxBackend.xml"};
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG, BackendAsyncOnlineMain.class);
		context.start();
		//ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		//UserService userService = (UserService)ctx.getBean("userService");
		
		//final BatchImportConfirmServiceHandler batchImportConfirmServiceHandler = (BatchImportConfirmServiceHandler)context.getBean("batchImportConfirmServiceHandler");
		final WifiDeviceService wifiDeviceService = (WifiDeviceService)context.getBean("wifiDeviceService");
		//final UserDeviceFacadeService userDeviceFacadeService = (UserDeviceFacadeService)context.getBean("userDeviceFacadeService");
		final UserWifiDeviceService userWifiDeviceService = (UserWifiDeviceService)context.getBean("userWifiDeviceService");
		//final ChargingFacadeService chargingFacadeService = (ChargingFacadeService)context.getBean("chargingFacadeService");
		//final BackendBusinessService backendBusinessService = (BackendBusinessService)context.getBean("backendBusinessService");;
		final AtomicInteger atomic_failed = new AtomicInteger(0);
		final AtomicInteger atomic_successed = new AtomicInteger(0);
		@SuppressWarnings("unused")
		final String batchno = "20160523-00000008";
		final Integer uid_willbinded = 3;
		ShipmentExcelImport.excelImport("/Users/Edmond/gospace/20160523-00000008.xlsx","/Users/Edmond/gospace/20160523-00000008-out.xlsx", new ImportElementCallback(){
			@Override
			public DeviceCallbackDTO elementDeviceInfoFetch(String sn) {
				ModelCriteria mc = new ModelCriteria();
		        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("sn", sn);
		        List<String> macs_fromdb = wifiDeviceService.findIdsByModelCriteria(mc);//.findModelByModelCriteria(mc);
		        if(macs_fromdb.isEmpty()){
		        	atomic_failed.incrementAndGet();
		        	return null;
		        }
		        else{
		        	String dmac = macs_fromdb.get(0);
		        	//TODO:增加索引更新 是否可关闭
		        	DeviceCallbackDTO result = new DeviceCallbackDTO();
		        	result.setMac(dmac);
		        	//result.setOrig_swver(wifiDevice.getOrig_swver());
		        	atomic_successed.incrementAndGet();
		        	return result;
		        	//return models.get(0);
		        }
			}

			@Override
			public void afterExcelImported(Set<String> dmacs, Set<String> failed_sns) {
				if(dmacs.isEmpty()) return;
				List<String> all_dmacs = new ArrayList<String>(dmacs);
				int total = all_dmacs.size();
				int totalPages = PageHelper.getTotalPages(total, 100);
				System.out.println(String.format("total:%s totalPages:%s all_dmacs:%s",total, totalPages,all_dmacs));
				for(int pageno= 1;pageno<=totalPages;pageno++){
					List<String> pages = PageHelper.pageList(all_dmacs, pageno, 100);
					System.out.println(String.format("pageno:%s pagesize:%s pages:%s", pageno,100,pages));
					if(uid_willbinded != null && uid_willbinded.intValue() >0){
						//userDeviceFacadeService.doForceBindDevices(uid_willbinded.intValue(),pages);
						for(@SuppressWarnings("unused") String dmac:pages){
							/*chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,uid_willbinded, dmac, 
									importVto.isCanbeturnoff(),importVto.isEnterpriselevel(),
									importVto.isCustomized(),
									importVto.getOwner_percent(), 
				        			importVto.getRcm(), importVto.getRcp(), importVto.getAit(), false);
				        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);*/
						}
						System.out.println(String.format("A uid_willbinded:%s doForceBindDevices:%s", uid_willbinded,pages));
					}else{
						//如果设备从未上线则强制解绑，如果设备上线过则不动作
						List<String> forceUnbindedDevices = new ArrayList<>();
						List<String> noActionDevices = new ArrayList<>();
						List<WifiDevice> wifiDevices = wifiDeviceService.findByIds(pages);
						try{
							for(WifiDevice device:wifiDevices){
								if(device.getLast_logout_at() == null 
										&& device.getLast_reged_at() == null
										&& device.getOem_swver().contains("P06V0.0.0Build0000")){//从未上线
									forceUnbindedDevices.add(device.getId());
								}else{
									noActionDevices.add(device.getId());
								}
							}
							if(!forceUnbindedDevices.isEmpty())
								//userDeviceFacadeService.doForceUnbindDevice(forceUnbindedDevices);
								userWifiDeviceService.deleteByIds(forceUnbindedDevices);
							//变更分成比例
							for(@SuppressWarnings("unused") String dmac:forceUnbindedDevices){//需要变更owner = -1
								/*chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,-1, dmac, 
										importVto.isCanbeturnoff(),importVto.isEnterpriselevel(),
										importVto.isCustomized(),
										importVto.getOwner_percent(), 
					        			importVto.getRcm(), importVto.getRcp(), importVto.getAit(), false);
					        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);*/
							}
							for(@SuppressWarnings("unused") String dmac:noActionDevices){//不需要变更owner 
								/*chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,null, dmac,
										importVto.isCanbeturnoff(),importVto.isEnterpriselevel(),
										importVto.isCustomized(),
										importVto.getOwner_percent(), 
					        			importVto.getRcm(), importVto.getRcp(), importVto.getAit(), false);
					        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);*/
							}
							System.out.println(String.format("B uid_willbinded:%s forceUnbindedDevices:%s", uid_willbinded,forceUnbindedDevices));
							System.out.println(String.format("B uid_willbinded:%s noActionDevices:%s", uid_willbinded,noActionDevices));
						}catch(Exception ex){
							ex.printStackTrace(System.out);
						}finally{
							if(forceUnbindedDevices != null){
								forceUnbindedDevices.clear();
								forceUnbindedDevices = null;
							}
							if(noActionDevices != null){
								noActionDevices.clear();
								noActionDevices = null;
							}
							if(wifiDevices != null){
								wifiDevices.clear();
								wifiDevices = null;
							}
						}
						
					}
					
					/*try {
						backendBusinessService.blukIndexs(pages);
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace(System.out);
					} catch (Exception e) {
						e.printStackTrace(System.out);
					} finally{
						if(pages != null){
							pages.clear();
							pages = null;
						}
					}*/
				}
			}
		});
		context.close();
		
		System.out.println(String.format("filepath[%s] import successfully! successed[%s] failed[%s]", "/Users/Edmond/gospace/20160523-00000008.xlsx",atomic_successed.get(),atomic_failed.get()));
	}
}
