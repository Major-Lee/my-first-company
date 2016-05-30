package com.bhu.vas.web.http.response;

import java.util.Map;

import com.bhu.vas.web.http.WxResponse;

/**
 *  
 *  Zhangpy(mason) on 2015/7/13.
 */
public class CodeResponse extends WxResponse {

    String code_url;
    @Override
    public void getValueFromMap(Map map) {
        super.getValueFromMap(map);
        code_url=map.get("code_url").toString();
    }

    public String getCode_url() {
        return code_url;
    }

    public void setCode_url(String code_url) {
        this.code_url = code_url;
    }
}
