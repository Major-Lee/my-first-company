package com.bhu.vas.business.ds.advertise.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class AdvertiseDao extends AbstractCoreDao<Integer, Advertise>{
	public int countByAdvertiseTime(Date start,Date end){
		int n=0;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("start", start);
		map.put("end", end);
		try {
			n = super.getSqlSessionMasterTemplate().selectOne(Advertise.class.getName()+".countByAdvertiseTime",map);
		} catch (Exception e) {
			return n;
		}	
		return n;
	}
}
