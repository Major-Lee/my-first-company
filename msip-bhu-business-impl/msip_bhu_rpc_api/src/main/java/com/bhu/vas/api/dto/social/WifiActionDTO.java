package com.bhu.vas.api.dto.social;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/7.
 */
public class WifiActionDTO implements Serializable{
    private int up;
    private int down;
    private int report;

    public int getUp() {
        return up;
    }
    public void setUp(String up) {
        this.up = Integer.parseInt(up);
    }
    public int getDown() {
        return down;
    }
    public void setDown(String down) {
        this.down = Integer.parseInt(down);
    }
    public int getReport() {
        return report;
    }
    public void setReport(String report) {
        this.report = Integer.parseInt(report);
    }

}