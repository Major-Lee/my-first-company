package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGroupRelationPK;
import com.smartwork.msip.cores.orm.model.BasePKModel;

/**
 * Created by bluesand on 7/16/15.
 */
@SuppressWarnings("serial")
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
        return WifiDeviceGroupRelationPK.class;
    }


}
