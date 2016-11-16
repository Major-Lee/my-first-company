package com.bhu.vas.business.ds.advertise.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class AdvertiseDao extends AbstractCoreDao<String, Advertise>{
	public int countByAdvertiseTime(Date start,Date end,String province, String city,
			String district ){
		int n=0;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("start", start);
		map.put("end", end);
		map.put("province", province);
		map.put("city", city);
		map.put("district", district);
		try {
			n = super.getSqlSessionMasterTemplate().selectOne(Advertise.class.getName()+".countByAdvertiseTime",map);
		} catch (Exception e) {
			return n;
		}	
		return n;
	}
	public List<Advertise> queryByAdvertiseTime(String start,String end,String province, String city,
			String district ){
		List<Advertise> ads= new ArrayList<Advertise>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("start", start);
		map.put("end", end);
		map.put("province", province);
		map.put("city", city);
		map.put("district", district);
		try {
			ads = super.getSqlSessionMasterTemplate().selectList(Advertise.class.getName()+".queryByAdvertiseTime",map);
		} catch (Exception e) {
			return ads;
		}	
		return ads;
	}
}
