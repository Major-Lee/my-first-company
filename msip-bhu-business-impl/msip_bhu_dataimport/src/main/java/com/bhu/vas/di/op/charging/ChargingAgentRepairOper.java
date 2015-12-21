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
 * 每日必须进行状态保活，每天凌晨需要把在线的设备重新写入到日志中，作为模拟登录，模拟登录的时间缺省为当日的起始时间，防止漏算了长时间在线的设备
 * 对于终端则无需这样
 * ./startupbuilder_agentlog_import.sh 2015-11-05,2015-11-06,2015-11-07,2015-11-08 /BHUData/bulogs/copylogs/%s/chargingsimulogs/ /BHUData/bulogs/copylogs/%s/charginglogs/
 * @author Edmond
 *
 */
public class ChargingAgentRepairOper {

	/**
	 * 
	 * @param argv agentuid,datetime(,都好分割)
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		/*String[] argv = {"2015-10-17,2015-10-18,2015-10-19,2015-10-20,2015-10-21,2015-10-22,2015-10-23,2015-10-24,2015-10-25,2015-10-26,2015-10-27","/BHUData/bulogs/copylogs/%s/chargingsimulogs/","/BHUData/bulogs/copylogs/%s/charginglogs/"};
		*/
		if(argv == null || argv.length !=2){
			System.out.println("参数不全 $agentuser $dates");
			return;
		}
		System.out.println("----------ParamsStart------------");
		Integer agentUser = Integer.parseInt(argv[0]);
		String[] dates = argv[1].split(",");
		System.out.println("----------ParamsEnd------------");
		System.out.println("AgentUser参数:"+agentUser);
		System.out.println("修复日期参数:"+argv[1]);

		//String date = "2015-09-10";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		AgentDeviceClaimService agentDeviceClaimService = (AgentDeviceClaimService)ctx.getBean("agentDeviceClaimService");
		Step05AgentWholeDayRecordService step05AgentWholeDayRecordService = (Step05AgentWholeDayRecordService)ctx.getBean("step05AgentWholeDayRecordService");
		ModelCriteria mc_devices = new ModelCriteria();
		mc_devices.createCriteria().andColumnEqualTo("agentuser", agentUser).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
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
		List<Integer> users = new ArrayList<Integer>();
		users.add(agentUser);
		for(String date:dates){
			step05AgentWholeDayRecordService.doAgentWholeDay(date, agentUser);
			step05AgentWholeDayRecordService.doAgentWholeCurrentMonth(date, users);
		}
		
		//Step00ParserLogService step00ParserLogService = (Step00ParserLogService)ctx.getBean("step00ParserLogService");
		{//修复claim表中的数据
			
		}
	}
}	
