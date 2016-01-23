package com.bhu.vas.business.search.core.condition.component;

import java.util.HashMap;
import java.util.Map;

public enum SearchConditionLogicEnumType {
	Must("must","必须满足该条件"),
    MustNot("must_not", "必须不满足该条件"),
    Should("should", "可以满足也不可以不满足"),
    ;
    private String name;//命名
    private String desc;//描述
    static Map<String, SearchConditionLogicEnumType> allLogics;
    
    private SearchConditionLogicEnumType(String name, String desc){
        this.name = name;
        this.desc = desc;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    static {
    	allLogics = new HashMap<String,SearchConditionLogicEnumType>();
    	SearchConditionLogicEnumType[] logics = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
        for (SearchConditionLogicEnumType logic : logics){
        	allLogics.put(logic.getName(), logic);
        }
    }
    public static SearchConditionLogicEnumType fromName(String name){
    	SearchConditionLogicEnumType logicType = allLogics.get(name);
        return logicType;
    }
}
