package com.bhu.vas.api.vto.agent;

import java.io.Serializable;

/**
 * Created by bluesand on 9/29/15.
 */
public class AgentUploadVTO implements Serializable{
    private int uid;
    private int aid;
    private String filename;

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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
