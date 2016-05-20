package com.bhu.vas.web.http.response;

import com.bhu.vas.web.http.WxResponse;

public class GenerateQCCodeUrlResponse extends WxResponse {
    String ticket;
    String url;
    int expire_seconds;


    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getExpire_seconds() {
        return expire_seconds;
    }

    public void setExpire_seconds(int expire_seconds) {
        this.expire_seconds = expire_seconds;
    }
}
