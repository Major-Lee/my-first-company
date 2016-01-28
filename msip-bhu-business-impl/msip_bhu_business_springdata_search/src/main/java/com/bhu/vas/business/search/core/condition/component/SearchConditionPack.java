package com.bhu.vas.business.search.core.condition.component;

import java.util.ArrayList;
import java.util.List;

import com.bhu.vas.business.search.core.exception.SearchQueryValidateException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartwork.msip.cores.helper.ArrayHelper;

/**
 * 搜索引擎的通用多组合条件定义逻辑包装类
 * 一个pack对象相当于搜索引擎中一个bool逻辑
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchConditionPack extends SearchConditionLogic implements ICondition{
	//包含的子packs
	@JsonProperty("cps")
	private List<SearchConditionPack> childSearchConditionPacks;
	//包含的条件列表
	@JsonProperty("cs")
	private List<SearchCondition> childSearchCondtions;

	@Override
	public void check() throws SearchQueryValidateException{
		if(hasChildPacks() && hasChildConditions()){
			throw new SearchQueryValidateException("SearchConditionPack data illegal");
		}
		if(!hasChildPacks() && !hasChildConditions()){
			throw new SearchQueryValidateException("SearchConditionPack data illegal");
		}
	}
	
	public boolean hasChildPacks(){
		if(childSearchConditionPacks == null || childSearchConditionPacks.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public boolean hasChildConditions(){
		if(childSearchCondtions == null || childSearchCondtions.isEmpty()) {
			return false;
		}
		return true;
	}

	public List<SearchConditionPack> getChildSearchConditionPacks() {
		return childSearchConditionPacks;
	}

	public void setChildSearchConditionPacks(
			List<SearchConditionPack> childSearchConditionPacks) {
		this.childSearchConditionPacks = childSearchConditionPacks;
	}

	public List<SearchCondition> getChildSearchCondtions() {
		return childSearchCondtions;
	}

	public void setChildSearchCondtions(List<SearchCondition> childSearchCondtions) {
		this.childSearchCondtions = childSearchCondtions;
	}
	
	public void addChildSearchCondtions(SearchCondition... conditions){
		if(conditions == null || conditions.length == 0) return;
		
		if(childSearchCondtions == null) 
			childSearchCondtions = new ArrayList<SearchCondition>();
		childSearchCondtions.addAll(ArrayHelper.toList(conditions));
	}
	
	public static SearchConditionPack builderSearchConditionPackWithPacks(SearchConditionPack... childPacks){
		return builderSearchConditionPackWithPacks(null, childPacks);
	}
	
	public static SearchConditionPack builderSearchConditionPackWithPacks(SearchConditionLogicEnumType logic, 
			SearchConditionPack... childPacks){
		SearchConditionPack pack = new SearchConditionPack();
		if(childPacks != null){
			pack.setChildSearchConditionPacks(ArrayHelper.toList(childPacks));
		}
		if(logic == null){
			logic = SearchConditionLogicEnumType.Must;
		}
		pack.setLogic(logic.getName());
		return pack;
	}
	
	public static SearchConditionPack builderSearchConditionPackWithConditions(SearchCondition... conditions){
		return builderSearchConditionPackWithConditions(null, conditions);
	}
	
	public static SearchConditionPack builderSearchConditionPackWithConditions(SearchConditionLogicEnumType logic, 
			SearchCondition... conditions){
		SearchConditionPack pack = new SearchConditionPack();
		if(conditions != null){
			pack.setChildSearchCondtions(ArrayHelper.toList(conditions));
		}
		if(logic == null){
			logic = SearchConditionLogicEnumType.Must;
		}
		pack.setLogic(logic.getName());
		return pack;
	}
}
