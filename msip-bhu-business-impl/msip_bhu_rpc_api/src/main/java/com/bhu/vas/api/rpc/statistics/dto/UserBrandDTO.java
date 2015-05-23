package com.bhu.vas.api.rpc.statistics.dto;

import java.io.Serializable;

/**
 * Created by bluesand on 5/23/15.
 */
public class UserBrandDTO implements Serializable {

    private String brand;

    private int count;

    private String ratio;


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }
}
