package com.bhu.vas.api.vto.agent;

import com.smartwork.msip.jdo.Response;

import java.io.Serializable;

/**
 * Created by bluesand on 9/29/15.
 */
@SuppressWarnings("serial")
public class AgentUploadResponseError extends Response implements Serializable {

    public AgentUploadResponseError(boolean success, String message, Object result) {
        super(success, message);
        this.result = result;
    }

    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
