package com.bhu.vas.business.ds.user.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;


@Repository
public class UserWalletLogDao extends AbstractCoreDao<Long,UserWalletLog>{
	public Map<String,Object> fetchCashSumAndCountByUid(Integer uid,String start_time,String end_time,String mac,String umac,Integer status){
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			map.put("uid", uid);
			map.put("start_time", start_time);
			map.put("end_time", end_time);
			if (mac != null && !mac.isEmpty())
				map.put("mac", mac);
			if (umac != null && !umac.isEmpty())
				map.put("umac", umac);
			if (status != null)
				map.put("status", status);
			result = super.getSqlSessionMasterTemplate().selectOne(UserWalletLog.class.getName()+".countAndsum", map);
		}catch (Exception e) {
			return result;
		}
		return result;
	}
	
}
