package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.dto.version.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionBuilder;
import com.bhu.vas.business.ds.device.dao.WifiDeviceVersionBuilderDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceVersionBuilderService extends AbstractCoreService<String,WifiDeviceVersionBuilder, WifiDeviceVersionBuilderDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceVersionBuilderDao wifiDeviceVersionBuilderDao) {
		super.setEntityDao(wifiDeviceVersionBuilderDao);
	}

	public String deviceVersionUpdateURL(boolean isFirstGray){
		WifiDeviceVersionBuilder versionb = this.getById(isFirstGray?WifiDeviceVersionBuilder.VersionBuilder_FirstGray:WifiDeviceVersionBuilder.VersionBuilder_Normal);
		if(versionb == null) return null;
		return versionb.getFirmware_upgrade_url();
    }
	
	
	public boolean deviceVersionUpdateCheck(boolean isFirstGray,String currentDeviceVB){
		WifiDeviceVersionBuilder versionb = this.getById(isFirstGray?WifiDeviceVersionBuilder.VersionBuilder_FirstGray:WifiDeviceVersionBuilder.VersionBuilder_Normal);
		if(versionb == null || StringUtils.isEmpty(currentDeviceVB)) return false;
		int ret = DeviceVersion.compareVersions(currentDeviceVB,versionb.getD_firmware_name());
		if(versionb.isForce_device_update() && ret == -1 ){
			return true;
		}else{
			return false;
		}
    }
	
	public boolean appVersionUpdateCheck(String currentClientVB){
		return false;
	}
	
	/*public TailPage<WifiDevice> findModelByOnline(int pageNo, int pageSize){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", true);
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		return super.findModelTailPageByModelCriteria(mc);
	}
	
	public List<WifiDevice> findOnlineByIds(List<String> ids, boolean online){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIn("id", ids).an.andColumnEqualTo("online", online);
		return super.findModelByModelCriteria(mc);
	}
	
	public List<String> filterOnlineIdsWith(List<String> ids, boolean online){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIn("id", ids).andColumnEqualTo("online", online);
		return super.findIdsByModelCriteria(mc);
	}
	
	public long countByOnline(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", true);
		return super.countByModelCriteria(mc);
	}
	
	public long count(){
		return super.countByModelCriteria(new ModelCriteria());
	}*/
}
