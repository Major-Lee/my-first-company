package com.bhu.vas.business.search.test.user;
//package com.naola.business.test.user;
//
//import java.io.IOException;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.elasticsearch.ElasticsearchException;
//import org.junit.Test;
//
//import com.smartwork.msip.es.exception.ESException;
//import com.smartwork.msip.es.exception.ESQueryValidateException;
//import com.smartwork.msip.es.request.QueryResponse;
//import com.smartwork.msip.localunit.BaseTest;
//import com.whisper.api.user.dto.UserDTO;
//import com.whisper.business.search.service.user.UserLocationIndexService;
//import com.whisper.business.search.service.user.UserLocationSearchService;
//
//
//public class UserLocationTest extends BaseTest{
//	
//	@Resource
//	UserLocationSearchService userLocationSearchService;
//	
//	@Resource
//	UserLocationIndexService userLocationIndexService;
//	
//	@Test
//	public void query() throws ElasticsearchException, IOException, ESException, ESQueryValidateException{
//		Integer uid = 200119;
//		double[] lat_lon = new double[]{40.7143528, -74.0059731};
//		QueryResponse<List<UserDTO>> result = userLocationSearchService.searchUsersByLocation(uid, lat_lon, 0, 10);
//		List<UserDTO> list = result.getResult();
//		for(UserDTO dto : list){
//			System.out.print(","+dto.getId());
//		}
//	}
//
//}
