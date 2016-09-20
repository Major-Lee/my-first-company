package com.bhu.vas.web.http;

import java.util.Map;

/**
 * 功能：
 * 马生录(mason) on 2015/7/10.
 */
public class WxResponse {
    String responseContent;
    boolean resultSuccess=true;
    String resultErrorCode;
    String resultMessage;

    public WxResponse(){

    }

    public void setPropertyMap(Map map){
        if(!map.containsKey("return_code")){
            resultSuccess=false;
            return;
        }
        String return_code = map.get("return_code").toString().toUpperCase();
        if(!return_code.equals("SUCCESS")){
            resultSuccess=false;
            resultMessage=map.get("return_msg").toString();
            return;
        }
        getValueFromMap(map);
    }
    public void getValueFromMap(Map map){

    }
    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public boolean isResultSuccess() {
        return resultSuccess;
    }

    public void setResultSuccess(boolean resultSuccess) {
        this.resultSuccess = resultSuccess;
    }

    public String getResultErrorCode() {
        return resultErrorCode;
    }

    public void setResultErrorCode(String resultErrorCode) {
        this.resultErrorCode = resultErrorCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
