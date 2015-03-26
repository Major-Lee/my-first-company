package com.bhu.vas.business.search.service.device;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.search.WifiDeviceSearchDTO;
import com.bhu.vas.business.search.constants.BusinessIndexConstants;
import com.bhu.vas.business.search.mapable.WifiDeviceMapableComponent;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.es.exception.ESQueryValidateException;
import com.smartwork.msip.es.index.resolver.IndexableResolver;
import com.smartwork.msip.es.request.QueryHelper;
import com.smartwork.msip.es.request.QueryRequest;
import com.smartwork.msip.es.request.QueryResponse;
import com.smartwork.msip.es.request.list.QueryListRequest;
import com.smartwork.msip.es.service.SearchService;

/**
 * 搜索用户的业务service类
 * @author lawliet
 *
 */
@Service
public class WifiDeviceSearchService extends SearchService<WifiDeviceSearchDTO>{
	
	@Override
	public WifiDeviceSearchDTO buildDto(Map<String,Object> sourceMap) {
		//System.out.println("uid : " + sourceMap.get(UserMapableComponent.M_id).toString() + " = " + JsonHelper.getJSONString(sourceMap));
		WifiDeviceSearchDTO dto = new WifiDeviceSearchDTO();
		dto.setId(sourceMap.get(WifiDeviceMapableComponent.M_id).toString());
		
		Object show_address = sourceMap.get(WifiDeviceMapableComponent.M_show_address);
		if(show_address != null) dto.setAddress(show_address.toString());
		
		Object workmodel = sourceMap.get(WifiDeviceMapableComponent.M_workmodel);
		if(workmodel != null) dto.setWorkmodel(workmodel.toString());
		
		Object devicetype = sourceMap.get(WifiDeviceMapableComponent.M_devicetype);
		if(devicetype != null) dto.setDevicetype(devicetype.toString());
		
		Object count = sourceMap.get(WifiDeviceMapableComponent.M_count);
		if(count != null) dto.setCount(Integer.parseInt(count.toString()));
		
		Object online = sourceMap.get(WifiDeviceMapableComponent.M_online);
		if(online != null) dto.setOnline(Integer.parseInt(online.toString()));
		
		Object registerat = sourceMap.get(WifiDeviceMapableComponent.M_register_at);
		if(registerat != null) dto.setRegister_at(Long.parseLong(registerat.toString()));
		return dto;
	}
	
	//用户地理位置定位模糊值
	public static final String GeoPoint_Distance = "2km";
	//public static final String GeoPoint_Distance = "2000m";
	/**
	 * 根据地理位置keyword来搜索
	 * @param keyword
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public QueryResponse<List<WifiDeviceSearchDTO>> searchByKeyword(String keyword,
			int start, int size) throws ESQueryValidateException {
		
		if(StringUtils.isEmpty(keyword)){
			return emptyQueryListResponse();
		}
		String standardKeyword = IndexableResolver.standardString(keyword);
		//QueryBuilder query = QueryHelper.stringQueryBuilder(WifiDeviceMapableComponent.M_address, standardKeyword);
		//QueryFilterBuilder filter = FilterBuilders.queryFilter(query);
		TermFilterBuilder filter = FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, standardKeyword);
		QueryListRequest queryRequest = super.builderQueryListRequest(BusinessIndexConstants.WifiDeviceIndex, 
				BusinessIndexConstants.Types.WifiDeviceType, null, null, filter, start, size);
		queryRequest.addSort(sortByOnline());
		queryRequest.addSort(sortByCount());
		return super.searchListByQuery(queryRequest);
	}
	
	/**
	 * 搜索注册时间大于此时间的数据
	 * @param register_at
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public QueryResponse<List<WifiDeviceSearchDTO>> searchGtByRegisterAt(long register_at,
			int start, int size) throws ESQueryValidateException {
		
		if(register_at <= 0){
			return emptyQueryListResponse();
		}
		
		FilterBuilder filter = FilterBuilders.rangeFilter(WifiDeviceMapableComponent.M_register_at).gt(register_at);
		QueryListRequest queryRequest = super.builderQueryListRequest(BusinessIndexConstants.WifiDeviceIndex, 
				BusinessIndexConstants.Types.WifiDeviceType, null, null, filter, start, size);
		queryRequest.addSort(sortByRegisterAt());
		return super.searchListByQuery(queryRequest);
	}
	
	/**
	 * 根据地理位置keyword来搜索数量
	 * @param keyword 如果为空,则全匹配
	 * @return
	 * @throws ESQueryValidateException
	 */
	public long countByKeyword(String keyword) throws ESQueryValidateException{
		QueryBuilder query = null;
//		FilterBuilder filter = null;
		if(StringUtils.isEmpty(keyword)) {
			query = QueryBuilders.matchAllQuery();
			//filter = FilterBuilders.matchAllFilter();
		}else{
			String standardKeyword = IndexableResolver.standardString(keyword);
			query = QueryHelper.termQueryBuilder(WifiDeviceMapableComponent.M_address, standardKeyword);
			//query = QueryHelper.stringQueryBuilder(WifiDeviceMapableComponent.M_address, standardKeyword);
			//filter = FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, standardKeyword);
		}
		
		QueryRequest queryRequest = super.builderQueryRequest(BusinessIndexConstants.WifiDeviceIndex, 
				BusinessIndexConstants.Types.WifiDeviceType, query, null, null);
		return super.searchCountByQuery(queryRequest);
	}
	
