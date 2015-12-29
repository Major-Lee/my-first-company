package com.bhu.vas.business.search.service;

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

import com.bhu.vas.api.dto.search.condition.SearchCondition;
import com.bhu.vas.api.dto.search.condition.SearchConditionMessage;
import com.bhu.vas.api.dto.search.condition.SearchConditionPattern;
import com.bhu.vas.api.dto.search.condition.SearchConditionSortPattern;
import com.bhu.vas.api.dto.search.condition.payload.SearchConditionGeopointDistancePayload;
import com.bhu.vas.api.dto.search.condition.payload.SearchConditionGeopointPayload;
import com.bhu.vas.api.dto.search.condition.payload.SearchConditionGeopointRectanglePayload;
import com.bhu.vas.api.dto.search.condition.payload.SearchConditionRangePayload;
import com.bhu.vas.business.search.FieldDefine;
import com.bhu.vas.business.search.SortBuilderHelper;
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
	 * 使用search condition message进行搜索
	 */
	public Page<MODEL> searchByConditionMessage(String message, int pageNo, int pageSize) 
			throws ElasticsearchIllegalArgumentException{
		
		SearchConditionMessage searchConditionMessage = null;
		if(!StringUtils.isEmpty(message)){
			searchConditionMessage = JsonHelper.getDTO(message, SearchConditionMessage.class);
		}
		return searchByConditionMessage(searchConditionMessage, pageNo, pageSize);
	}

	/**
	 * 使用search condition message进行搜索
	 */
	public Page<MODEL> searchByConditionMessage(SearchConditionMessage searchConditionMessage, int pageNo, int pageSize) 
			throws ElasticsearchIllegalArgumentException{
/*		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		SearchType searchType = SearchType.QUERY_THEN_FETCH;
		
		if(searchConditionMessage != null){
			//解析condition返回原生搜索QueryBuilder
			parserCondition(nativeSearchQueryBuilder, searchConditionMessage.getSearchConditions());
	    	searchType = SearchType.fromId(searchConditionMessage.getSearchType());
		}else{
			nativeSearchQueryBuilder.withFilter(FilterBuilders.matchAllFilter());
		}
		
		//使用es原生方式进行查询
        SearchQuery searchQuery = nativeSearchQueryBuilder
        		.withSearchType(searchType)
        		.withPageable(new PageRequest(pageNo, pageSize))
        		.build();
        return getElasticsearchTemplate().queryForPage(searchQuery, entityClass);*/
		return getElasticsearchTemplate().queryForPage(builderSearchQueryByConditionMessage(searchConditionMessage, 
				pageNo, pageSize), entityClass);
	}
	
	private SearchQuery builderSearchQueryByConditionMessage(SearchConditionMessage searchConditionMessage, 
			int pageNo, int pageSize){
		return builderNativeSearchQueryByConditionMessage(searchConditionMessage, pageNo, pageSize).build();
	}
	
	private NativeSearchQueryBuilder builderNativeSearchQueryByConditionMessage(SearchConditionMessage searchConditionMessage, 
			int pageNo, int pageSize){
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		SearchType searchType = SearchType.QUERY_THEN_FETCH;
		
		if(searchConditionMessage != null){
			//解析condition返回原生搜索QueryBuilder
			parserCondition(nativeSearchQueryBuilder, searchConditionMessage.getSearchConditions());
	    	searchType = SearchType.fromId(searchConditionMessage.getSearchType());
		}else{
			nativeSearchQueryBuilder.withFilter(FilterBuilders.matchAllFilter());
		}
		
		//使用es原生方式进行查询
        nativeSearchQueryBuilder
        		.withSearchType(searchType)
        		.withPageable(new PageRequest(pageNo, pageSize));
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
	 * 解析condition条件为ES Search搜索对象
	 * @param conditions
	 * @return FilterBuilder
	 */
	private void parserCondition(NativeSearchQueryBuilder nativeSearchQueryBuilder, List<SearchCondition> conditions){
		FilterBuilder filter = null;
		try{
			if(conditions != null && !conditions.isEmpty()) {
				//以Boolfilter汇总条件列表 暂不初始化 当没有匹配条件存在的时候 会使用全匹配代替
				BoolFilterBuilder boolFilter = null;
				for(SearchCondition condition : conditions){
					if(condition == null || StringUtils.isEmpty(condition.getKey()) || 
							StringUtils.isEmpty(condition.getPattern())) continue;
				
					FieldDefine fieldDefine = getFieldByName(condition.getKey());
					if(fieldDefine != null){
						//判断是否是搜索匹配条件
						SearchConditionPattern conditionPattern = SearchConditionPattern.getByPattern(condition.getPattern());
						if(SearchConditionPattern.Unkown != conditionPattern){
							//解析condition搜索匹配条件
							FilterBuilder conditionFilterBuilder = parserSearchCondition(condition, conditionPattern, fieldDefine);
							if(conditionFilterBuilder != null){
								if(boolFilter == null) boolFilter = FilterBuilders.boolFilter();
								parserSearchConditionRelationship(conditionFilterBuilder, conditionPattern, boolFilter);
							}
						}else{
							//判断是否是排序条件
							SearchConditionSortPattern conditionSortPattern = SearchConditionSortPattern.getByPattern(condition.getPattern());
							if(SearchConditionSortPattern.Unkown != conditionSortPattern){
								//解析condition排序条件
								SortBuilder conditionSortBuilder = parserSortCondition(condition, conditionSortPattern, fieldDefine);
								if(conditionSortBuilder != null){
									nativeSearchQueryBuilder.withSort(conditionSortBuilder);
								}
							}
						}
					}
				}
				filter = boolFilter;
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		
		if(filter == null) {
			filter = FilterBuilders.matchAllFilter();
		}
		nativeSearchQueryBuilder.withFilter(filter);
//		
//		return nativeSearchQueryBuilder;
	}
	
	/**
	 * 解析condition的排序条件
	 * @param condition
	 * @param fieldDefine
	 * @param nativeSearchQueryBuilder
	 * @return 返回SortBuilder
	 */
	private SortBuilder parserSortCondition(SearchCondition condition, SearchConditionSortPattern conditionSortPattern, FieldDefine fieldDefine){
		String fieldName = fieldDefine.getName();
		String conditionPayload = condition.getPayload();

		SortBuilder conditionSortBuilder = null;
		int method = conditionSortPattern.getMethod();
		String sortFieldName = fieldDefine.getScore_name() != null ? fieldDefine.getScore_name() : fieldName;
		switch(method){
			//正常排序
			case SearchConditionSortPattern.Method_Sort:
				conditionSortBuilder = parserMethodSort(sortFieldName, conditionSortPattern);
				break;
			//根据提供的geopoint当原点,按与原点的距离排序
			case SearchConditionSortPattern.Method_DistanceSort:
				conditionSortBuilder = parserMethodSortDistance(sortFieldName, conditionSortPattern, conditionPayload);
				break;
			default:
				break;
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
	
	/**
	 * 解析condition搜索条件之间的逻辑匹配关系
	 * @param conditionPattern
	 * @param conditionFilterBuilder
	 * @param boolFilter
	 */
	private void parserSearchConditionRelationship(FilterBuilder conditionFilterBuilder, 
			SearchConditionPattern conditionPattern, BoolFilterBuilder boolFilter){
		int necessity = conditionPattern.getNecessity();
		switch(necessity){
			case SearchConditionPattern.Necessity_Must:
				boolFilter.must(conditionFilterBuilder);
				break;
			case SearchConditionPattern.Necessity_MustNot:
				boolFilter.mustNot(conditionFilterBuilder);
				break;
			case SearchConditionPattern.Necessity_Should:
				boolFilter.should(conditionFilterBuilder);
				break;
			default:
				break;
		}
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
	 * @param conditionSortPattern
	 * @return
	 */
	public SortBuilder parserMethodSort(String sortFieldName, SearchConditionSortPattern conditionSortPattern){
		return SortBuilderHelper.builderSort(sortFieldName, 
					conditionSortPattern.isModeAsc() ? SortOrder.ASC : SortOrder.DESC);
	}
	
	/**
	 * GeopointDistance排序方式转换SortBuilder
	 * @param sortFieldName
	 * @param conditionSortPattern
	 * @param conditionPayload
	 * @return
	 */
	public SortBuilder parserMethodSortDistance(String sortFieldName, SearchConditionSortPattern conditionSortPattern, String conditionPayload){
		if(validateConditionPayloadVaild(conditionPayload)){
			SearchConditionGeopointPayload geopointPayload = JsonHelper.getDTO(conditionPayload,
					SearchConditionGeopointPayload.class);
			if(geopointPayload != null){
				return SortBuilderHelper.builderDistanceSort(sortFieldName, 
							geopointPayload.getLat(), geopointPayload.getLon(), 
							conditionSortPattern.isModeAsc() ? SortOrder.ASC : SortOrder.DESC);
			}
		}
		return null;
	}


	
	public abstract FieldDefine getFieldByName(String fieldName);
}
