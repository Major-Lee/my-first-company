package com.bhu.vas.api.rpc.agent.dto;

import java.io.Serializable;

/**
 * 代理商导出文件
 */
public class AgentOutputDTO implements Serializable{

    private int aid;

    private String path;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
