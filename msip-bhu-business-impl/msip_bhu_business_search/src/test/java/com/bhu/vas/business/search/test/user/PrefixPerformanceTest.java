package com.bhu.vas.business.search.test.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;

import com.bhu.vas.business.search.test.user.index.PrefixIndexableComponent;
import com.bhu.vas.business.search.test.user.service.PerformanceIndexService;
import com.smartwork.msip.es.ESClient;
import com.smartwork.msip.es.exception.ESException;
import com.smartwork.msip.es.test.index.IndexStructureConstants;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomPicker;


public class PrefixPerformanceTest extends BaseTest{
	//每个线程最多创建的记录数量
	public static final int Thread_Max_Create_Count = 500000;
	public static final String[] truenames = {"系","统","管","理","员", "卢","广","鹏","星", "范", "邓","湘","清", "吴","云","香", "王","淑","英", "杜","晓","娜", "永","峰", "杨","金","禄", "刘","晓","凤", "周","雅","倩"};
	public static final String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	@Resource
	private PerformanceIndexService performanceIndexService;
	
	@Resource
	private ESClient esclient;
	
	/**
	 * 初始化prefix测试数据 1000W条记录
	 * 20个线程 每个线程执行50W条记录
	 * @throws ElasticsearchException
	 * @throws IOException
	 * @throws ESException
	 * @throws InterruptedException 
	 */
	//@Test
	public void init() throws ElasticsearchException, IOException, ESException, InterruptedException{
		System.out.println("start prefix response create");
		performanceIndexService.createPerformanceResponse();
		
		long start = System.currentTimeMillis();
		System.out.println("start prefix data create ts : " + start);
		List<Thread> threads = new ArrayList<Thread>();
		
		int thread_start_index = 0;
		for(int i = 0;i<20;i++){
			Thread thread = new BulkIndexThread(performanceIndexService, thread_start_index);
			thread.start();
			threads.add(thread);
			thread_start_index = thread_start_index + Thread_Max_Create_Count;
		}
		
		while(true){
			System.out.println("check all thread states");
			boolean allDied = true;
			for(Thread thread : threads){
				if(thread.isAlive()){
					allDied = false;
					break;
				}
			}
			
			if(allDied){
				break;
			}
			Thread.sleep(5000);
		}
		
		System.out.println("end prefix data create ts : " + (System.currentTimeMillis() - start));
	}
	
	//@Test
	public void prefixPerformanceTest(){
		long ts = 0;
		for(int i = 0; i<500;i++){
			ts+=prefixSearch();
		}
		System.out.println("prefix平均ts : " + (ts/500));
	}
	//@Test
	public long prefixSearch(){
		
	//	System.out.println("start prefix : " + start);
		SearchRequestBuilder builder = esclient.getTransportClient().prepareSearch(IndexStructureConstants.PerformanceIndex)
				    .setTypes(IndexStructureConstants.PerformanceIndexTypes.PrefixType)
				   // .setQuery(QueryBuilders.matchPhrasePrefixQuery("name", RandomPicker.randString(truenames,2)).analyzer("keyword"))
				   //.setQuery(QueryBuilders.prefixQuery("name", RandomPicker.randString(truenames,2)))
				    .setQuery(QueryBuilders.prefixQuery("pinyin", RandomPicker.randString(letters,1)))
		            .setFrom(0)
		            .setSize(10);
		//System.out.println(builder.toString());
		long start = System.currentTimeMillis();
		SearchResponse response = builder.execute().actionGet();
		long ret = (System.currentTimeMillis() - start);
		System.out.println("end prefix : " + ret);
		/*SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {  
        	//hit.getSource().put("location", "111");
            System.out.println("分数:"   
                    + hit.getScore()  
                    + ",ID:"  
                    + hit.id() 
                    + ", 名称:"  
                    + hit.getSource().get("showname"));
        }*/
        return ret;
	}
	
	@Test
	public void matchPerformanceTest(){
		long ts = 0;
		for(int i = 0; i<500;i++){
			ts+=matchSearch();
		}
		System.out.println("match平均ts : " + (ts/500));
	}
	//@Test
	public long matchSearch(){
		
		SearchRequestBuilder builder = esclient.getTransportClient().prepareSearch(IndexStructureConstants.PerformanceIndex)
				    .setTypes(IndexStructureConstants.PerformanceIndexTypes.PrefixType)
				    //.setQuery(QueryBuilders.matchPhraseQuery("name", RandomPicker.randString(truenames,1)).slop(10))
				    		//QueryBuilders.queryString(RandomPicker.randString(truenames,1)).field("name"))
				   // .setQuery(QueryBuilders.matchQuery("name", RandomPicker.randString(truenames,2)).analyzer("keyword"))
				    //.setQuery(QueryBuilders.termQuery("name", RandomPicker.randString(truenames,2)))
				   .setQuery(QueryBuilders.termQuery("pinyin", RandomPicker.randString(letters,1)))
		            .setFrom(0)
		            .setSize(10);
		//System.out.println(builder.toString());
		long start = System.currentTimeMillis();
		SearchResponse response = builder.execute().actionGet();
		long ret = (System.currentTimeMillis() - start);
		System.out.println("end string : " + ret);
		/*SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {  
        	//hit.getSource().put("location", "111");
            System.out.println("分数:"   
                    + hit.getScore()  
                    + ",ID:"  
                    + hit.id() 
                    + ", 名称:"  
                    + hit.getSource().get("showname"));
        }*/
       return ret;
	}
	
	/**
	 * 运行批量创建数据索引
	 * 每个bulk 5000个doc
	 * 一共创建500000个doc
	 * @author lawliet
	 *
	 */
	class BulkIndexThread extends Thread{

		private PerformanceIndexService performanceIndexService;
		private int thread_start_index;
		public BulkIndexThread(PerformanceIndexService performanceIndexService, int thread_start_index){
			this.performanceIndexService = performanceIndexService;
			this.thread_start_index = thread_start_index;
		}
		
		@Override
		public void run() {
			System.out.println("Thread: " + Thread.currentThread().getName() + " start ");
			//String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
			int max = thread_start_index + 500000;
			int max_bulkCount = 5000;
			List<PrefixIndexableComponent> prefixIndexables = new ArrayList<PrefixIndexableComponent>();
			int bulkCount = 0;
			for(int i=thread_start_index;i<max;i++){
				try{
					if(bulkCount >= max_bulkCount){
						performanceIndexService.createPrefixIndexsByComponents(prefixIndexables);
						bulkCount = 0;
						prefixIndexables.clear();
						System.out.println("Thread: " + Thread.currentThread().getName() + " bulk execute ");
					}else{
						prefixIndexables.add(performanceIndexService.buildPrefixIndexableComponent(String.valueOf(i+1), RandomPicker.randString(truenames,4)));
					}
					Thread.sleep(100);
				}catch(Exception ex){
					ex.printStackTrace();
				}finally{
					bulkCount++;
				}
			}
			System.out.println("Thread: " + Thread.currentThread().getName() + " end ");
		}
		
	}
}

