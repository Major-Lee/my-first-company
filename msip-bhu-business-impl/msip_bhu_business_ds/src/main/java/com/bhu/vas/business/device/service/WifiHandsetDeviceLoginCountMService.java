package com.bhu.vas.business.device.service;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.device.dao.WifiHandsetDeviceLoginCountMDao;
import com.bhu.vas.business.device.mdto.WifiHandsetDeviceLoginCountMDTO;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.Pagination;

/**
 * wifi设备接入移动设备的接入数量 (mongodb)
 * 一个移动设备连接同一个wifi设备多次，只增量1次
 * id = wifiId
 * @author tangzichao
 *
 */
@Service
public class WifiHandsetDeviceLoginCountMService {
	
	@Resource
	private WifiHandsetDeviceLoginCountMDao wifiHandsetDeviceLoginCountMDao;
	
	
	public void incrCount(String wifiId){
		this.incrCount(wifiId, 1);
	}
	
	public void incrCount(String wifiId, long incr){
		Query query = new Query(Criteria.where("_id").is(wifiId));
		Update update = new Update();
		update.inc("count", incr);
		wifiHandsetDeviceLoginCountMDao.upsert(query, update);
	}
	
	/**
	 * 按照count从大到小排序
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Pagination<WifiHandsetDeviceLoginCountMDTO> findLoginCountsSortByCountDesc(int pageNo, int pageSize){
		Query query = new Query();
		query.with(new Sort(Direction.DESC,"count"));
		return wifiHandsetDeviceLoginCountMDao.findPagination(pageNo, pageSize, query);
	}
	
	
}
