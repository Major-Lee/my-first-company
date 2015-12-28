package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.di.business.datainit.charging.Step05AgentWholeDayRecordService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 针对批次号进行设备标记，标记为设备无须进行第一次返现
 * 对于导入的设备需要在进行计费返现程序执行之前进行设备的标记
 * @author Edmond
 *
 */
public class ChargingAgentDeviceNoFirstCashbackOper {

	/**
	 * 
	 * @param argv batchno
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{

		if(argv == null || argv.length !=2){
			System.out.println("参数不全 $batchno");
			return;
		}
		System.out.println("----------ParamsStart------------");
		Integer batchno = Integer.parseInt(argv[0]);
		System.out.println("----------ParamsEnd------------");
		System.out.println("批次号参数:"+argv[1]);

		//String date = "2015-09-10";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		AgentDeviceClaimService agentDeviceClaimService = (AgentDeviceClaimService)ctx.getBean("agentDeviceClaimService");
		ModelCriteria mc_devices = new ModelCriteria();
		mc_devices.createCriteria().andColumnEqualTo("agentuser", batchno).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc_devices.setPageNumber(1);
		mc_devices.setPageSize(100);
		EntityIterator<String, WifiDevice> it_devices = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class,WifiDevice.class, wifiDeviceService.getEntityDao(), mc_devices);
		while(it_devices.hasNext()){
			List<WifiDevice> next = it_devices.next();
			for(WifiDevice device:next){
				{
					AgentDeviceClaim claim = agentDeviceClaimService.getById(device.getSn());
					if(claim.getClaim_at() == null){
						claim.setHdtype(claim.getHdtype());
						claim.setMac(device.getId());
						claim.setImport_status(1);
						claim.setStatus(1);
						claim.setClaim_at(new Date());
						agentDeviceClaimService.update(claim);
					}
				}
			}
		}
	}
}	
