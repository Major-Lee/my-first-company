package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 6/25/15.
 */
public class URouterHdTimeLineVTO implements Serializable {

    private String date;

    private List detail;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List getDetail() {
        return detail;
    }

    public void setDetail(List detail) {
        this.detail = detail;
    }
}
