package com.bhu.vas.business.ds.user.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;


@Service
public class UserWifiDeviceFacadeService {
	public final static int WIFI_DEVICE_BIND_LIMIT_NUM = 10;

    @Resource
    private UserWifiDeviceService userWifiDeviceService;
    
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private UserService userService;
	
    @Resource
    private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
    
	/**
	 * 新增用户设备关系数据
	 * @param mac 设备mac
	 * @param uid 用户id
	 * @return
	 */
	public UserWifiDevice insertUserWifiDevice(String mac, Integer uid){
		return insertUserWifiDevice(mac, uid, null);
	}
	/**
	 * 新增用户设备关系数据
	 * @param mac 设备mac
	 * @param uid 用户id
	 * @param device_name 设备别名
	 * @return
	 */
	public UserWifiDevice insertUserWifiDevice(String mac, Integer uid, String device_name){
		if(StringUtils.isEmpty(mac) || uid == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		UserWifiDevice entity = new UserWifiDevice();
		entity.setId(mac);
		entity.setUid(uid);
		entity.setDevice_name(device_name);
		entity.setCreated_at(new Date());
		return userWifiDeviceService.insert(entity);
	}
	
	/**
	 * 验证设备是否被某用户绑定
	 * @param mac 设备mac
	 * @param uid 用户id
	 */
	public void validateUserWifiDevice(String mac, Integer uid){
		UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
		if(userWifiDevice == null || !userWifiDevice.getUid().equals(uid)){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_NOT_YOURBINDED,new String[]{mac});
		}
	}
	/**
	 * 根据用户id获取绑定的设备macs
	 * @param uid
	 * @return
	 */
	public List<String> findUserWifiDeviceIdsByUid(Integer uid){
		List<UserWifiDevice> userWifiDevices = findUserWifiDevicesByUid(uid);
		if(userWifiDevices == null || userWifiDevices.isEmpty()) return Collections.emptyList();
		
		List<String> ids = new ArrayList<String>();
		for(UserWifiDevice userWifiDevice : userWifiDevices){
			ids.add(userWifiDevice.getId());
		}
		return ids;
	}
	
	/**
	 * 根据用户id获取绑定的设备关系数据
	 * @param uid
	 * @return
	 */
	public List<UserWifiDevice> findUserWifiDevicesByUid(Integer uid){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid);
		return userWifiDeviceService.findModelByModelCriteria(mc);
	}
	
	/**
	 * 获取用户的设备的绑定关系数据
	 * 如果用户id和数据中的用户id不匹配 则返回null
	 * @param mac
	 * @param uid
	 * @return
	 */
	public UserWifiDevice findUserWifiDeviceById(String mac, Integer uid){
		UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
		if(userWifiDevice == null) return null;
		
		if(!userWifiDevice.getUid().equals(uid)) return null;
		return userWifiDevice;
	}
	
	/**
	 * 根据设备mac获取绑定此设备的用户id
	 * @param mac
	 * @return
	 */
	public Integer findUidById(String mac){
		UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
		if(userWifiDevice == null) return null;
		return userWifiDevice.getUid();
	}

	/**
	 * 根据用户id和设备macs条件进行查询数量
	 * @param uid
	 * @param macs
	 * @return
	 */
	public int countByUidAndMacsParam(Integer uid, List<String> macs){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid).andColumnIn("id", macs);
		return userWifiDeviceService.countByModelCriteria(mc);
	}

	/**
	 * 清除用户绑定的设备
	 * @param uid
	 */
	public void clearUserWifiDevices(int uid){
    	ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("uid", uid);
        userWifiDeviceService.deleteByCommonCriteria(mc);
    }
	
	/**
	 * 判断设备mac是存在关系数据
	 * @param mac
	 * @return
	 */
    public boolean isUserWifiDevice(String mac) {
    	UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
    	return userWifiDevice == null ? false : true;
    }
    
    /**
     * 根据设备mac获取绑定此设备的用户数据实体
     * @param mac
     * @return
     */
	public User findUserById(String mac){
		if(StringUtils.isNotEmpty(mac)){
			String mac_lower = mac.toLowerCase();
			Integer bindUid = this.findUidById(mac_lower);
			if(bindUid != null){
				return userService.getById(bindUid);
			}
		}
		return null;
	}
	
	/**
	 * 暂时兼容函数
	 * @param uid
	 * @return
	 */
	public List<UserDeviceDTO>  fetchBindDevices(int uid) {
		List<UserWifiDevice> userDeviceList = this.fetchBindDevicesWithLimit(uid, WIFI_DEVICE_BIND_LIMIT_NUM);
		List<UserDeviceDTO> bindDevicesDTO = new ArrayList<UserDeviceDTO>();
		if(userDeviceList != null && !userDeviceList.isEmpty()){
			for (UserWifiDevice userWifiDevice : userDeviceList) {
				UserDeviceDTO userDeviceDTO = new UserDeviceDTO();
				userDeviceDTO.setMac(userWifiDevice.getId());
				userDeviceDTO.setUid(userWifiDevice.getUid());
				userDeviceDTO.setDevice_name(userWifiDevice.getDevice_name());
	
				WifiDevice wifiDevice = wifiDeviceService.getById(userWifiDevice.getId());
				if (wifiDevice != null) {
					if(StringUtils.isEmpty(wifiDevice.getOrig_swver())) continue;
					if(wifiDevice.getOrig_swver().endsWith(VapEnumType.DUT_soc) || wifiDevice.getOrig_swver().endsWith(VapEnumType.DUT_CWifi) ){
						continue;
					}
					
					userDeviceDTO.setOnline(wifiDevice.isOnline());
					if (wifiDevice.isOnline()) { //防止有些设备已经离线了，没有更新到后台
						userDeviceDTO.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance()
								.presentOnlineSize(userWifiDevice.getId()));
					}
					userDeviceDTO.setVer(wifiDevice.getOrig_swver());
					userDeviceDTO.setWork_mode(wifiDevice.getWork_mode());
					userDeviceDTO.setOrig_model(wifiDevice.getOrig_model());
				}
				bindDevicesDTO.add(userDeviceDTO);
			}
		}
		return bindDevicesDTO;
	}
	
	/**
	 * 暂时兼容函数
	 * @param uid
	 * @param limit
	 * @return
	 */
	public List<UserWifiDevice> fetchBindDevicesWithLimit(int uid, int limit) {
	      ModelCriteria mc = new ModelCriteria();
	      mc.createCriteria().andColumnEqualTo("uid", uid);
	      mc.setPageNumber(1);
	      mc.setPageSize(limit);
	      return userWifiDeviceService.findModelByModelCriteria(mc);
	  }
	
	/**
	 * 根据用户id遍历设备关系数据
	 * @param uid
	 * @param notify
	 */
	public void iteratorByUid(Integer uid, IteratorNotify<List<UserWifiDevice>> notify){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid);
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<String, UserWifiDevice> it = new KeyBasedEntityBatchIterator<String, UserWifiDevice>(String.class, UserWifiDevice.class, userWifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<UserWifiDevice> userWifiDevices = it.next();
			notify.notifyComming(userWifiDevices);
		}
	}
}
