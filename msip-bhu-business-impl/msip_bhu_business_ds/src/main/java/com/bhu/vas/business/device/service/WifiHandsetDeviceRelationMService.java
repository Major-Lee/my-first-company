package com.bhu.vas.business.device.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.business.device.dao.WifiHandsetDeviceRelationMDao;
import com.bhu.vas.business.device.mdto.WifiHandsetDeviceRelationMDTO;
import com.bhu.vas.business.msequence.mdao.SequenceMDao;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.Pagination;

@Service
public class WifiHandsetDeviceRelationMService {
	
	@Resource
	private WifiHandsetDeviceRelationMDao wifiHandsetDeviceRelationMDao;
	
	@Resource
	private SequenceMDao sequenceMDao;
	
	public void addRelation(String handsetId, String wifiId, Date login_at){
		if(StringUtils.isEmpty(handsetId) || StringUtils.isEmpty(wifiId)) return;
		if(login_at == null) login_at = new Date();
		
		WifiHandsetDeviceRelationMDTO mdto = new WifiHandsetDeviceRelationMDTO();
		mdto.setId(sequenceMDao.getNextSequenceId(WifiHandsetDeviceRelationMDTO.class.getName()));
		mdto.setHandsetId(handsetId);
		mdto.setWifiId(wifiId);
		mdto.setLogin_at(login_at);
		mdto = wifiHandsetDeviceRelationMDao.save(mdto);
	}
	
	public Pagination<WifiHandsetDeviceRelationMDTO> findRelations(String wifiId, int pageNo, int pageSize){
		Query query = new Query(Criteria.where("wifiId").is(wifiId));
		query.with(new Sort(Direction.DESC,"_id"));
		return wifiHandsetDeviceRelationMDao.findPagination(pageNo, pageSize, query);
	}
	
	public Pagination<WifiHandsetDeviceRelationMDTO> findRelations(String wifiId, String handsetId, int pageNo, int pageSize){
		Query query = new Query(Criteria.where("wifiId").is(wifiId).and("handsetId").is(handsetId));
		query.with(new Sort(Direction.DESC,"_id"));
		return wifiHandsetDeviceRelationMDao.findPagination(pageNo, pageSize, query);
	}
	
}
