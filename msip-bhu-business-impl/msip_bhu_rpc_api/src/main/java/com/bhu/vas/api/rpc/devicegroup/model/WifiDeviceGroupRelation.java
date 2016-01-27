package com.bhu.vas.api.rpc.devicegroup.model;

import java.util.Date;

import com.bhu.vas.api.rpc.devicegroup.model.pk.WifiDeviceGroupRelationPK;
import com.smartwork.msip.cores.orm.model.BasePKModel;

/**
 * 群组关联的设备mac
 * @author Edmond
 *
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



    public long getGid() {
        if (this.getId() == null) {
            return 0;
        }
        return this.getId().getGid();
    }

    public void setGid(int gid) {
        if (this.getId() == null) {
            this.setId(new WifiDeviceGroupRelationPK());
        }
        this.getId().setGid(gid);
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
