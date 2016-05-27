package com.bhu.vas.business.ds.charging.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchDetail;
import com.bhu.vas.business.ds.charging.dao.WifiDeviceBatchDetailDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class WifiDeviceBatchDetailService extends AbstractCoreService<String,WifiDeviceBatchDetail,WifiDeviceBatchDetailDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceBatchDetailDao wifiDeviceBatchDetailDao) {
		super.setEntityDao(wifiDeviceBatchDetailDao);
	}
	
	
	public void deviceStore(String dmac,String sellor,String partner,int last_importor,String last_batchno){
		boolean insert = false;
		WifiDeviceBatchDetail detail = this.getById(dmac);
		if(detail == null){
			detail = new WifiDeviceBatchDetail();
			detail.setId(dmac);
			insert = true;
		}
		detail.setLast_importor(last_importor);
		detail.setLast_batchno(last_batchno);
		detail.setSellor(sellor);
		detail.setPartner(partner);
		if(insert){
			this.insert(detail);
		}else{
			this.update(detail);
		}
	}
}
