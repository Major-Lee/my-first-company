package com.bhu.vas.api.mdto;

import java.io.Serializable;

/**
 * Created by bluesand on 8/11/15.
 */
@SuppressWarnings("serial")
public class WifiHandsetDeviceItemLogMDTO implements Serializable {
    /**
     * 时间戳
     */
    private long ts;
    /**
     * 上下线类型 login/logout
     */
    private String type;

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
