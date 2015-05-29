package com.bhu.vas.api.rpc.statistics.dto;

import java.io.Serializable;

/**
 * Created by bluesand on 5/29/15.
 */
public class UserUrlSubDTO implements Serializable {

    private String category;

    private int count;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
