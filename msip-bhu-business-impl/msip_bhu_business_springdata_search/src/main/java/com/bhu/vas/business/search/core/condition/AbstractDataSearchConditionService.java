package com.bhu.vas.business.search.core.condition;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import com.bhu.vas.business.search.FieldDefine;
import com.bhu.vas.business.search.SortBuilderHelper;
import com.bhu.vas.business.search.core.AbstractDataSearchService;
import com.bhu.vas.business.search.core.condition.component.SearchCondition;
import com.bhu.vas.business.search.core.condition.component.SearchConditionLogic;
import com.bhu.vas.business.search.core.condition.component.SearchConditionLogicEnumType;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPack;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPattern;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSort;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSortPattern;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionGeopointDistancePayload;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionGeopointPayload;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionGeopointRectanglePayload;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionRangePayload;
import com.bhu.vas.business.search.core.exception.SearchQueryValidateException;
import com.bhu.vas.business.search.model.AbstractDocument;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
 * 封装了condition解析逻辑service
 * @author tangzichao
 *
 * @param <MODEL>
 */
public abstract class AbstractDataSearchConditionService<MODEL extends AbstractDocument> extends AbstractDataSearchService<MODEL>{
	
	/**
	 * 使用search condition message进行搜索page
	 * @param message
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ElasticsearchIllegalArgumentException
	 */
	public Page<MODEL> searchByConditionMessage(String message, int pageNo, int pageSize) 
			throws ElasticsearchIllegalArgumentException{
		return searchByConditionMessage(getSearchConditionMessageDTO(message), pageNo, pageSize);
	}
	/**
	 * 使用search condition message进行搜索list
	 * @param message
	 * @return
	 * @throws ElasticsearchIllegalArgumentException
	 */
	public List<MODEL> searchByConditionMessage(String message) 
			throws ElasticsearchIllegalArgumentException{
		return searchByConditionMessage(getSearchConditionMessageDTO(message));
	}

	/**
	 * 转换message为SearchConditionMessage对象
	 * @param message
	 * @return
	 */
	public SearchConditionMessage getSearchConditionMessageDTO(String message){
		if(!StringUtils.isEmpty(message)){
			return JsonHelper.getDTO(message, SearchConditionMessage.class);
		}
		return null;
	}
	
	/**
	 * 使用search condition message进行搜索page
	 * @param searchConditionMessage
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ElasticsearchIllegalArgumentException
	 */
	public Page<MODEL> searchByConditionMessage(SearchConditionMessage searchConditionMessage, int pageNo, int pageSize) 
			throws ElasticsearchIllegalArgumentException{
		return getElasticsearchTemplate().queryForPage(builderSearchQueryByConditionMessage(searchConditionMessage, 
				pageNo, pageSize), entityClass);
	}
	/**
	 * 使用search condition message进行搜索list
	 * @param searchConditionMessage
	 * @return
	 * @throws ElasticsearchIllegalArgumentException
	 */
	public List<MODEL> searchByConditionMessage(SearchConditionMessage searchConditionMessage) 
			throws ElasticsearchIllegalArgumentException{
		return getElasticsearchTemplate().queryForList(builderSearchQueryByConditionMessage(searchConditionMessage), 
				entityClass);
	}
	
	private SearchQuery builderSearchQueryByConditionMessage(SearchConditionMessage searchConditionMessage, 
			int pageNo, int pageSize){
		return builderNativeSearchQueryByConditionMessage(searchConditionMessage, pageNo, pageSize).build();
	}
	
	private SearchQuery builderSearchQueryByConditionMessage(SearchConditionMessage searchConditionMessage){
		return builderNativeSearchQueryByConditionMessage(searchConditionMessage).build();
	}
	
	private NativeSearchQueryBuilder builderNativeSearchQueryByConditionMessage(SearchConditionMessage searchConditionMessage){
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		SearchType searchType = SearchType.QUERY_THEN_FETCH;
		
		if(searchConditionMessage != null){
			//解析condition返回原生搜索QueryBuilder
			//parserCondition(nativeSearchQueryBuilder, searchConditionMessage.getSearchConditions());
			parser(nativeSearchQueryBuilder, searchConditionMessage);
	    	searchType = SearchType.fromId(searchConditionMessage.getSearchType());
		}else{
			nativeSearchQueryBuilder.withFilter(FilterBuilders.matchAllFilter());
		}
		
		//使用es原生方式进行查询
        nativeSearchQueryBuilder.withSearchType(searchType);
//        		.withPageable(new PageRequest(pageNo, pageSize));
        return nativeSearchQueryBuilder;
	}
	
