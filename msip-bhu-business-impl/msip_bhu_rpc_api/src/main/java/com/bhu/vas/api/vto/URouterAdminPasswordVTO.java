package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * Created by bluesand on 5/19/15.
 */
@SuppressWarnings("serial")
public class URouterAdminPasswordVTO implements Serializable {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
