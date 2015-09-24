package com.bhu.vas.api.rpc.vap.dto;

import java.io.Serializable;

/**
 * Created by bluesand on 6/1/15.
 */
@SuppressWarnings("serial")
public class VapModeUrlViewCountDTO implements Serializable {
    private long total_count;
    private long count;

    public long getTotal_count() {
        return total_count;
    }

    public void setTotal_count(long total_count) {
        this.total_count = total_count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
