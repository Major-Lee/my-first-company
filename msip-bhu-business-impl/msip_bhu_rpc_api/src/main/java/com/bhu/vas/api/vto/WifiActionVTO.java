package com.bhu.vas.api.vto;

import java.io.Serializable;

public class WifiActionVTO implements Serializable {
    private String up;
    private String down;
    private String report;
    
    public String getUp() {
        return up;
    }
    public void setUp(String up) {
        this.up = up;
    }
    public String getDown() {
        return down;
    }
    public void setDown(String down) {
        this.down = down;
    }
    public String getReport() {
        return report;
    }
    public void setReport(String report) {
        this.report = report;
    }
    
}
