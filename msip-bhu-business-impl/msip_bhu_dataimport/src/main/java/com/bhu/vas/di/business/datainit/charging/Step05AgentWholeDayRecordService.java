package com.bhu.vas.di.business.datainit.charging;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeDayMDTO;
import com.bhu.vas.business.ds.agent.mdto.AgentWholeMonthMDTO;
import com.bhu.vas.business.ds.agent.mdto.LineRecord;
import com.bhu.vas.business.ds.agent.mdto.LineRecords;
import com.bhu.vas.business.ds.agent.mservice.AgentWholeDayMService;
import com.bhu.vas.business.ds.agent.mservice.AgentWholeMonthMService;
import com.bhu.vas.business.ds.agent.mservice.WifiDeviceWholeDayMService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 根据日志分析的结果得到设备日汇总数据后，可以对代理商代理的设备进行日汇总
 * @author Edmond
 *
 */
@Service
public class Step05AgentWholeDayRecordService {
	
	@Resource
	private UserService userSerivce;
	@Resource
	private AgentDeviceClaimService agentDeviceClaimService;
	
	@Resource
	private WifiDeviceWholeDayMService wifiDeviceWholeDayMService;

	@Resource
	private AgentWholeDayMService agentWholeDayMService;
	
	@Resource
	private AgentWholeMonthMService agentWholeMonthMService;
	
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
				doAgentWholeDay(date,uid,lineDeviceRecordsMap,lineHandsetRecordsMap);
			}
			doAgentWholeCurrentMonth(date,uids);
		}
	}
	
	private void doAgentWholeDay(String date,Integer uid,Map<String, LineRecords> lineDeviceRecordsMap,Map<String,Map<String,LineRecords>> lineHandsetRecordsMap){
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
					summary.setT_dct(summary.getT_dct()+1);//setTotal_connecttimes(summary.getTotal_connecttimes()+1);
					summary.setT_dod(summary.getT_dod()+record.gaps());//.setTotal_onlineduration(summary.getTotal_onlineduration()+record.gaps());
					summary.setT_dtx_bytes(summary.getT_dtx_bytes()+record.getTx_bytes());
					summary.setT_drx_bytes(summary.getT_drx_bytes()+record.getRx_bytes());
					//total_connecttimes++;
					//total_online_duration += record.gaps();
				}
				Map<String, LineRecords> map = lineHandsetRecordsMap.get(device.getMac());
				if(map != null){
					user_devices_hit++;
					summary.setT_handsets(summary.getT_handsets()+map.size());
					Iterator<Entry<String, LineRecords>> iter_inner = map.entrySet().iterator();
					while(iter_inner.hasNext()){
						Entry<String, LineRecords> next = iter_inner.next();
						LineRecords value = next.getValue();
						for(LineRecord record:value.getRecords()){
							summary.setT_hct(summary.getT_hct()+1);
							summary.setT_hod(summary.getT_dod()+record.gaps());
							summary.setT_htx_bytes(summary.getT_htx_bytes()+record.getTx_bytes());
							summary.setT_hrx_bytes(summary.getT_hrx_bytes()+record.getRx_bytes());
						}
					}
					
				}
				//lineHandsetRecordsMap.remove(device.getMac());
			}
		}
		//System.out.println("uid:"+uid+summary);
		AgentWholeDayMDTO mdto = new AgentWholeDayMDTO();
		mdto.setId(AgentWholeDayMDTO.generateId(date, uid));
		mdto.setDate(date);
		mdto.setUser(uid);
		mdto.setDct(summary.getT_dct());
		mdto.setDod(summary.getT_dod());
		mdto.setDevices(user_devices_hit);
		mdto.setDtx_bytes(summary.getT_dtx_bytes());
		mdto.setDrx_bytes(summary.getT_drx_bytes());
		
		mdto.setHandsets(summary.getT_handsets());
		mdto.setHct(summary.getT_hct());
		mdto.setHod(summary.getT_hod());
		mdto.setHtx_bytes(summary.getT_htx_bytes());
		mdto.setHrx_bytes(summary.getT_hrx_bytes());
		mdto.setUpdated_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
		agentWholeDayMService.save(mdto);
		
		//System.out.println("~~~~~~~size:"+lineHandsetRecordsMap.size());
	}
	
	private void doAgentWholeCurrentMonth(String date,List<Integer> users){
		Date certainDate = DateTimeHelper.parseDate(date, DateTimeHelper.FormatPattern5);
		Date monthStartDate = DateTimeExtHelper.getFirstDateOfMonth(certainDate);
		Date monthEndDate = DateTimeExtHelper.getLastDateOfMonth(certainDate);
		
		String monthKey = DateTimeHelper.formatDate(certainDate, DateTimeHelper.FormatPattern11);
		List<RecordSummaryDTO> summary = agentWholeDayMService.summaryAggregationBetween(users, 
				DateTimeHelper.formatDate(monthStartDate, DateTimeHelper.FormatPattern5), 
				DateTimeHelper.formatDate(monthEndDate, DateTimeHelper.FormatPattern5));
		
		for(RecordSummaryDTO dto:summary){
			int user = Integer.parseInt(dto.getId());
			AgentWholeMonthMDTO monthdto = new AgentWholeMonthMDTO();
			monthdto.setId(AgentWholeMonthMDTO.generateId(monthKey, user));
			monthdto.setDate(monthKey);
			monthdto.setUser(user);
			monthdto.setDevices(dto.getT_devices());
			monthdto.setDod(dto.getT_dod());
			monthdto.setDct(dto.getT_dct());
			monthdto.setDrx_bytes(dto.getT_drx_bytes());
			monthdto.setDtx_bytes(dto.getT_dtx_bytes());
			monthdto.setHandsets(dto.getT_handsets());
			monthdto.setHct(dto.getT_hct());
			monthdto.setHod(dto.getT_hod());
			monthdto.setHtx_bytes(dto.getT_htx_bytes());
			monthdto.setHrx_bytes(dto.getT_hrx_bytes());
			
			monthdto.setUpdated_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
			agentWholeMonthMService.save(monthdto);
		}
	}
}
