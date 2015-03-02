package com.bhu.vas.business.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.user.model.UserDevice;
import com.bhu.vas.business.user.dao.UserDeviceDao;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.service.EntityService;

@Service
@Transactional("coreTransactionManager")
public class UserDeviceService extends EntityService<String,UserDevice, UserDeviceDao>{
	@Resource
	@Override
	public void setEntityDao(UserDeviceDao userDeviceDao) {
		super.setEntityDao(userDeviceDao);
	}
	/**
	 * 注册用户当前设备
	 * @param uid
	 * @param devicetoken
	 * @param devicetype
	 * @param pushtype
	 * @param client_system_v
	 * @param client_app_v
	 * @param unittype
	 */
	public void deviceRegister(String udid, String devicetoken, String devicetype, String pushtype, 
			String client_system_v, String client_app_v, String unittype){		
		UserDevice entity = this.getById(udid);
		if(entity == null){
			entity = new UserDevice();
			entity.setId(udid);
			entity.setDevicetoken(devicetoken);
			entity.setDevicetype(devicetype);
			entity.setPushtype(pushtype);
			entity.setClient_system_v(client_system_v);
			entity.setClient_app_v(client_app_v);
			entity.setUnittype(unittype);
			this.insert(entity);
		}else{
			entity.setDevicetoken(devicetoken);
			entity.setDevicetype(devicetype);
			entity.setPushtype(pushtype);
			entity.setClient_system_v(client_system_v);
			entity.setClient_app_v(client_app_v);
			entity.setUnittype(unittype);
			this.update(entity);
		}
	}
	/**
	 * 用户注销当前设备
	 * @param uid
	 * @param dt
	 * @return
	 */
	public boolean destoryRegister(String udid, String dt){
		if(StringHelper.isEmpty(dt)) return false;
		UserDevice entity = this.getById(udid);
		if(entity == null) return false;
		
		if(dt.equals(entity.getDevicetoken())){
			this.delete(entity);
		}
		return true;
	}
	/**
	 * 根据dt查找用户id
	 * @param dt
	 * @return
	 */
//	public List<String> getByDt(String dt){
//		ModelCriteria mc = new ModelCriteria();
//		mc.createCriteria().andColumnEqualTo("devicetoken", dt);
//		return this.findIdsByModelCriteria(mc);
//	}
}
