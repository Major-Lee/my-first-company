package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 把mac地址替换为sn，
 * @author Edmond
 * update t_agents_devices_marks aaaa set aaaa.id = (select dddd.sn from t_wifi_devices dddd where dddd.id =  aaaa.id)
 */
public class ChargingAgentFirstCashBackMarkRepairOper {

	/**
	 * 
	 * @param argv agentuid,datetime(,都好分割)
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		/*ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		AgentDeviceMarkService agentDeviceMarkService = (AgentDeviceMarkService)ctx.getBean("agentDeviceMarkService");
		//AgentDeviceClaimService agentDeviceClaimService = (AgentDeviceClaimService)ctx.getBean("agentDeviceClaimService");
		//Step05AgentWholeDayRecordService step05AgentWholeDayRecordService = (Step05AgentWholeDayRecordService)ctx.getBean("step05AgentWholeDayRecordService");
		ModelCriteria mc_marks = new ModelCriteria();
		mc_marks.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc_marks.setPageNumber(1);
		mc_marks.setPageSize(100);
		EntityIterator<String, AgentDeviceMark> it_marks = new KeyBasedEntityBatchIterator<String,AgentDeviceMark>(String.class,AgentDeviceMark.class, agentDeviceMarkService.getEntityDao(), mc_marks);
		while(it_marks.hasNext()){
			List<AgentDeviceMark> next = it_marks.next();
			for(AgentDeviceMark dmark:next){
				{
					WifiDevice device = wifiDeviceService.getById(dmark.getId());
					dmark.setId(device.getSn());
					dmark.setNeed_afcb(true);
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
		}*/
	}
}	
