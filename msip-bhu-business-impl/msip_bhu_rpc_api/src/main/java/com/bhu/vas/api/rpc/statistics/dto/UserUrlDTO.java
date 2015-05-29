package com.bhu.vas.api.rpc.statistics.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 5/29/15.
 */
public class UserUrlDTO implements Serializable{

    private String category;

    private int count;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private List<UserUrlSubDTO> detail;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<UserUrlSubDTO> getDetail() {
        return detail;
    }

    public void setDetail(List<UserUrlSubDTO> detail) {
        this.detail = detail;
    }
}
