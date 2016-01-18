package com.bhu.vas.business.ds.device.service;


/**
 * wifi设备接入移动设备的接入数量 (mongodb)
 * 一个移动设备连接同一个wifi设备多次，只增量1次
 * id = wifiId
 * @author tangzichao
 *
 */
//@Service
public class WifiHandsetDeviceLoginCountMService {
	/*	
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
	
	*//**
	 * 按照count从大到小排序
	 * @param pageNo
	 * @param pageSize
	 * @return
	 *//*
	public Pagination<WifiHandsetDeviceLoginCountMDTO> findWifiDevicesOrderMaxHandset(int pageNo, int pageSize){
		Query query = new Query();
		query.with(new Sort(Direction.DESC,"count"));
		return wifiHandsetDeviceLoginCountMDao.findPagination(pageNo, pageSize, query);
	}
	
	*/
}
