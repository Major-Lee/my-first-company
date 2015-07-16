package com.bhu.vas.api.rpc.devices.model;

import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGroupRelationPK;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.smartwork.msip.cores.orm.model.BasePKModel;

import java.util.Date;

/**
 * Created by bluesand on 7/16/15.
 */
public class WifiDeviceGroupRelation extends BasePKModel<WifiDeviceGroupRelationPK> {

    private Date created_at;

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }



    public int getGid() {
        if (this.getId() == null) {
            return 0;
        }
        return this.getId().getGid();
    }

    public void setGid(int uid) {
        if (this.getId() == null) {
            this.setId(new WifiDeviceGroupRelationPK());
        }
        this.getId().setGid(uid);
    }

    public String getMac() {
        if (this.getId() == null) {
            return null;
        }
        return this.getId().getMac();
    }

    public void setMac(String mac) {
        if(this.getId() == null) {
            this.setId(new WifiDeviceGroupRelationPK());
        }
        this.getId().setMac(mac);
    }

    @Override
    protected Class<WifiDeviceGroupRelationPK> getPKClass() {
        return null;
    }


}
