package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by newBie on 2016/3/11.
 */
public enum  SocialActionType{
    ACTION_UP("up"),
    ACTIOM_REPORT("report");

    private final String value;

    SocialActionType(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }

    public static boolean isActionType(String params){
        for (SocialActionType value : SocialActionType.values()){
            if (value.getValue().equals(params)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[]args ){
        boolean flag = SocialActionType.isActionType("down");
        System.out.println(flag);
    }
}
