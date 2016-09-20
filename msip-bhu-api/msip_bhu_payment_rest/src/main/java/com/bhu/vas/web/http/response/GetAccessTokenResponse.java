package com.bhu.vas.web.http.response;


import com.bhu.vas.web.http.WxResponse;

public class GetAccessTokenResponse extends WxResponse {
    String access_token;
    int expires_in;
    //RESPONSE　CONTENT {"access_token":"M2kcFYKnunZsy01rQiyAFEBL_f7AkvCltUuZ8wwMGjoHzrkxB5p6A7pgEo03IVBcxfGI15_LLkK9bOXSO3wfBKwJD6LarfEOJ7g2JKzO4kc","expires_in":7200}

    public GetAccessTokenResponse() {
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }
}
