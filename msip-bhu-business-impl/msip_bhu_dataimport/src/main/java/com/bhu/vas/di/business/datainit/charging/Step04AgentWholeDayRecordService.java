package com.bhu.vas.di.business.datainit.charging;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeDayMDTO;
import com.bhu.vas.business.ds.agent.mdto.LineRecord;
import com.bhu.vas.business.ds.agent.mdto.LineRecords;
import com.bhu.vas.business.ds.agent.mservice.AgentWholeDayMService;
import com.bhu.vas.business.ds.agent.mservice.WifiDeviceWholeDayMService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 根据日志分析的结果得到设备日汇总数据后，可以对代理商代理的设备进行日汇总
 * @author Edmond
 *
 */
@Service
public class Step04AgentWholeDayRecordService {
	
	@Resource
	private UserService userSerivce;
	@Resource
	private AgentDeviceClaimService agentDeviceClaimService;
	
	@Resource
	private WifiDeviceWholeDayMService wifiDeviceWholeDayMService;

	@Resource
	private AgentWholeDayMService agentWholeDayMService;
	
	public void agentDailyRecord2Mongo(String date,Map<String, LineRecords> lineDeviceRecordsMap,Map<String,Map<String,LineRecords>> lineHandsetRecordsMap){
		ModelCriteria mc_user = new ModelCriteria();
		mc_user.createCriteria().andColumnEqualTo("utype", User.Agent_User).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc_user.setPageNumber(1);
		mc_user.setPageSize(100);
		EntityIterator<Integer, User> it_user = new KeyBasedEntityBatchIterator<Integer,User>(Integer.class,User.class, userSerivce.getEntityDao(), mc_user);
		while(it_user.hasNext()){
			List<Integer> uids = it_user.nextKeys();
			for(Integer uid:uids){
				//agentDeviceClaimService.findModelPageByModelCriteria(mc);
				int user_devices_hit = 0;
				RecordSummaryDTO summary = new RecordSummaryDTO();
				summary.setId(uid.toString());
				ModelCriteria mc_claim = new ModelCriteria();
				mc_claim.createCriteria().andColumnEqualTo("uid", uid).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
				mc_claim.setPageNumber(1);
				mc_claim.setPageSize(100);
				EntityIterator<String, AgentDeviceClaim> it_claim = new KeyBasedEntityBatchIterator<String,AgentDeviceClaim>(String.class,AgentDeviceClaim.class, agentDeviceClaimService.getEntityDao(), mc_claim);
				while(it_claim.hasNext()){
					List<AgentDeviceClaim> devices = it_claim.next();
					//List<String> stringIds = IdHelper.getPKs(devices, String.class);
					/*List<String> macs = new ArrayList<String>();
					for(AgentDeviceClaim device:devices){
						macs.add(device.getMac());
					}
					if(!macs.isEmpty()){
						//List<RecordSummaryDTO> summaryAggregationWith = wifiDeviceWholeDayMService.summaryAggregationWith(macs, date);
						//从mongo中获取汇总数据
						List<RecordSummaryDTO> summaryAggregation = wifiDeviceWholeDayMService.summaryAggregationWith(macs,date);
						for(RecordSummaryDTO dto:summaryAggregation){
							summary.incr(dto);
							//System.out.println(String.format("B mac[%s] total_onlineduration[%s] total_connecttimes[%s]",dto.getId(), dto.getTotal_onlineduration(),dto.getTotal_connecttimes()));
						}
					}*/
					//直接从缓存中汇总数据
					for(AgentDeviceClaim device:devices){
						LineRecords lineRecords = lineDeviceRecordsMap.get(device.getMac());
						if(lineRecords == null || lineRecords.getRecords().isEmpty() ) continue;
						for(LineRecord record:lineRecords.getRecords()){
							summary.setTotal_connecttimes(summary.getTotal_connecttimes()+1);
							summary.setTotal_onlineduration(summary.getTotal_onlineduration()+record.gaps());
							//total_connecttimes++;
							//total_online_duration += record.gaps();
						}
						Map<String, LineRecords> map = lineHandsetRecordsMap.get(device.getMac());
						if(map != null){
							user_devices_hit++;
							summary.setTotal_handsets(summary.getTotal_handsets()+map.size());
						}
						//lineHandsetRecordsMap.remove(device.getMac());
					}
				}
				//System.out.println("uid:"+uid+summary);
				AgentWholeDayMDTO mdto = new AgentWholeDayMDTO();
				mdto.setId(AgentWholeDayMDTO.generateId(date, uid));
				mdto.setDate(date);
				mdto.setUser(uid);
				mdto.setConnecttimes(summary.getTotal_connecttimes());
				mdto.setOnlineduration(summary.getTotal_onlineduration());
				mdto.setDevices(user_devices_hit);
				mdto.setHandsets(summary.getTotal_handsets());
				mdto.setTx_bytes(0l);
				mdto.setRx_bytes(0l);
				agentWholeDayMService.save(mdto);
				
				System.out.println("~~~~~~~size:"+lineHandsetRecordsMap.size());
			}
		}
	}
}
