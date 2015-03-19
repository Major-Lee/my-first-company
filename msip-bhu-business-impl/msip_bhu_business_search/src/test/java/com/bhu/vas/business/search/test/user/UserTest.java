package com.bhu.vas.business.search.test.user;
//package com.naola.business.test.user;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.elasticsearch.ElasticsearchException;
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.index.query.FilterBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.junit.Test;
//
//import com.smartwork.msip.es.ESClient;
//import com.smartwork.msip.es.exception.ESException;
//import com.smartwork.msip.es.exception.ESQueryValidateException;
//import com.smartwork.msip.es.request.QueryResponse;
//import com.smartwork.msip.localunit.BaseTest;
//import com.whisper.api.frd.dto.UserFrdDTO;
//import com.whisper.api.user.dto.UserDTO;
//import com.whisper.api.user.model.User;
//import com.whisper.business.search.service.user.UserIndexService;
//import com.whisper.business.search.service.user.UserSearchService;
//
//
//public class UserTest extends BaseTest{
//	
//	@Resource
//	UserSearchService userSearchService;
//	
//	@Resource
//	UserIndexService userIndexService;
//	
//	@Resource
//	protected ESClient esclient;
//	
//	//@Test
//	public void init() throws ElasticsearchException, IOException, ESException{
//		//userIndexService.deleteUserResponse();
//		userIndexService.createUserResponse();
//		
//		User user1 = new User();
//		user1.setId(1);
//		user1.setAvatar("1.jpg");
//		user1.setNick("Aoi_Sora");
//		user1.setMobileno("13810048517");
//		user1.setCreated_at(new Date());
//		
//		User user2 = new User();
//		user2.setId(2);
//		user2.setAvatar("2.jpg");
//		user2.setNick("李文华");
//		user2.setMobileno("13810048516");
//		user2.setCreated_at(new Date());
//		
//		User user3 = new User();
//		user3.setId(3);
//		user3.setAvatar("3.jpg");
//		user3.setNick("李书宜");
//		user3.setMobileno("13810048518");
//		user3.setCreated_at(new Date());
//		
//		User user4 = new User();
//		user4.setId(4);
//		user4.setAvatar("4.jpg");
//		user4.setNick("Abailong**");
//		user4.setMobileno("13810048514");
//		user4.setCreated_at(new Date());
//		
//		List<User> users = new ArrayList<User>();
//		users.add(user1);
//		users.add(user2);
//		users.add(user3);
//		users.add(user4);
//		
//		userIndexService.createIndexsByEntitys(users);
//	}
//	//@Test
//	public void updateMapping() throws IOException, ESException{
//		userIndexService.createUserMapping();
//	}
//	
//	//@Test
//	public void testPrefixSearch() throws ESQueryValidateException{
//		String q = "八宝";
//		prefixSearch(q);
////		q = "lw";
////		prefixSearch(q);
////		q = "liwen";
////		prefixSearch(q);
////		q = "李";
////		prefixSearch(q);
////		q = "aoi_";
////		prefixSearch(q);
//	}
//	//@Test
//	public void prefixSearch(String q) throws ESQueryValidateException{
//		long t1 = System.currentTimeMillis();
//		System.out.println("start-----------------------q:" + q);
//		int uid = 1;
//		QueryResponse<List<UserFrdDTO>> result = userSearchService.prefixSearchUsers(uid, q, 0, 10);
//		System.out.println("end-----------------------q:" + q + " time:" + (System.currentTimeMillis() - t1));
//		List<UserFrdDTO> dtos = result.getResult();
//		for(UserDTO dto : dtos){
//			System.out.println(dto.getNick());
//		}
//		System.out.println(dtos.size() + ":" + result.isFromCache());
//	}
//	
//	//@Test
//	public void explainTest1(){
//		SearchRequestBuilder builder = esclient.getTransportClient().prepareSearch("user_index")
//				.setTypes("user")
//				//.setQuery(QueryBuilders.queryString("艾斯").field("nick"))
//				//.setQuery(QueryBuilders.filteredQuery(QueryBuilders.queryString("艾斯").field("nick"), FilterBuilders.termFilter("id", 200102)))
//				.setPostFilter(FilterBuilders.prefixFilter("nick", "艾斯"))
//				.setFrom(0)
//				.setSize(10)
//				.setExplain(true);
//		System.out.println(builder.toString());
//		SearchResponse response = builder.execute().actionGet();
//		SearchHits shs = response.getHits();
//        for (SearchHit hit : shs) {  
//        	//hit.getSource().put("location", "111");
//            System.out.println("分数:"   
//                    + hit.getScore()  
//                    + ",ID:"  
//                    + hit.id() 
//                    + ", 名称:"  
//                    + hit.getSource().get("shownick"));
//        }
//	}
//	
//	@Test
//	public void createIndexResponseWithDifferentSetting(){
//		boolean createIndexRequestBuilder = esclient.getChannelManager().
//				getResponseChannel().createIndexResponse("different_index", "10", "1");
//		System.out.println(createIndexRequestBuilder);
//	}
//	
//	//@Test
//	public void update() throws IOException, Exception{
////		User user = new User();
////		user.setId(200013);//2014071921nwx.jpg
////		user.setNick("lawliet_cc");
////		user.setAvatar("11.jpg");
////		user.setMobileno("13810048517");
////		user.setCreated_at(new Date());
////		userIndexService.createIndex4User(user);
//		
//		//userIndexService.createIndex(userIndexService.buildAvatarIndexableComponent(200075, "1.jpg"));
//		//userIndexService.updateIndex(userIndexService.buildAvatarIndexableComponent(200176, "1.jpg"));
//		userIndexService.updateUserAvatar(200176, "1.jpg");
//	}
//}