	private NativeSearchQueryBuilder builderNativeSearchQueryByConditionMessage(SearchConditionMessage searchConditionMessage, 
			int pageNo, int pageSize){
		NativeSearchQueryBuilder nativeSearchQueryBuilder = builderNativeSearchQueryByConditionMessage(searchConditionMessage);
		nativeSearchQueryBuilder.withPageable(new PageRequest(pageNo, pageSize));
        return nativeSearchQueryBuilder;
	}
	
	private NativeSearchQueryBuilder builderNativeSearchQueryByConditionMessage(SearchConditionMessage searchConditionMessage, 
			String indices, String types, int pageNo, int pageSize){
		NativeSearchQueryBuilder nativeSearchQueryBuilder = builderNativeSearchQueryByConditionMessage(
				searchConditionMessage, pageNo, pageSize);
		nativeSearchQueryBuilder
				.withIndices(indices)
				.withTypes(types);
		return nativeSearchQueryBuilder;
	}
	
	/**
	 * 解析搜索对象成为ES查询对象
	 * @param nativeSearchQueryBuilder
	 * @param searchConditionMessage
	 */
	private void parser(NativeSearchQueryBuilder nativeSearchQueryBuilder, SearchConditionMessage searchConditionMessage){
		FilterBuilder filterBuilder = null;
		List<SortBuilder> sortBuilders = null;
		try{
			//parser search condition packs
			List<SearchConditionPack> packs = searchConditionMessage.getSearchConditionPacks();
			if(packs == null || packs.isEmpty()){
				filterBuilder = FilterBuilders.matchAllFilter();
			}else{
				filterBuilder = parserSearchPacks(FilterBuilders.boolFilter(), packs);
			}
			//parser search sort
			sortBuilders = parserSearchSorts(searchConditionMessage.getSearchConditionSorts());
		}catch(Exception ex){
			ex.printStackTrace();
			filterBuilder = FilterBuilders.matchAllFilter();
		}finally{
			nativeSearchQueryBuilder.withFilter(filterBuilder);
			if(sortBuilders != null && !sortBuilders.isEmpty()){
				for(SortBuilder sortBuilder : sortBuilders){
					nativeSearchQueryBuilder.withSort(sortBuilder);
				}
			}
		}
	}
	/**
	 * 解析搜索条件的包装类实现
	 * @param boolFilter
	 * @param packs
	 * @return
	 * @throws SearchQueryValidateException
	 */
	private FilterBuilder parserSearchPacks(BoolFilterBuilder boolFilter, List<SearchConditionPack> packs)
			throws SearchQueryValidateException{
		if(packs != null && !packs.isEmpty()){
			for(SearchConditionPack pack : packs){
				BoolFilterBuilder childBoolFilter = FilterBuilders.boolFilter();
				parserSearchLogic(boolFilter, childBoolFilter, pack);
				if(pack.hasChildPacks()){
					parserSearchPacks(childBoolFilter, pack.getChildSearchConditionPacks());
				}else if(pack.hasChildConditions()){
					parserSearchConditions(childBoolFilter, pack.getChildSearchCondtions());
				}
			}
		}
		return boolFilter;
	}
	
	/**
	 * 解析搜索条件的排序实现
	 * @param sorts
	 * @return
	 * @throws SearchQueryValidateException
	 */
	private List<SortBuilder> parserSearchSorts(List<SearchConditionSort> sorts)
			throws SearchQueryValidateException{
		List<SortBuilder> sortBuilders = null;
		if(sorts != null && !sorts.isEmpty()){
			sortBuilders = new ArrayList<SortBuilder>();
			for(SearchConditionSort sort : sorts){
				SortBuilder sortBuilder = parserSortCondition(sort);
				if(sortBuilder != null){
					sortBuilders.add(sortBuilder);
				}
			}
		}
		return sortBuilders;
	}
	
	/**
	 * 解析搜索条件的逻辑实现
	 * @param boolFilter
	 * @param childFilter
	 * @param logic
	 * @throws SearchQueryValidateException
	 */
	private void parserSearchLogic(BoolFilterBuilder boolFilter, FilterBuilder childFilter, SearchConditionLogic logic)
			throws SearchQueryValidateException{
//		logic.check();
		
		String logic_name = logic.getLogic();
		if(StringUtils.isEmpty(logic_name))
			throw new SearchQueryValidateException(String.format("ParserSearchLogic logic empty [%s]", logic_name));
		
		SearchConditionLogicEnumType logicType = SearchConditionLogicEnumType.fromName(logic_name);
		if(logicType == null)
			throw new SearchQueryValidateException(String.format("ParserSearchLogic logic illegal [%s]", logic_name));
		
		switch(logicType){
			case Must:
				boolFilter.must(childFilter);
				break;
			case MustNot:
				boolFilter.mustNot(childFilter);
				break;
			case Should:
				boolFilter.should(childFilter);
				break;
			default:
				throw new SearchQueryValidateException(String.format("ParserSearchLogic logic unsupport [%s]", logic_name));
		}
	}
	
