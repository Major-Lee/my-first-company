package com.bhu.vas.di.business.datainit.charging;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
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
	private WifiDeviceService wifiDeviceService;
	public void agentDailyRecord2Mongo(String date){
		
		ModelCriteria mc_user = new ModelCriteria();
		mc_user.createCriteria().andColumnEqualTo("utype", User.Agent_User).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc_user.setPageNumber(1);
		mc_user.setPageSize(100);
		EntityIterator<Integer, User> it_user = new KeyBasedEntityBatchIterator<Integer,User>(Integer.class,User.class, userSerivce.getEntityDao(), mc_user);
		while(it_user.hasNext()){
			List<Integer> uids = it_user.nextKeys();
			for(Integer uid:uids){
				
			}
		}
	}
}
