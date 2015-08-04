package com.bhu.vas.business.search.service.device;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.geo.GeoHashUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public WifiDeviceSearchDTO buildDto(Map<String,Object> sourceMap) {
		//System.out.println(JsonHelper.getJSONString(sourceMap));
		//System.out.println("uid : " + sourceMap.get(UserMapableComponent.M_id).toString() + " = " + JsonHelper.getJSONString(sourceMap));
		WifiDeviceSearchDTO dto = new WifiDeviceSearchDTO();
		dto.setId(sourceMap.get(WifiDeviceMapableComponent.M_id).toString());
		
		Object show_address = sourceMap.get(WifiDeviceMapableComponent.M_show_address);
		if(show_address != null) dto.setAddress(show_address.toString());
		
		Object workmodel = sourceMap.get(WifiDeviceMapableComponent.M_workmodel);
		if(workmodel != null) dto.setWorkmodel(workmodel.toString());
		
		Object configmodel = sourceMap.get(WifiDeviceMapableComponent.M_configmodel);
		if(configmodel != null) dto.setConfigmodel(configmodel.toString());
		
		Object origswver = sourceMap.get(WifiDeviceMapableComponent.M_origswver);
		if(origswver != null) dto.setOrigswver(origswver.toString());
		
		Object devicetype = sourceMap.get(WifiDeviceMapableComponent.M_devicetype);
		if(devicetype != null) dto.setDevicetype(devicetype.toString());
		
		Object count = sourceMap.get(WifiDeviceMapableComponent.M_count);
		if(count != null) dto.setCount(Integer.parseInt(count.toString()));
		
		Object online = sourceMap.get(WifiDeviceMapableComponent.M_online);
		if(online != null) dto.setOnline(Integer.parseInt(online.toString()));
		
		Object groups = sourceMap.get(WifiDeviceMapableComponent.M_groups);
		if(groups != null) dto.setGroups(groups.toString());
		
		Object registerat = sourceMap.get(WifiDeviceMapableComponent.M_register_at);
		if(registerat != null) dto.setRegister_at(Long.parseLong(registerat.toString()));
		
		Object ghash = sourceMap.get(WifiDeviceMapableComponent.M_ghash);
		if(ghash != null){
			List<String> ghash_list = (List<String>)ghash;
			if(!ghash_list.isEmpty()){
				GeoPoint point = GeoHashUtils.decode(ghash_list.get(0));
				if(point != null){
					dto.setLat(point.getLat());
					dto.setLon(point.getLon());
				}
			}
		}
		return dto;
	}
	
	//用户地理位置定位模糊值
	public static final String GeoPoint_Distance = "2km";
	//public static final String GeoPoint_Distance = "2000m";
	/**
	 * 根据keyword进行分词来搜索地理位置
	 * @param keyword 可以是 mac 或 地理名称
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
//	public QueryResponse<List<WifiDeviceSearchDTO>> searchByKeyword(String keyword,
//			int start, int size) throws ESQueryValidateException {
//		
//		if(StringUtils.isEmpty(keyword)){
//			return emptyQueryListResponse();
//		}
//		FilterBuilder filter = null;
//		if(StringHelper.isValidMac(keyword)){
//			filter = FilterBuilders.prefixFilter(WifiDeviceMapableComponent.M_id, keyword.toLowerCase());
//		}
//		//如果不是mac 去地理位置域进行搜索
//		else{
//			String standardKeyword = IndexableResolver.standardString(keyword);
//			filter = FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, standardKeyword);
//		}
//		
//		//QueryBuilder query = QueryHelper.stringQueryBuilder(WifiDeviceMapableComponent.M_address, standardKeyword);
//		//QueryFilterBuilder filter = FilterBuilders.queryFilter(query);
//		
//		QueryListRequest queryRequest = super.builderQueryListRequest(BusinessIndexConstants.WifiDeviceIndex, 
//				BusinessIndexConstants.Types.WifiDeviceType, null, null, filter, start, size);
//		queryRequest.addSort(sortByOnline());
//		queryRequest.addSort(sortByCount());
//		return super.searchListByQuery(queryRequest);
//	}
	
	
	/**
	 * region表示 需要包含的地域名称
	 * excepts表示 需要不包含的地域名称 逗号分割
	 * 根据keyword进行分词来搜索地理位置
	 * @param keyword 可以是 mac 或 地理名称
	 * @param region 北京市
	 * @param excepts 广东省,上海市
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public QueryResponse<List<WifiDeviceSearchDTO>> searchByKeyword(String keyword, String region,
			String excepts, int start, int size) throws ESQueryValidateException {
		
		if(StringUtils.isEmpty(keyword) && StringUtils.isEmpty(region) && StringUtils.isEmpty(excepts)){
			return emptyQueryListResponse();
		}
		
		BoolFilterBuilder filter = FilterBuilders.boolFilter();
		if(!StringUtils.isEmpty(keyword)){
			filter.must(FilterBuilders.orFilter(
					FilterBuilders.prefixFilter(WifiDeviceMapableComponent.M_id, keyword.toLowerCase()),
					FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, IndexableResolver.standardString(keyword)))
					);
//			if(StringHelper.isValidMac(keyword)){
//				filter.must(FilterBuilders.prefixFilter(WifiDeviceMapableComponent.M_id, keyword.toLowerCase()));
//			}
//			//如果不是mac 去地理位置域进行搜索
//			else{
//				String standardKeyword = IndexableResolver.standardString(keyword);
//				filter.must(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, standardKeyword));
//			}
		}
		
		if(!StringUtils.isEmpty(region)){
			//String standardRegion = IndexableResolver.standardString(region);
			filter.must(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, region));
		}
		
		if(!StringUtils.isEmpty(excepts)){
			//String standardRegion = IndexableResolver.standardString(excepts);
			String[] except_array = excepts.split(StringHelper.COMMA_STRING_GAP);
			for(String except : except_array){
				filter.mustNot(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, except));
			}
		}
		
		//QueryBuilder query = QueryHelper.stringQueryBuilder(WifiDeviceMapableComponent.M_address, standardKeyword);
		//QueryFilterBuilder filter = FilterBuilders.queryFilter(query);
		
		QueryListRequest queryRequest = super.builderQueryListRequest(BusinessIndexConstants.WifiDeviceIndex, 
				BusinessIndexConstants.Types.WifiDeviceType, null, null, filter, start, size);
		queryRequest.addSort(sortByOnline());
		queryRequest.addSort(sortByCount());
		return super.searchListByQuery(queryRequest);
	}
	
	/**
	 * 根据多个条件来进行搜索
	 * @param mac 
	 * @param orig_swver 软件版本号
	 * @param adr 位置参数
	 * @param work_mode 工作模式
	 * @param config_mode 配置模式
	 * @param online null表示全部 true为在线 
	 * @param newVersionDevice null 标识全部 true为新版本设备 大于1.2.7的设备 false为老版本 小于等于1.2.7
	 * @param region 地区
	 * @param excepts 排除地区
	 * @param groupids 所属群组ids 空格分隔
	 * @param groupids_excepts 排序所属群组ids 空格分隔
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public QueryResponse<List<WifiDeviceSearchDTO>> searchByKeywords(String mac, String orig_swver, String adr, 
			String work_mode, String config_mode, String devicetype, Boolean online, Boolean newVersionDevice,
			String region, String excepts, String groupids, String groupids_excepts, int start, int size) throws ESQueryValidateException {

		FilterBuilder filter = null;
		if(StringHelper.hasLeastOneNotEmpty(mac, orig_swver, adr, work_mode, config_mode, 
				devicetype, region, excepts, groupids, groupids_excepts) || online != null || newVersionDevice != null){
			BoolFilterBuilder boolfilter = FilterBuilders.boolFilter();
			if(!StringUtils.isEmpty(mac)){
				boolfilter.must(FilterBuilders.prefixFilter(WifiDeviceMapableComponent.M_id, mac.toLowerCase()));
			}
			if(!StringUtils.isEmpty(orig_swver)){
//				boolfilter.must(FilterBuilders.queryFilter(QueryBuilders.fuzzyQuery(
//						WifiDeviceMapableComponent.M_origswver, orig_swver)));
				boolfilter.must(FilterBuilders.queryFilter(QueryBuilders.wildcardQuery(
						WifiDeviceMapableComponent.M_origswver, "*"+orig_swver+"*")));
			}
			if(!StringUtils.isEmpty(adr)){
				boolfilter.must(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, adr));
			}
			if(!StringUtils.isEmpty(work_mode)){
				boolfilter.must(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_workmodel, work_mode));
			}
			if(!StringUtils.isEmpty(config_mode)){
				boolfilter.must(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_configmodel, config_mode));
			}
			if(!StringUtils.isEmpty(devicetype)){
				boolfilter.must(FilterBuilders.prefixFilter(WifiDeviceMapableComponent.M_devicetype, devicetype));
			}
			if(online != null){
				boolfilter.must(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_online, online ? 1 : 0));
			}
			if(newVersionDevice != null){
				boolfilter.must(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_nvd, newVersionDevice ? 1 : 0));
			}
			if(!StringUtils.isEmpty(region)){
				boolfilter.must(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, region));
			}
			if(!StringUtils.isEmpty(excepts)){
				String[] except_array = excepts.split(StringHelper.COMMA_STRING_GAP);
				for(String except : except_array){
					boolfilter.mustNot(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_address, except));
				}
			}
			if(!StringUtils.isEmpty(groupids)){
				String[] groupids_array = groupids.split(StringHelper.WHITESPACE_STRING_GAP);
				for(String groupid : groupids_array){
					boolfilter.must(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_groups, groupid));
				}
			}
			if(!StringUtils.isEmpty(groupids_excepts)){
				String[] groupids_array = groupids_excepts.split(StringHelper.WHITESPACE_STRING_GAP);
				for(String groupid : groupids_array){
					boolfilter.mustNot(FilterBuilders.termFilter(WifiDeviceMapableComponent.M_groups, groupid));
				}
			}
			filter = boolfilter;
		}else{
			filter = FilterBuilders.matchAllFilter();
		}
		
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
	 * 搜索存在地理位置的数据
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public QueryResponse<List<WifiDeviceSearchDTO>> searchExistAddress(int start, int size) throws ESQueryValidateException {
		ExistsFilterBuilder filter = FilterBuilders.existsFilter(WifiDeviceMapableComponent.M_address);
		QueryListRequest queryRequest = super.builderQueryListRequest(BusinessIndexConstants.WifiDeviceIndex, 
				BusinessIndexConstants.Types.WifiDeviceType, null, null, filter, start, size);
		//queryRequest.addSort(sortByOnline());
		queryRequest.addSort(sortByRegisterAt());
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
