package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.user.dto.DeviceDTO;
import com.bhu.vas.api.user.model.DeviceEnum;
import com.bhu.vas.api.user.model.UserDeviceState;
import com.bhu.vas.business.ds.user.dao.UserDeviceStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserDeviceStateService extends AbstractCoreService<String,UserDeviceState,UserDeviceStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserDeviceStateDao userDeviceStateDao) {
		super.setEntityDao(userDeviceStateDao);
	}
	
	public boolean userHasDeviceRegister(String udid,DeviceEnum device){
		UserDeviceState state = this.getById(udid);
		if(state == null) return false;
		return state.fetchDevice(device) != null;
	}
	
	public boolean userNewDeviceRegisterOrReplace(String udid,DeviceEnum device,
			String dn,String dm,String dt,String du,String cv,String pv,String ut, String pt){
		boolean isupdate = false;
		DeviceDTO dto = new DeviceDTO();
		dto.setDn(dn);
		dto.setDm(dm);
		dto.setDt(dt);
		dto.setDu(du);
		dto.setCv(cv);
		dto.setPv(pv);
		dto.setUt(ut);
		dto.setPt(pt);
		dto.setSo(false);
		dto.setDrt(System.currentTimeMillis());
		UserDeviceState state = this.getById(udid);
		if(state == null){
			state = new UserDeviceState();
			state.setId(udid);
			state.addDevice(device, dto);
			this.insert(state);
			isupdate = true;
		}else{
			DeviceDTO dbdto = state.fetchDevice(device);
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
	
	public boolean userDeviceSignedOff(String udid, String dt, DeviceEnum device){
		UserDeviceState state = this.getById(udid);
		if(state == null) return false;
		DeviceDTO dbdto = state.fetchDevice(device);
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
