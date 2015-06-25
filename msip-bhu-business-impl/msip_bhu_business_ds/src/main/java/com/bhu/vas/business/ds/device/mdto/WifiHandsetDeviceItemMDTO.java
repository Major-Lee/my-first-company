package com.bhu.vas.business.ds.device.mdto;

import java.util.List;

/**
 * Created by bluesand on 6/24/15.
 */
public class WifiHandsetDeviceItemMDTO {

    private String date;

    private List<WifiHandsetDeviceItemDetailMTDTO> detail;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<WifiHandsetDeviceItemDetailMTDTO> getDetail() {
        return detail;
    }

    public void setDetail(List<WifiHandsetDeviceItemDetailMTDTO> detail) {
        this.detail = detail;
    }
}
