package com.bhu.vas.api.vto.agent;

import java.io.Serializable;

/**
 * Created by bluesand on 10/21/15.
 */
@SuppressWarnings("serial")
public class AgentFinancialUploadVTO implements Serializable {
    private int uid;
    private int aid;
    private String url;
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
