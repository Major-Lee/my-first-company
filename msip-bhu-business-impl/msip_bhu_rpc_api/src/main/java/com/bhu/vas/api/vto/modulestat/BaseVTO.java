package com.bhu.vas.api.vto.modulestat;

import java.io.Serializable;

/**
 * Created by bluesand on 12/11/15.
 */
@SuppressWarnings("serial")
public class BaseVTO implements Serializable{

    private int type;

    private String desc;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
