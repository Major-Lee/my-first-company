package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;


@SuppressWarnings("serial")
public class UserWifiDevice extends BaseStringModel {
	//绑定此设备的用户id
	private Integer uid;
	//用户给此设备设置的别名
    private String device_name;
    //设备名称是否正式修改
    private boolean device_name_modifyed;
    
    private Date created_at;
    
    public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
    
    public boolean isDevice_name_modifyed() {
		return device_name_modifyed;
	}

	public void setDevice_name_modifyed(boolean device_name_modifyed) {
		this.device_name_modifyed = device_name_modifyed;
	}

	public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
