package com.bhu.vas.business.search.service.user;
//package com.whisper.business.search.service.user;
//
//import java.util.List;
//import java.util.Map;
//
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.FilterBuilder;
//import org.elasticsearch.index.query.FilterBuilders;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.search.sort.SortBuilder;
//import org.springframework.stereotype.Service;
//
//import com.smartwork.msip.cores.helper.StringHelper;
//import com.smartwork.msip.es.exception.ESQueryValidateException;
//import com.smartwork.msip.es.index.resolver.IndexableResolver;
//import com.smartwork.msip.es.request.QueryHelper;
//import com.smartwork.msip.es.request.QueryResponse;
//import com.smartwork.msip.es.request.list.QueryListRequest;
//import com.smartwork.msip.es.service.SearchService;
//import com.whisper.api.frd.dto.UserFrdDTO;
//import com.whisper.business.search.constants.IndexConstants;
//import com.whisper.business.search.mapable.UserMapableComponent;
//
///**
// * 搜索用户的业务service类
// * @author lawliet
// *
// */
//@Service
//public class UserSearchService extends SearchService<UserFrdDTO>{
//	
//	@Override
//	public UserFrdDTO buildDto(Map<String,Object> sourceMap) {
//		//System.out.println(sourceMap);
//		UserFrdDTO dto = new UserFrdDTO();
//		dto.setId(Integer.parseInt(sourceMap.get(UserMapableComponent.M_id).toString()));
//		Object shownick_obj = sourceMap.get(UserMapableComponent.M_shownick);
//		if(shownick_obj != null){
//			dto.setNick(shownick_obj.toString());
//		}
//		Object avatar_obj = sourceMap.get(UserMapableComponent.M_avatar);
//		if(avatar_obj != null){
//			dto.setAvatar(avatar_obj.toString());
//		}
//		/*Object mobileno_obj = sourceMap.get(UserMapableComponent.M_showmobileno);
//		if(mobileno_obj != null){
//			dto.setMobileno(mobileno_obj.toString());
//		}*/
//		return dto;
//	}
//	
//	/**
//	 * 前缀方式搜索用户
//	 * 正名, 全拼, 首拼, 手机号
//	 * @param uid
//	 * @param keyword 关键词(可选, 如果没有关键词, 为全匹配)
//	 * @param start
//	 * @param size
//	 * @return
//	 * @throws ESQueryValidateException 
//	 */
//	public QueryResponse<List<UserFrdDTO>> prefixSearchUsers(Integer uid, String keyword,
//			int start, int size) throws ESQueryValidateException {
//		
//		QueryBuilder query = null;
//		String standardKeyword = null;
//		//keyword 关键词(可选, 如果没有关键词, 为全匹配)
//		if(StringHelper.isNotEmpty(keyword)){
//			standardKeyword = IndexableResolver.standardString(keyword);
//			BoolQueryBuilder boolean_query = QueryHelper.emptyBoolQueryBuilder();
//			//UserMapableComponent.M_mno
//			QueryBuilder prefix_query = QueryHelper.prefixBooleanQueryBuilder4Should(standardKeyword,UserMapableComponent.M_nick, 
//					UserMapableComponent.M_pinyin);
//			QueryBuilder string_query = QueryHelper.stringQueryBuilder(UserMapableComponent.M_mno, standardKeyword);
//			
//			boolean_query.should(prefix_query);
//			boolean_query.should(string_query);
//			query = boolean_query;
//		}
//		else{
//			//query = QueryHelper.matchAllQuery();
//			return emptyQueryListResponse();
//		}
//
//		QueryListRequest queryRequest = super.builderQueryListRequest(IndexConstants.UserIndex, 
//				IndexConstants.Types.UserType, query, null, filterBuilderNotUid(uid), start, size);
//		
//		queryRequest.addSort(sortBuilder4Users());
////		queryRequest.addSort(sortBuilder4Users());
//		return super.searchListByQuery(queryRequest);
//	}
//	/**
//	 * 根据用户标示前缀匹配用户
//	 * @param uid
//	 * @param symbol
//	 * @param start
//	 * @param size
//	 * @return
//	 * @throws ESQueryValidateException
//	 */
//	public QueryResponse<List<UserFrdDTO>> symbolSearchUsers(Integer uid, String symbol,
//			int start, int size) throws ESQueryValidateException {
//		
//		if(StringHelper.isNotEmpty(symbol)){
//			String standard_symbol = IndexableResolver.standardString(symbol);
//			//System.out.println(standard_symbol);
//			QueryBuilder query = QueryHelper.prefixQueryBuilder(UserMapableComponent.M_symbol, standard_symbol);
//			
//			QueryListRequest queryRequest = super.builderQueryListRequest(IndexConstants.UserIndex, 
//					IndexConstants.Types.UserType, query, null, filterBuilderNotUid(uid), start, size);
//			queryRequest.addSort(sortBuilder4Users());
//			return super.searchListByQuery(queryRequest);
//		}
//		return emptyQueryListResponse();
//	}
//	
//	/**
//	 * 搜索用户的通用排序方式
//	 * 按照业务的score sort字段
//	 * @return
//	 */
//	public SortBuilder sortBuilder4Users(){
//		return QueryHelper.sortBuilder(UserMapableComponent.M_sort, true);
//	}
//	/**
//	 * 过滤器 去掉自己
//	 * @param uid
//	 * @return
//	 */
//	public FilterBuilder filterBuilderNotUid(int uid){
//		return FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter(UserMapableComponent.M_id, uid));
//	}
//
//}