	/**
	 * 解析condition条件为ES Search搜索对象
	 * @param conditions
	 * @return FilterBuilder
	 * @throws SearchQueryValidateException 
	 */
	private void parserSearchConditions(BoolFilterBuilder boolFilter, List<SearchCondition> conditions) 
			throws SearchQueryValidateException{
		if(conditions != null && !conditions.isEmpty()) {
			for(SearchCondition condition : conditions){
				condition.check();
				
				FieldDefine fieldDefine = getFieldByName(condition.getKey());
				if(fieldDefine != null){
					//判断是否是搜索匹配条件
					SearchConditionPattern conditionPattern = SearchConditionPattern.getByPattern(condition.getPattern());
					if(SearchConditionPattern.Unkown != conditionPattern){
						//解析condition搜索匹配条件
						FilterBuilder conditionFilter = parserSearchCondition(condition, conditionPattern, fieldDefine);
						if(conditionFilter != null){
							parserSearchLogic(boolFilter, conditionFilter, condition);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 解析condition的排序条件
	 * @param conditionSort
	 * @return 返回SortBuilder
	 * @throws SearchQueryValidateException 
	 */
	private SortBuilder parserSortCondition(SearchConditionSort conditionSort) throws SearchQueryValidateException{
		if(conditionSort == null) return null;
		conditionSort.check();
	
		SortBuilder conditionSortBuilder = null;
		FieldDefine fieldDefine = getFieldByName(conditionSort.getKey());
		if(fieldDefine != null){
			String fieldName = fieldDefine.getName();
			//判断是否是排序条件
			SearchConditionSortPattern conditionSortPattern = SearchConditionSortPattern.getByPattern(conditionSort.getPattern());
			if(SearchConditionSortPattern.Unkown != conditionSortPattern){
				String conditionPayload = conditionSort.getPayload();
				
				int method = conditionSortPattern.getMethod();
				String sortFieldName = fieldDefine.getScore_name() != null ? fieldDefine.getScore_name() : fieldName;
				String order = conditionSort.getOrder();
				SortOrder sortOrder = SortOrder.ASC;
				if(StringUtils.isNotEmpty(order)){
					sortOrder = SortOrder.valueOf(order);
				}
				
				switch(method){
					//正常排序
					case SearchConditionSortPattern.Method_Sort:
						conditionSortBuilder = parserMethodSort(sortFieldName, sortOrder);
						break;
					//根据提供的geopoint当原点,按与原点的距离排序
					case SearchConditionSortPattern.Method_DistanceSort:
						conditionSortBuilder = parserMethodSortDistance(sortFieldName, conditionPayload, sortOrder);
						break;
					default:
						break;
				}
			}
		}
		return conditionSortBuilder;
	}
	
	/**
	 * 解析condition的搜索匹配条件
	 * @param condition
	 * @param fieldDefine
	 * @param boolFilter
	 * @return 返回FilterBuilder
	 */
	private FilterBuilder parserSearchCondition(SearchCondition condition, SearchConditionPattern conditionPattern, FieldDefine fieldDefine){
		String fieldName = fieldDefine.getName();
		String conditionPayload = condition.getPayload();
		
		FilterBuilder conditionFilterBuilder = null;
		int method = conditionPattern.getMethod();
		int method_ext = conditionPattern.getMethod_ext();
		
		switch(method){
			case SearchConditionPattern.Method_Wildcard:
				conditionFilterBuilder = parserMethodWildcard(fieldName, conditionPayload);
				break;
			case SearchConditionPattern.Method_Term:
				conditionFilterBuilder = parserMethodTerm(fieldName, conditionPayload);
				break;
			case SearchConditionPattern.Method_Prefix:
				conditionFilterBuilder = parserMethodPrefix(fieldName, conditionPayload);
				break;
			case SearchConditionPattern.Method_Range:
				conditionFilterBuilder = parserMethodRange(fieldName, conditionPayload, method_ext);
				break;
			case SearchConditionPattern.Method_String:
				conditionFilterBuilder = parserMethodString(fieldName, conditionPayload);
				break;
			case SearchConditionPattern.Method_Missing:
				conditionFilterBuilder = parserMethodMissing(fieldName);
				break;
			case SearchConditionPattern.Method_Existing:
				conditionFilterBuilder = parserMethodExisting(fieldName);
				break;
			case SearchConditionPattern.Method_Geopoint:
				conditionFilterBuilder = parserMethodGeopoint(fieldName, conditionPayload, method_ext);
				break;
			default:
				break;
		}
		return conditionFilterBuilder;
	}
	
	public boolean validateConditionPayloadVaild(String conditionPayload){
		if(StringUtils.isEmpty(conditionPayload)) return false;
		return true;
	}
	
	
	public void iteratorAll(String indices, String types, String message, IteratorNotify<Page<MODEL>> notify){
		SearchConditionMessage searchConditionMessage = null;
		if(!StringUtils.isEmpty(message)){
			searchConditionMessage = JsonHelper.getDTO(message, SearchConditionMessage.class);
		}
		
		SearchQuery searchQuery = builderNativeSearchQueryByConditionMessage(searchConditionMessage, indices, types, 0, 500).build();

		String scrollId = getElasticsearchTemplate().scan(searchQuery, 60000, false);
		boolean hasRecords = true;
		while (hasRecords) {
			Page<MODEL> page = getElasticsearchTemplate().scroll(scrollId, 60000, entityClass);
			if (page.hasContent()) {
				notify.notifyComming(page);
			} else {
				hasRecords = false;
			}
		}
	}
	
	/****************************  Method Parser **********************************/
	//Method Parser是根据不同的method解析成不同的ES查询或排序对象
	
	/**
	 * 模糊匹配方式转换FilterBuilder
	 * @param fieldName
	 * @param conditionPayload
	 * @return 
	 */
	public FilterBuilder parserMethodWildcard(String fieldName, String conditionPayload){
		if(validateConditionPayloadVaild(conditionPayload)){
			return FilterBuilders.queryFilter(QueryBuilders.wildcardQuery(fieldName, 
					StringHelper.ASTERISK_STRING_GAP.concat(conditionPayload).
					concat(StringHelper.ASTERISK_STRING_GAP)));
		}
		return null;
	}
	
	/**
	 * 项匹配方式转换FilterBuilder
	 * @param fieldName
	 * @param conditionPayload
	 * @return
	 */
	public FilterBuilder parserMethodTerm(String fieldName, String conditionPayload){
		if(validateConditionPayloadVaild(conditionPayload)){
			return FilterBuilders.termFilter(fieldName, conditionPayload);
		}
		return null;
	}
	
	/**
	 * 前缀匹配方式转换FilterBuilder
	 * @param fieldName
	 * @param conditionPayload
	 * @return
	 */
	public FilterBuilder parserMethodPrefix(String fieldName, String conditionPayload){
		if(validateConditionPayloadVaild(conditionPayload)){
			return FilterBuilders.prefixFilter(fieldName, conditionPayload);
		}
		return null;
	}
	
	/**
	 * 范围匹配方式转换FilterBuilder
	 * 范围匹配具体有分为以下几种:
	 * 1:大于条件和小于条件同时存在
	   2:大于条件
	   3:大于等于条件
	   4:小于条件
	   5:小于等于条件
	 * @param fieldName
	 * @param conditionPayload
	 * @param method_ext
	 * @return
	 */
	public FilterBuilder parserMethodRange(String fieldName, String conditionPayload, int method_ext){
		FilterBuilder rangeFilterBuilder = null;
		if(validateConditionPayloadVaild(conditionPayload)){
			SearchConditionRangePayload rangePayload = JsonHelper.getDTO(conditionPayload, SearchConditionRangePayload.class);
			if(rangePayload != null){
				//Range范围搜索的以下几种情况
				switch(method_ext){
					case SearchConditionPattern.MethodExt_Range_Between:
						rangeFilterBuilder = FilterBuilders.rangeFilter(fieldName).
							gt(rangePayload.getGreaterThanValue()).lt(rangePayload.getLessThanValue());
						break;
					case SearchConditionPattern.MethodExt_Range_GreaterThan:
						rangeFilterBuilder = FilterBuilders.rangeFilter(fieldName).
							gt(rangePayload.getGreaterThanValue());
						break;
					case SearchConditionPattern.MethodExt_Range_GreaterThanEqual:
						rangeFilterBuilder = FilterBuilders.rangeFilter(fieldName).
							gte(rangePayload.getGreaterThanValue());
						break;
					case SearchConditionPattern.MethodExt_Range_LessThan:
						rangeFilterBuilder = FilterBuilders.rangeFilter(fieldName).
							lt(rangePayload.getLessThanValue());
						break;
					case SearchConditionPattern.MethodExt_Range_LessThanEqual:
						rangeFilterBuilder = FilterBuilders.rangeFilter(fieldName).
							lte(rangePayload.getLessThanValue());
						break;
					default:
						break;
				}
			}
		}
		return rangeFilterBuilder;
	}

	/**
	 * 字符串匹配方式转换FilterBuilder
	 * @param fieldName
	 * @param conditionPayload
	 * @return
	 */
	public FilterBuilder parserMethodString(String fieldName, String conditionPayload){
		if(validateConditionPayloadVaild(conditionPayload)){
			//return FilterBuilders.queryFilter(QueryBuilders.queryStringQuery(conditionPayload).field(fieldName));
			return FilterBuilders.queryFilter(QueryBuilders.matchQuery(fieldName, conditionPayload));
		}
		return null;
	}
	
	/**
	 * Missing匹配方式转换FilterBuilder
	 * @param fieldName
	 * @param conditionPayload
	 * @return
	 */
	public FilterBuilder parserMethodMissing(String fieldName){
		return FilterBuilders.missingFilter(fieldName);
	}
	
	
	/**
	 * Existing匹配方式转换FilterBuilder
	 * @param fieldName
	 * @param conditionPayload
	 * @return
	 */
	public FilterBuilder parserMethodExisting(String fieldName){
		return FilterBuilders.existsFilter(fieldName);
	}
	
	/**
	 * Geopoint匹配方式转换FilterBuilder
	 * Geopoint匹配具体有分为以下几种:
	 * 1:坐标点为圆心半径内匹配
	   2:左上右下2个坐标点构成的长方形内匹配
	   TODO:还可以扩展多边形等等 
	 * @param fieldName
	 * @param conditionPayload
	 * @param method_ext
	 * @return
	 */
	public FilterBuilder parserMethodGeopoint(String fieldName, String conditionPayload, int method_ext){
		FilterBuilder geopointFilterBuilder = null;
		if(validateConditionPayloadVaild(conditionPayload)){
			//Geopoint坐标搜索的以下几种情况
			switch(method_ext){
				case SearchConditionPattern.MethodExt_GeopointDistance:
					SearchConditionGeopointDistancePayload geopointDistancePayload = JsonHelper.getDTO(
							conditionPayload, SearchConditionGeopointDistancePayload.class);
					if(geopointDistancePayload != null){
						geopointFilterBuilder = FilterBuilders.geoDistanceFilter(fieldName)
				                .distance(geopointDistancePayload.getDistance())
				                .point(geopointDistancePayload.getLat(), geopointDistancePayload.getLon());
					}
					break;
				case SearchConditionPattern.MethodExt_GeopointRectangle:
					SearchConditionGeopointRectanglePayload geopointRectanglePayload = JsonHelper.getDTO(
							conditionPayload, SearchConditionGeopointRectanglePayload.class);
					if(geopointRectanglePayload != null){
						geopointFilterBuilder = FilterBuilders.geoBoundingBoxFilter(fieldName)
								.topLeft(geopointRectanglePayload.getTopLeft_lat(), geopointRectanglePayload.getTopLeft_lon())
								.bottomRight(geopointRectanglePayload.getBottomRight_lat(), geopointRectanglePayload.getBottomRight_lon());
					}
					break;
				default:
					break;
			}
		}
		return geopointFilterBuilder;
	}
	
	/**
	 * 正常排序方式转换SortBuilder
	 * @param sortFieldName
	 * @param order
	 * @return
	 */
	public SortBuilder parserMethodSort(String sortFieldName, SortOrder sortOrder){
		return SortBuilderHelper.builderSort(sortFieldName, sortOrder);
	}
	
	/**
	 * GeopointDistance排序方式转换SortBuilder
	 * @param sortFieldName
	 * @param conditionPayload
	 * @param order
	 * @return
	 */
	public SortBuilder parserMethodSortDistance(String sortFieldName, String conditionPayload, SortOrder sortOrder){
		if(validateConditionPayloadVaild(conditionPayload)){
			SearchConditionGeopointPayload geopointPayload = JsonHelper.getDTO(conditionPayload,
					SearchConditionGeopointPayload.class);
			if(geopointPayload != null){
				return SortBuilderHelper.builderDistanceSort(sortFieldName, 
							geopointPayload.getLat(), geopointPayload.getLon(), sortOrder);
			}
		}
		return null;
	}


	
	public abstract FieldDefine getFieldByName(String fieldName);
}
