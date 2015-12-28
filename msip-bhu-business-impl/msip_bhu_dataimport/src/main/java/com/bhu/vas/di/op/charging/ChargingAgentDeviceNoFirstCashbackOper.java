package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceMark;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceMarkService;
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
		System.out.println("批次号参数:"+batchno);

		//String date = "2015-09-10";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		AgentDeviceMarkService agentDeviceMarkService = (AgentDeviceMarkService)ctx.getBean("agentDeviceMarkService");
		AgentDeviceClaimService agentDeviceClaimService = (AgentDeviceClaimService)ctx.getBean("agentDeviceClaimService");
		ModelCriteria mc_claim = new ModelCriteria();
		mc_claim.createCriteria().andColumnEqualTo("import_id", batchno).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc_claim.setPageNumber(1);
		mc_claim.setPageSize(100);
		EntityIterator<String, AgentDeviceClaim> it_claim = new KeyBasedEntityBatchIterator<String,AgentDeviceClaim>(String.class,AgentDeviceClaim.class, agentDeviceClaimService.getEntityDao(), mc_claim);
		while(it_claim.hasNext()){
			List<AgentDeviceClaim> next = it_claim.next();
			for(AgentDeviceClaim claim:next){
				AgentDeviceMark devicemark = agentDeviceMarkService.getById(claim.getId());
				if(devicemark == null){
					devicemark = new AgentDeviceMark();
					devicemark.setId(claim.getId());
					devicemark.setNeed_afcb(false);
					devicemark.setAfcb(false);
					devicemark.setAfcb_date(null);
		    		agentDeviceMarkService.insert(devicemark);
				}else{
					System.out.println(String.format("sn[%s] mark already existed!", claim.getId()));
				}
			}
		}
	}
}	
