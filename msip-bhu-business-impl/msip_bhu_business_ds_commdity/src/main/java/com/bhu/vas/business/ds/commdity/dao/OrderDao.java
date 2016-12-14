package com.bhu.vas.business.ds.commdity.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCommdityDao;

@Repository
public class OrderDao extends AbstractCommdityDao<String, Order>{//ReadWriteSpliterEntityDao<StorePurchasedItemPK,StorePurchasedItem,Integer>{

	public List<Map<String,Object>> qualityGoodsSharedealPages(int pageNo, int pageSize){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> params = new HashMap<String,Object>();
		try{
			params.put("start", (pageNo-1)*pageSize);
			params.put("limit", pageSize);
			result = super.getSqlSessionMasterTemplate().selectList(UserWalletLog.class.getName()+".qualityGoodsSharedealPages", params);
		}catch (Exception e) {
			return result;
		}
		return result;
	}

	public List<Map<String,Object>> countQualityGoodsSharedeal(){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> params = new HashMap<String,Object>();
		try{
			result = super.getSqlSessionMasterTemplate().selectList(UserWalletLog.class.getName()+".countQualityGoodsSharedeal", params);
		}catch (Exception e) {
			return result;
		}
		return result;
	}
}
