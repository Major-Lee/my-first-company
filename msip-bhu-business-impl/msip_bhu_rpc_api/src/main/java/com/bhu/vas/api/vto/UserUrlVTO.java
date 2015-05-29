package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 5/27/15.
 */
public class UserUrlVTO implements Serializable {

    private String category;

    private int count;

    private List<String> categoryDetail;

    private List<Integer> countDetail;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getCategoryDetail() {
        return categoryDetail;
    }

    public void setCategoryDetail(List<String> categoryDetail) {
        this.categoryDetail = categoryDetail;
    }

    public List<Integer> getCountDetail() {
        return countDetail;
    }

    public void setCountDetail(List<Integer> countDetail) {
        this.countDetail = countDetail;
    }
}
