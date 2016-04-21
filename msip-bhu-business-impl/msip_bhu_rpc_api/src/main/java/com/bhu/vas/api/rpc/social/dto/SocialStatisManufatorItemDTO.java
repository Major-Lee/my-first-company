package com.bhu.vas.api.rpc.social.dto;

import java.io.Serializable;

/**
 * Created by bluesand on 3/12/16.
 */
@SuppressWarnings("serial")
public class SocialStatisManufatorItemDTO implements Serializable {

    private String name;

    private int count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
