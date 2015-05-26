package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.dto.MobileDeviceDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.UserMobileDeviceState;
import com.bhu.vas.business.ds.user.dao.UserMobileDeviceStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserMobileDeviceStateService extends AbstractCoreService<Integer,UserMobileDeviceState,UserMobileDeviceStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserMobileDeviceStateDao userMobileDeviceStateDao) {
		super.setEntityDao(userMobileDeviceStateDao);
	}
	
	public boolean userHasDeviceRegister(int uid,DeviceEnum device){
		UserMobileDeviceState state = this.getById(uid);
		if(state == null) return false;
		return state.fetchDevice(device) != null;
	}
	
	public boolean userNewDeviceRegisterOrReplace(int uid,DeviceEnum device,
			String dm, String dt,String cv,String pv,String ut, String pt){
		boolean isupdate = false;
		MobileDeviceDTO dto = new MobileDeviceDTO();
		dto.setDm(dm);
		dto.setDt(dt);
		dto.setCv(cv);
		dto.setPv(pv);
		dto.setUt(ut);
		dto.setPt(pt);
		dto.setSo(false);
		dto.setDrt(System.currentTimeMillis());
		UserMobileDeviceState state = this.getById(uid);
		if(state == null){
			state = new UserMobileDeviceState();
			state.setId(uid);
			state.addDevice(device, dto);
			this.insert(state);
			isupdate = true;
		}else{
			MobileDeviceDTO dbdto = state.fetchDevice(device);
			if(dbdto == null){
				state.addDevice(device, dto);
				this.update(state);
				isupdate = true;
			}else{
				if(dbdto.hasChanged(dto)){
					state.replaceDevice(device, dto);
					this.update(state);
					isupdate = true;
				}
			}
		}
		return isupdate;
	}
	
	public boolean userDeviceSignedOff(int uid, String dt, DeviceEnum device){
		UserMobileDeviceState state = this.getById(uid);
		if(state == null) return false;
		MobileDeviceDTO dbdto = state.fetchDevice(device);
		if(dbdto == null) return false;
		if(dbdto.isSo()) return false;
		
		if(!dbdto.getDt().equals(dt)) return false;
		//if(!dbdto.getPt().equals(pt)) return false;
		
		dbdto.setSo(true);
		state.replaceDevice(device, dbdto);
		this.update(state);
		return true;
	}
}

