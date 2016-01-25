package com.bhu.vas.business.search.core.condition.component;

import java.io.Serializable;


/**
 * 搜索条件的与或非的逻辑控制类
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchConditionLogic implements Serializable{
	//逻辑与或非
	private String logic = SearchConditionLogicEnumType.Must.getName();
	
	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}
}
