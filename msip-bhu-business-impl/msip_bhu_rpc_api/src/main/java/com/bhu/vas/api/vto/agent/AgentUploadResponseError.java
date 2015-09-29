package com.bhu.vas.api.vto.agent;

import com.smartwork.msip.jdo.Response;

import java.io.Serializable;

/**
 * Created by bluesand on 9/29/15.
 */
public class AgentUploadResponseError extends Response implements Serializable {

    public AgentUploadResponseError(boolean success, String message, String result) {
        super(success, message);
        this.result = result;
    }

    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