	/**
	 * 以坐标点为圆心，以distance为半径，搜索在此范围内的数据
	 * @param coordinate
	 * @param distance
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public QueryResponse<List<WifiDeviceSearchDTO>> searchByGeoDistanceSort(double[] coordinate, String distance,
		int start, int size) throws ESQueryValidateException {
	
		if(coordinate != null && coordinate.length == 2){
			if(StringHelper.isEmpty(distance)){
				distance = GeoPoint_Distance;
			}
			SortBuilder sortBuilder = SortBuilders.geoDistanceSort(WifiDeviceMapableComponent.M_ghash).point(coordinate[0], coordinate[1]).
					unit(DistanceUnit.METERS);
			return super.searchByGeoPoint(BusinessIndexConstants.WifiDeviceIndex, BusinessIndexConstants.Types.WifiDeviceType, 
					WifiDeviceMapableComponent.M_ghash, coordinate[0], coordinate[1], distance, start, size, null, null, sortBuilder);
		}
		return emptyQueryListResponse();
	}
	
	/**
	 * 以坐标点为圆心，以distance为半径，搜索在此范围内的数据数量
	 * @param coordinate
	 * @param distance
	 * @return
	 * @throws ESQueryValidateException
	 */
	public long countByGeoDistanceSort(double[] coordinate, String distance) throws ESQueryValidateException {
		
			if(coordinate != null && coordinate.length == 2){
				if(StringHelper.isEmpty(distance)){
					distance = GeoPoint_Distance;
				}
				return super.countByGeoPoint(BusinessIndexConstants.WifiDeviceIndex, BusinessIndexConstants.Types.WifiDeviceType, 
						WifiDeviceMapableComponent.M_ghash, coordinate[0], coordinate[1], distance, null);
			}
			return 0;
	}
	/**
	 * 以左上右下坐标来锁定矩形区域 搜索此范围内的数据
	 * @param topleft_coordinate
	 * @param bottomRight_coordinate
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public QueryResponse<List<WifiDeviceSearchDTO>> searchByGeoBoundingBox(double[] topleft_coordinate,
			double[] bottomRight_coordinate, int start, int size) throws ESQueryValidateException{
		QueryBuilder query = QueryHelper.geoBoundingBoxQueryFilter(null, WifiDeviceMapableComponent.M_ghash, topleft_coordinate, bottomRight_coordinate);
		QueryListRequest queryRequest = super.builderQueryListRequest(BusinessIndexConstants.WifiDeviceIndex, 
				BusinessIndexConstants.Types.WifiDeviceType, query, null, null, start, size);
		return super.searchListByQuery(queryRequest);
	}
	
	/**
	 * 以左上右下坐标来锁定矩形区域 搜索此范围内的数据数量
	 * @param topleft_coordinate
	 * @param bottomRight_coordinate
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public long countByGeoBoundingBox(double[] topleft_coordinate,double[] bottomRight_coordinate) throws ESQueryValidateException{
		QueryBuilder query = QueryHelper.geoBoundingBoxQueryFilter(null, WifiDeviceMapableComponent.M_ghash, topleft_coordinate, bottomRight_coordinate);
		QueryRequest queryRequest = super.builderQueryRequest(BusinessIndexConstants.WifiDeviceIndex, 
				BusinessIndexConstants.Types.WifiDeviceType, query, null, null);
		return super.searchCountByQuery(queryRequest);
	}
	
	
	public SortBuilder sortByOnline(){
		return QueryHelper.sortBuilder(WifiDeviceMapableComponent.M_online, true);
	}
	
	public SortBuilder sortByCount(){
		return QueryHelper.sortBuilder(WifiDeviceMapableComponent.M_count, true);
	}
	
	public SortBuilder sortByRegisterAt(){
		return QueryHelper.sortBuilder(WifiDeviceMapableComponent.M_register_at, true);
	}
}
