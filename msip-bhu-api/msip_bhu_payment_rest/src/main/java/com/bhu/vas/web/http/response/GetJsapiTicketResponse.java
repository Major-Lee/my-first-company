package com.bhu.vas.web.http.response;


import com.bhu.vas.web.http.WxResponse;

public class GetJsapiTicketResponse extends WxResponse {
    String errcode;
    String errmsg;
    String ticket;
    int expires_in;

    public GetJsapiTicketResponse() {
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
}
