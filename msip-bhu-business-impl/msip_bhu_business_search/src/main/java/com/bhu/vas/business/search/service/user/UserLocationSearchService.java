package com.bhu.vas.business.search.service.user;
//package com.whisper.business.search.service.user;
//
//import java.util.List;
//import java.util.Map;
//
//import org.elasticsearch.common.unit.DistanceUnit;
//import org.elasticsearch.index.query.FilterBuilder;
//import org.elasticsearch.index.query.FilterBuilders;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.search.sort.SortBuilder;
//import org.elasticsearch.search.sort.SortBuilders;
//import org.springframework.stereotype.Service;
//
//import com.smartwork.msip.cores.helper.JsonHelper;
//import com.smartwork.msip.es.exception.ESQueryValidateException;
//import com.smartwork.msip.es.request.QueryHelper;
//import com.smartwork.msip.es.request.QueryResponse;
//import com.smartwork.msip.es.service.SearchService;
//import com.whisper.api.user.dto.UserDTO;
//import com.whisper.business.search.constants.IndexConstants;
//import com.whisper.business.search.mapable.UserLocationMapableComponent;
//import com.whisper.business.search.mapable.UserMapableComponent;
//
///**
// * 搜索用户的业务service类
// * @author lawliet
// *
// */
//@Service
//public class UserLocationSearchService extends SearchService<UserDTO>{
//	
//	@Override
//	public UserDTO buildDto(Map<String,Object> sourceMap) {
//		System.out.println("uid : " + sourceMap.get(UserMapableComponent.M_id).toString() + " = " + JsonHelper.getJSONString(sourceMap));
//		UserDTO dto = new UserDTO();
//		dto.setId(Integer.parseInt(sourceMap.get(UserMapableComponent.M_id).toString()));
//		return dto;
//	}
//	
//	//用户地理位置定位模糊值
//	//public static final String GeoPoint_Distance = "2km";
//	public static final String GeoPoint_Distance = "2000m";
//	/**
//	 * 根据用户标示前缀匹配用户
//	 * @param uid
//	 * @param symbol
//	 * @param start
//	 * @param size
//	 * @return
//	 * @throws ESQueryValidateException
//	 */
//	public QueryResponse<List<UserDTO>> searchUsersByLocation(Integer uid, double[] lat_lon,
//			int start, int size) throws ESQueryValidateException {
//		
//		if(lat_lon != null && lat_lon.length == 2){
//			FilterBuilder filterBuilder = this.filterBuilderNotUid(uid);
//			SortBuilder sortBuilder = SortBuilders.geoDistanceSort(UserLocationMapableComponent.M_location).point(lat_lon[0], lat_lon[1]).
//					unit(DistanceUnit.METERS);
//			QueryBuilder queryBuilder = QueryHelper.termQueryBuilder(UserLocationMapableComponent.M_area, "a");
//			return super.searchByGeoPoint(IndexConstants.UserLocationIndex, IndexConstants.Types.UserLocationType, 
//					UserLocationMapableComponent.M_location, lat_lon[0], lat_lon[1], GeoPoint_Distance, start, size, queryBuilder, filterBuilder, sortBuilder);
//		}
//		return emptyQueryListResponse();
//	}
//	
//	/**
//	 * 过滤器 去掉自己
//	 * @param uid
//	 * @return
//	 */
//	public FilterBuilder filterBuilderNotUid(int uid){
//		return FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter(UserLocationMapableComponent.M_id, uid));
//	}
//
//}
