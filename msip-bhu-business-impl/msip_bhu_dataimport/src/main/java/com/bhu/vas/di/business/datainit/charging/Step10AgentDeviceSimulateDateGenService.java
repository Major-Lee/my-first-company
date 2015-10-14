package com.bhu.vas.di.business.datainit.charging;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.agent.mdto.LineRecords;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;


@Service
public class Step10AgentDeviceSimulateDateGenService {
	@Resource
	private AgentDeviceClaimService agentDeviceClaimService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	public void deviceDataGen(String date,Map<String, LineRecords> lineDeviceRecordsMap){
		buildSimulateSN();
		AgentDeviceClaim agentDeviceClaim = null;
		Iterator<Entry<String, LineRecords>> iter = lineDeviceRecordsMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, LineRecords> next = iter.next();
			String key = next.getKey();
			agentDeviceClaim = new AgentDeviceClaim();
			String sn = RandomPicker.pick(sns);
			agentDeviceClaim.setId(sn);
			sns.remove(sn);
			agentDeviceClaim.setStock_code("10000190");
            agentDeviceClaim.setStock_name("uRouter");
            agentDeviceClaim.setMac(key);
            agentDeviceClaim.setSold_at(DateTimeHelper.getDateDaysAgo(RandomData.intNumber(180)));
            if(RandomData.flag())
            	agentDeviceClaim.setUid(100084);
            else
            	agentDeviceClaim.setUid(100083);
            agentDeviceClaimService.insert(agentDeviceClaim);
            
            WifiDevice device  = wifiDeviceService.getById(key);
            if(device == null){
            	device = new WifiDevice();
            	device.setId(key);
            	device.setSn(sn);
                device.setAgentuser(agentDeviceClaim.getUid());
                wifiDeviceService.insert(device);
            }
            
			System.out.println(sn);
		}
	}
	
	private final static String SN_Template = "BN%sBI%s%sAA";
	private Set<String> sns = new HashSet<>();
	private void buildSimulateSN(){
		int size = 0;//sns.size();
		while(size<10000){
			sns.add(String.format(SN_Template, 
					String.format("%03d", RandomData.intNumber(999)),
					String.format("%03d", RandomData.intNumber(999)),
					String.format("%03d", RandomData.intNumber(999))
					));
			size = sns.size();
		}
	}
}
