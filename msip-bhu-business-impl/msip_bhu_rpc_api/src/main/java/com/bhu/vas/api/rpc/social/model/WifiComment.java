package com.bhu.vas.api.rpc.social.model;

import java.util.Date;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseLongModel;

/**
 * Created by bluesand on 3/2/16.
 *
 * Wifi下的用户评论
 *
 */
public class WifiComment  extends BaseLongModel implements IRedisSequenceGenable {

    private long uid;

    private String bssid;

    private String message;

    private Date created_at;

	public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public void preInsert() {
        if (this.created_at == null)
            this.created_at = new Date();
        super.preInsert();
    }

    @Override
    public void setSequenceKey(Long key) {
        this.id = key;
    }

}
