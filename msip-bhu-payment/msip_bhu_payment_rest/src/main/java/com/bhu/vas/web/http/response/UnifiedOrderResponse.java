package com.bhu.vas.web.http.response;

import java.util.Map;

import com.bhu.vas.web.http.WxResponse;

public class UnifiedOrderResponse extends WxResponse {

    String code_url;
    String nonce_str;
    String sign;
    String notifyUrl;
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

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
