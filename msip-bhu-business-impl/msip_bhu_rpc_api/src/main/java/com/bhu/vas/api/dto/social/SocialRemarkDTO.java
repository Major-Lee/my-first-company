package com.bhu.vas.api.dto.social;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/17.
 */
@SuppressWarnings("serial")
public class SocialRemarkDTO implements Serializable {
    private String nick;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
