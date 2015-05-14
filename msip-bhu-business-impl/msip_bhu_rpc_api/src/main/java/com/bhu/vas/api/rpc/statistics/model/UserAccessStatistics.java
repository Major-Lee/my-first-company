package com.bhu.vas.api.rpc.statistics.model;

import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtPKModel;

import java.util.Date;
import java.util.Map;

/**
 * 记录用户登录的标识
 * 按月份分隔
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserAccessStatistics extends KeyDtoMapJsonExtPKModel<UserDatePK,Integer> {

	private Date created_at;

	private String device_mac;

	@Override
	public Class<Integer> getJsonParserModel() {
		return Integer.class;
	}
	@Override
	protected Class<UserDatePK> getPKClass() {
		return UserDatePK.class;
	}
	
	public Map<String,Integer> fetchAll(){
		return this.getExtension();
	}
	
	public void incrKey(String key){
		Integer value = this.getInnerModel(key);
		if(value == null){
			this.putInnerModel(key, 1);
		}else{
			this.putInnerModel(key, value.intValue()+1);
		}
	}
	
	public void replaceAll(Map<String,Integer> map){
		this.replaceInnerModels(map);
	}

	public String getMac() {
        if (this.getId() == null) {
            return null;
        }
        return this.getId().getMac();
    }

    public void setMac(String mac) {
        if(this.getId() == null) {
            this.setId(new UserDatePK());
        }
        this.getId().setMac(mac);
    }

    public String getDate() {
        if (this.getId() == null) {
            return "";
        }
        return this.getId().getDate();
    }

    public void setDate(String date) {
        if (this.getId() == null) {
            this.setId(new UserDatePK());
        }
        this.getId().setDate(date);
    }

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getDevice_mac() {
		return device_mac;
	}

	public void setDevice_mac(String device_mac) {
		this.device_mac = device_mac;
	}
}
