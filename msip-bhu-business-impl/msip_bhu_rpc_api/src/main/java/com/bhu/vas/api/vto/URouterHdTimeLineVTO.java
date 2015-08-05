package com.bhu.vas.api.vto;

import com.bhu.vas.api.mdto.WifiHandsetDeviceItemDetailMDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 6/25/15.
 */
@SuppressWarnings("serial")
public class URouterHdTimeLineVTO implements Serializable {

    private String date;

    private List<WifiHandsetDeviceItemDetailMDTO> detail;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<WifiHandsetDeviceItemDetailMDTO> getDetail() {
        return detail;
    }

    public void setDetail(List<WifiHandsetDeviceItemDetailMDTO> detail) {
        this.detail = detail;
    }
}
