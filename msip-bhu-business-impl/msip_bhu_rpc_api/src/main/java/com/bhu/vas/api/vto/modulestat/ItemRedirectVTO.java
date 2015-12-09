package com.bhu.vas.api.vto.modulestat;

import java.io.Serializable;

/**
 * Created by bluesand on 12/3/15.
 */
public class ItemRedirectVTO implements Serializable {

    private int sequence;

    private long dcount;

    private long mcount;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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
