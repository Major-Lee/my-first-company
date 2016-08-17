package com.bhu.vas.business.ds.user.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;


@Repository
public class UserWalletLogDao extends AbstractCoreDao<Long,UserWalletLog>{
	public Map<String,Object> fetchCashSumAndCountByUid(Integer uid,String start_time,String end_time,String mac){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("uid", uid);
		map.put("start_time", start_time);
		map.put("end_time", end_time);
		if (!mac.isEmpty() || mac != "")
			map.put("mac", mac);
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = super.getSqlSessionMasterTemplate().selectOne(UserWalletLog.class.getName()+".countAndsum", map);
		}catch (Exception e) {
			return result;
		}
		return result;
	}
	
	public String fetchCashSumByUid(Integer uid,String start_time,String end_time,String mac){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("uid", uid);
		map.put("start_time", start_time);
		map.put("end_time", end_time);
		if (!mac.isEmpty() || mac != "")
			map.put("mac", mac);
		String result = null;
		try{
			result = super.getSqlSessionMasterTemplate().selectOne(UserWalletLog.class.getName()+".cashSum", map);
		}catch (Exception e) {
			return result;
		}
		return result;
	}
	
	public String fetchCountRewardByUid(Integer uid,String start_time,String end_time,String mac){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("uid", uid);
		map.put("start_time", start_time);
		map.put("end_time", end_time);
		if (!mac.isEmpty() || mac != "")
			map.put("mac", mac);
		String result = null;
		try{
			result = super.getSqlSessionMasterTemplate().selectOne(UserWalletLog.class.getName()+".countReward", map);
		}catch (Exception e) {
			return result;
		}
		return result;
	}
}
