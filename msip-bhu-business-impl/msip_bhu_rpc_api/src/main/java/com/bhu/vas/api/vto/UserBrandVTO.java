package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 5/27/15.
 */
public class UserBrandVTO implements Serializable {

    private String brand;

    private int count;

    private List<String> brandDetail;

    private List<Integer> countDetail;

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

    public List<String> getBrandDetail() {
        return brandDetail;
    }

    public void setBrandDetail(List<String> brandDetail) {
        this.brandDetail = brandDetail;
    }

    public List<Integer> getCountDetail() {
        return countDetail;
    }

    public void setCountDetail(List<Integer> countDetail) {
        this.countDetail = countDetail;
    }
}
