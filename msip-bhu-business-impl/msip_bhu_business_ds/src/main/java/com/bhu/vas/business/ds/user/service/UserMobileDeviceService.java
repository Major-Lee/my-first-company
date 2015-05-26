package com.bhu.vas.business.ds.user.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.user.model.UserMobileDevice;
import com.bhu.vas.business.ds.user.dao.UserMobileDeviceDao;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.service.EntityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
@Transactional("coreTransactionManager")
public class UserMobileDeviceService extends EntityService<Integer,UserMobileDevice, UserMobileDeviceDao>{
	@Resource
	@Override
	public void setEntityDao(UserMobileDeviceDao userMobileDeviceDao) {
		super.setEntityDao(userMobileDeviceDao);
	}
	/**
	 * 注册用户当前设备
	 * @param uid
	 * @param dt
	 * @param d
	 * @param pt
	 */
	public void deviceRegister(int uid, String dt, String d, String pt){
		if(StringHelper.isEmpty(dt)) return;
		if(StringHelper.isEmpty(d)) return;
		//如果已经存在关系, 则先删除
		List<Integer> exist_entity_uids = this.getByDt(dt);
		if(!exist_entity_uids.isEmpty()){
			this.deleteByIds(exist_entity_uids);
		}
		
		UserMobileDevice entity = this.getById(uid);
		if(entity == null){
			entity = new UserMobileDevice();
			entity.setId(uid);
			entity.setDt(dt);
			entity.setD(d);
			entity.setPt(pt);
			this.insert(entity);
		}else{
			entity.setDt(dt);
			entity.setD(d);
			entity.setPt(pt);
			this.update(entity);
		}
	}
	/**
	 * 用户注销当前设备
	 * @param uid
	 * @param dt
	 * @return
	 */
	public boolean destoryRegister(int uid, String dt){
		//if(StringHelper.isEmpty(dt)) return false;
		UserMobileDevice entity = this.getById(uid);
		if(entity == null) return false;
		
		if(StringUtils.isEmpty(dt)){
			this.delete(entity);
		}else{
			if(dt.equals(entity.getDt())){
				this.delete(entity);
			}
		}
		return true;
	}
	/**
	 * 根据dt查找用户id
	 * @param dt
	 * @return
	 */
	public List<Integer> getByDt(String dt){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("dt", dt);
		return this.findIdsByModelCriteria(mc);
	}
}