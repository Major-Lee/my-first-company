package com.bhu.vas.rpc.facade;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.wifi.model.SsidInfo;
import com.bhu.vas.business.ds.wifi.service.SsidInfoService;
import com.bhu.vas.business.search.model.wifi.SsidDocument;
import com.bhu.vas.business.search.model.wifi.SsidDocumentHelper;
import com.bhu.vas.business.search.service.wifi.SsidDataSearchService;

/**
 * 第三方业务的service
 * @author yetao
 *
 */
@Service
public class SsidUnitFacadeService {
	private final static Logger logger = LoggerFactory.getLogger(SsidUnitFacadeService.class);

	@Resource 
	private SsidInfoService ssidInfoService;

	@Resource
	private SsidDataSearchService ssidDataSearchService;
	
	public Boolean reportSsidInfo(String bssid, String ssid, String mode, String pwd, Double lat, Double lon){
		SsidInfo ssidinfo = ssidInfoService.getById(bssid);
		boolean newflag = false;
		if(ssidinfo == null){
			ssidinfo = new SsidInfo();
			newflag = true;
			ssidinfo.setId(bssid);
		}
		ssidinfo.setSsid(ssid);
		ssidinfo.setMode(mode);
		ssidinfo.setPwd(pwd);
		ssidinfo.setLat(lat);
		ssidinfo.setLon(lon);
		if(newflag)
			ssidInfoService.insert(ssidinfo);
		else
			ssidInfoService.update(ssidinfo);
		
		SsidDocument doc = SsidDocumentHelper.fromNormalSsid(ssidinfo);
		ssidDataSearchService.bulkIndexOne(doc);
		return Boolean.TRUE;
	}
	
	public SsidInfo querySsidInfo(String bssid, String ssid, String mode){
		SsidDocument doc = ssidDataSearchService.searchById(bssid);
		if(doc != null && ssid.equals(doc.getS_ssid()) && mode.equals(doc.getS_mode()))
			return SsidDocumentHelper.toSsid(doc);
		return null;
	}
}
