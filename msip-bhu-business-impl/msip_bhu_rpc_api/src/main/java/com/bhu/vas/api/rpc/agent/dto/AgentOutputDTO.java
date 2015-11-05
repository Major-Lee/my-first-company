package com.bhu.vas.api.rpc.agent.dto;

import java.io.Serializable;

/**
 * 代理商导出文件
 */
@SuppressWarnings("serial")
public class AgentOutputDTO implements Serializable{

    private int aid;

    private String path;

    private String name;

    private int success_count;

    private int fail_count;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSuccess_count() {
        return success_count;
    }

    public void setSuccess_count(int success_count) {
        this.success_count = success_count;
    }

    public int getFail_count() {
        return fail_count;
    }

    public void setFail_count(int fail_count) {
        this.fail_count = fail_count;
    }
}
