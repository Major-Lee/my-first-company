package com.bhu.vas.api.rpc.user.model;

import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.smartwork.msip.cores.orm.model.BasePKModel;

import java.util.Date;

/**
 * Created by bluesand on 15/4/10.
 */
@SuppressWarnings("serial")
public class UserDevice extends BasePKModel<UserDevicePK> {

    private String device_name;
    //设备名称是否正式修改
    private boolean device_name_modifyed;
    private Date created_at;

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

    public String getMac() {
        if (this.getId() == null) {
            return null;
        }
        return this.getId().getMac();
    }

    public void setMac(String mac) {
        if(this.getId() == null) {
            this.setId(new UserDevicePK());
        }
        this.getId().setMac(mac);
    }

    public int getUid() {
        if (this.getId() == null) {
            return 0;
        }
        return this.getId().getUid();
    }

    public void setUid(int uid) {
        if (this.getId() == null) {
            this.setId(new UserDevicePK());
        }
        this.getId().setUid(uid);
    }

    @Override
    protected Class<UserDevicePK> getPKClass() {
        return UserDevicePK.class;
    }
}
