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

    private long rx_bytes;

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

    public long getRx_bytes() {
        return rx_bytes;
    }

    public void setRx_bytes(long rx_bytes) {
        this.rx_bytes = rx_bytes;
    }
}
