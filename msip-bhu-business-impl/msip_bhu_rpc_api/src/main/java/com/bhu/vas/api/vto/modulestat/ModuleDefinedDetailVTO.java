package com.bhu.vas.api.vto.modulestat;

import java.io.Serializable;

/**
 * Created by bluesand on 12/9/15.
 */
public class ModuleDefinedDetailVTO implements Serializable {

    private int type;

    private String desc;

    private long dcount;

    private long mcount;

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

    public long getDcount() {
        return dcount;
    }

    public void setDcount(long dcount) {
        this.dcount = dcount;
    }

    public long getMcount() {
        return mcount;
    }

    public void setMcount(long mcount) {
        this.mcount = mcount;
    }
}
