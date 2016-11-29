package com.bhu.vas.business.ds.user.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;


@Repository
public class UserWalletLogDao extends AbstractCoreDao<Long,UserWalletLog>{
	public Map<String,Object> fetchCashSumAndCountByUid(Integer uid, String start_time, String end_time, String mac,
			String umac, Integer status, String dut, Integer type, String role){
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			map.put("uid", uid);
			map.put("type", type);
			if (role != null && !role.isEmpty())
				map.put("role", role);
			if (start_time != null && !start_time.isEmpty())
				map.put("start_time", start_time);
			if (end_time != null && !end_time.isEmpty())
				map.put("end_time", end_time);
			if (mac != null && !mac.isEmpty())
				map.put("mac", mac);
			if (umac != null && !umac.isEmpty())
				map.put("umac", umac);
			if (status != null)
				map.put("status", status);
			if (dut != null && !dut.isEmpty())
				map.put("dut", dut);
			result = super.getSqlSessionMasterTemplate().selectOne(UserWalletLog.class.getName()+".countAndsum", map);
		}catch (Exception e) {
			return result;
		}
		return result;
	}
	
	
	public List<Map<String,Object>> queryRewardOrderpages(Integer uid, String mac, String umac, Integer status, String dut, 
			Integer type, String role, long start_created_ts, long end_created_ts, int pageNo, int pageSize){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> params = new HashMap<String,Object>();
		try{
			params.put("uid", uid);
			params.put("start_created_ts", new Date(start_created_ts));
			params.put("end_created_ts", new Date(end_created_ts));
			params.put("type", type);
			params.put("start", (pageNo-1)*pageSize);
			params.put("limit", pageSize);
			if (mac != null && !mac.isEmpty())
				params.put("mac", mac);
			if (umac != null && !umac.isEmpty())
				params.put("umac", umac);
			if (status != null)
				params.put("status", status);
			if (dut != null && !dut.isEmpty())
				params.put("dut", dut);
			if (role != null && !role.isEmpty())
				map.put("role", role);
			result = super.getSqlSessionMasterTemplate().selectList(UserWalletLog.class.getName()+".queryRewardpages", params);
		}catch (Exception e) {
			return result;
		}
		return result;
	}
}
