package com.bhu.vas.business.search.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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

/**
 * 封装了condition解析逻辑service
 * @author tangzichao
 *
 * @param <MODEL>
 */
public abstract class AbstractDataSearchConditionService<MODEL extends AbstractDocument> extends AbstractDataSearchService<MODEL>{
	
	/**
	 * 使用conditions进行动态多组合条件查询
	 */
	public Page<MODEL> searchByCondition(List<SearchCondition> conditions, int page, int pagesize) {
		//解析condition返回原生搜索QueryBuilder
		NativeSearchQueryBuilder nativeSearchQueryBuilder = parserCondition(conditions);
		//使用es原生方式进行查询
        SearchQuery searchQuery = nativeSearchQueryBuilder.withPageable(new PageRequest(page,pagesize)).build();
        return getElasticsearchTemplate().queryForPage(searchQuery, entityClass);
	}
	
	/**
	 * 解析condition条件为ES Search搜索对象
	 * @param conditions
	 * @return 返回原生搜索QueryBuilder
	 */
	private NativeSearchQueryBuilder parserCondition(List<SearchCondition> conditions){
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		FilterBuilder filter = null;
		try{
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
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		
		if(filter == null) {
			filter = FilterBuilders.matchAllFilter();
		}
		nativeSearchQueryBuilder.withFilter(filter);
		
		return nativeSearchQueryBuilder;
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
				conditionSortBuilder = SortBuilderHelper.builderSort(sortFieldName, 
						conditionSortPattern.isModeAsc() ? SortOrder.ASC : SortOrder.DESC);
				break;
			//根据提供的geopoint当原点,按与原点的距离排序
			case SearchConditionSortPattern.Method_DistanceSort:
				if(!StringUtils.isEmpty(conditionPayload)){
					SearchConditionGeopointPayload geopointPayload = JsonHelper.getDTO(conditionPayload,
							SearchConditionGeopointPayload.class);
					if(geopointPayload != null){
						conditionSortBuilder = SortBuilderHelper.builderDistanceSort(sortFieldName, 
								geopointPayload.getLat(), geopointPayload.getLon(), 
								conditionSortPattern.isModeAsc() ? SortOrder.ASC : SortOrder.DESC);
					}
				}
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
				conditionFilterBuilder = FilterBuilders.queryFilter(QueryBuilders.wildcardQuery(fieldName, 
						StringHelper.ASTERISK_STRING_GAP.concat(condition.getPayload()).
						concat(StringHelper.ASTERISK_STRING_GAP)));
				break;
			case SearchConditionPattern.Method_Term:
				conditionFilterBuilder = FilterBuilders.termFilter(fieldName, conditionPayload);
				break;
			case SearchConditionPattern.Method_Prefix:
				conditionFilterBuilder = FilterBuilders.prefixFilter(fieldName, conditionPayload);
				break;
			case SearchConditionPattern.Method_Range:
				SearchConditionRangePayload rangePayload = JsonHelper.getDTO(conditionPayload, SearchConditionRangePayload.class);
				if(rangePayload != null){
					//Range范围搜索的以下几种情况
					switch(method_ext){
						case SearchConditionPattern.MethodExt_Range_Between:
							conditionFilterBuilder = FilterBuilders.rangeFilter(fieldName).
								gt(rangePayload.getGreaterThanValue()).lt(rangePayload.getLessThanValue());
							break;
						case SearchConditionPattern.MethodExt_Range_GreaterThan:
							conditionFilterBuilder = FilterBuilders.rangeFilter(fieldName).
								gt(rangePayload.getGreaterThanValue());
							break;
						case SearchConditionPattern.MethodExt_Range_GreaterThanEqual:
							conditionFilterBuilder = FilterBuilders.rangeFilter(fieldName).
								gte(rangePayload.getGreaterThanValue());
							break;
						case SearchConditionPattern.MethodExt_Range_LessThan:
							conditionFilterBuilder = FilterBuilders.rangeFilter(fieldName).
								lt(rangePayload.getLessThanValue());
							break;
						case SearchConditionPattern.MethodExt_Range_LessThanEqual:
							conditionFilterBuilder = FilterBuilders.rangeFilter(fieldName).
								lte(rangePayload.getLessThanValue());
							break;
						default:
							break;
					}
				}
				break;
			case SearchConditionPattern.Method_String:
				conditionFilterBuilder = FilterBuilders.queryFilter(QueryBuilders.queryStringQuery(conditionPayload)
						.field(fieldName));
				break;
			case SearchConditionPattern.Method_Missing:
				conditionFilterBuilder = FilterBuilders.missingFilter(fieldName);
				break;
			case SearchConditionPattern.Method_Existing:
				conditionFilterBuilder = FilterBuilders.existsFilter(fieldName);
				break;
			case SearchConditionPattern.Method_Geopoint:
				//Geopoint坐标搜索的以下几种情况
				switch(method_ext){
					case SearchConditionPattern.MethodExt_GeopointDistance:
						SearchConditionGeopointDistancePayload geopointDistancePayload = JsonHelper.getDTO(
								conditionPayload, SearchConditionGeopointDistancePayload.class);
						if(geopointDistancePayload != null){
							conditionFilterBuilder = FilterBuilders.geoDistanceFilter(fieldName)
					                .distance(geopointDistancePayload.getDistance())
					                .point(geopointDistancePayload.getLat(), geopointDistancePayload.getLon());
						}
						break;
					case SearchConditionPattern.MethodExt_GeopointRectangle:
						SearchConditionGeopointRectanglePayload geopointRectanglePayload = JsonHelper.getDTO(
								conditionPayload, SearchConditionGeopointRectanglePayload.class);
						if(geopointRectanglePayload != null){
							conditionFilterBuilder = FilterBuilders.geoBoundingBoxFilter(fieldName)
									.topLeft(geopointRectanglePayload.getTopLeft_lat(),geopointRectanglePayload.getTopLeft_lon())
									.bottomRight(geopointRectanglePayload.getBottomRight_lat(),geopointRectanglePayload.getBottomRight_lon());
						}
						break;
					default:
						break;
				}
				
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
	
	public abstract FieldDefine getFieldByName(String fieldName);
}
