//package com.bhu.vas.business.search.test.user;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.elasticsearch.ElasticsearchException;
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.junit.Test;
//
//import com.bhu.vas.business.search.test.user.index.PrefixIndexableComponent;
//import com.bhu.vas.business.search.test.user.service.PerformanceIndexService;
//import com.smartwork.msip.es.ESClient;
//import com.smartwork.msip.es.exception.ESException;
//import com.smartwork.msip.es.test.index.IndexStructureConstants;
//import com.smartwork.msip.localunit.BaseTest;
//
//
//public class MultiMatchPerformanceTest extends BaseTest{
//	//每个线程最多创建的记录数量
//	public static final int Thread_Max_Create_Count = 500000;
//	public static final String[] truenames = {"系","统","管","理","员", "卢","广","鹏","星", "范", "邓","湘","清", "吴","云","香", "王","淑","英", "杜","晓","娜", "永","峰", "杨","金","禄", "刘","晓","凤", "周","雅","倩"};
//	public static final String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
//	@Resource
//	private PerformanceIndexService performanceIndexService;
//	
//	@Resource
//	private ESClient esclient;
//	
//	/**
//	 * 初始化prefix测试数据 1000W条记录
//	 * 20个线程 每个线程执行50W条记录
//	 * @throws ElasticsearchException
//	 * @throws IOException
//	 * @throws ESException
//	 * @throws InterruptedException 
//	 */
//	//@Test
//	public void init() throws ElasticsearchException, IOException, ESException, InterruptedException{
//		System.out.println("start multi match response create");
//		long start = System.currentTimeMillis();
//		performanceIndexService.createPerformanceResponse();
//		List<PrefixIndexableComponent> prefixIndexables = new ArrayList<PrefixIndexableComponent>();
//		prefixIndexables.add(performanceIndexService.buildPrefixIndexableComponent("1", "唐子超"));
//		prefixIndexables.add(performanceIndexService.buildPrefixIndexableComponent("2", "tangzichao"));
//		performanceIndexService.createPrefixIndexsByComponents(prefixIndexables);
//		System.out.println("end multi match data create ts : " + (System.currentTimeMillis() - start));
//	}
//	
//	@Test
//	public void multiMatchPerformanceTest(){
//		long ts = 0;
//		for(int i = 0; i<500;i++){
//			ts+=multiMatchSearch();
//		}
//		System.out.println("multiMatch平均ts : " + (ts/500));
//	}
//	//@Test
//	public long multiMatchSearch(){
//		
//		//System.out.println("start multi match : " + start);
//		SearchRequestBuilder builder = esclient.getTransportClient().prepareSearch(IndexStructureConstants.PerformanceIndex)
//				    .setTypes(IndexStructureConstants.PerformanceIndexTypes.PrefixType)
//				    .setQuery(QueryBuilders.multiMatchQuery("t").field("pinyin").field("name"))
//		            .setFrom(0)
//		            .setSize(10);
//		//System.out.println(builder.toString());
//		long start = System.currentTimeMillis();
//		SearchResponse response = builder.execute().actionGet();
//		long ret = (System.currentTimeMillis() - start);
//		System.out.println("end multi match : " + ret);
//		/*SearchHits hits = response.getHits();
//        for (SearchHit hit : hits) {  
//        	//hit.getSource().put("location", "111");
//            System.out.println("分数:"   
//                    + hit.getScore()  
//                    + ",ID:"  
//                    + hit.id() 
//                    + ", 名称:"  
//                    + hit.getSource().get("showname"));
//        }*/
//        return ret;
//	}
//}
//
