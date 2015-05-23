package com.bhu.vas.api.rpc.statistics.dto;

import java.io.Serializable;

/**
 * Created by bluesand on 5/23/15.
 */
public class UserBrandStatisticsDTO implements Serializable{

    private String date;

    private String content;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
