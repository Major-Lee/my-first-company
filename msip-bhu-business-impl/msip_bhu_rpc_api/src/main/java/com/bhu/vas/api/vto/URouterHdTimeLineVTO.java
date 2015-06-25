package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 6/25/15.
 */
public class URouterHdTimeLineVTO implements Serializable {

    private String date;

    private List<URouterHdTimeLineItemVTO> detail;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<URouterHdTimeLineItemVTO> getDetail() {
        return detail;
    }

    public void setDetail(List<URouterHdTimeLineItemVTO> detail) {
        this.detail = detail;
    }
}
