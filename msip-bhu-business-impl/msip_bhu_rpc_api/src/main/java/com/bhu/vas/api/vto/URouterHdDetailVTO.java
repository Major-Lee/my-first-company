package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 6/26/15.
 */
@SuppressWarnings("serial")
public class URouterHdDetailVTO implements Serializable {
    private String mac;
    //终端mac
    private String hd_mac;
    //下行字节累加
    private String total_rx_bytes;

    private String rx_bytes;

    private List<URouterHdTimeLineVTO> timeline;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    public String getTotal_rx_bytes() {
        return total_rx_bytes;
    }

    public String getRx_bytes() {
        return rx_bytes;
    }

    public void setRx_bytes(String rx_bytes) {
        this.rx_bytes = rx_bytes;
    }

    public void setTotal_rx_bytes(String total_rx_bytes) {
        this.total_rx_bytes = total_rx_bytes;
    }

    public List<URouterHdTimeLineVTO> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<URouterHdTimeLineVTO> timeline) {
        this.timeline = timeline;
    }

}
