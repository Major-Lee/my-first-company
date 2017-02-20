package com.bhu.vas.business.ds.commdity.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.commdity.model.Order;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCommdityDao;

@Repository
public class OrderDao extends AbstractCommdityDao<String, Order>{//ReadWriteSpliterEntityDao<StorePurchasedItemPK,StorePurchasedItem,Integer>{

	public List<Map<String,Object>> qualityGoodsSharedealPages(int pageNo, int pageSize){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> params = new HashMap<String,Object>();
		try{
			params.put("start", (pageNo-1)*pageSize);
			params.put("limit", pageSize);
			result = super.getSqlSessionSlaverTemplate().selectList(Order.class.getName()+".qualityGoodsSharedealPages", params);
		}catch (Exception e) {
			return result;
		}
		return result;
	}

	public List<Map<String,Object>> countQualityGoodsSharedeal(){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> params = new HashMap<String,Object>();
		try{
			result = super.getSqlSessionSlaverTemplate().selectList(Order.class.getName()+".countQualityGoodsSharedeal", params);
		}catch (Exception e) {
			return result;
		}
		return result;
	}
	
	public List<Map<String,Object>> statOrderIncome(Map<String, Object> map) {
		return super.getSqlSessionSlaverTemplate().selectList(Order.class.getName()+".statOrderIncome",map);
	}
}
