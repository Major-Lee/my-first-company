package com.bhu.vas.api.vto.agent;

import java.io.Serializable;

/**
 * Created by bluesand on 10/20/15.
 */
public class WarehouseManagerVTO implements Serializable {

    private long id;

    private String n;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }
}
