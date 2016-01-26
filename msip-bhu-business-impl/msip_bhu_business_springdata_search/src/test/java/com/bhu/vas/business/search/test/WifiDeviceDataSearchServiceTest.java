package com.bhu.vas.business.search.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.data.domain.Page;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.core.condition.component.SearchCondition;
import com.bhu.vas.business.search.core.condition.component.SearchConditionLogicEnumType;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPack;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPattern;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSort;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSortPattern;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionGeopointDistancePayload;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionGeopointPayload;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionGeopointRectanglePayload;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionRangePayload;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.localunit.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WifiDeviceDataSearchServiceTest extends BaseTest{
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	
	@BeforeClass
    public static void setUp() throws Exception {
		System.out.println("setUp");
		Thread.sleep(1000);
		//System.out.println(FieldIndex.not_analyzed.name());
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	System.out.println("tearDown");
    	Thread.sleep(1000);
    }
    
    @Test
	public void test001BatchCreateDocument(){
    	//wifiDeviceDataSearchService.refresh(false);
		List<WifiDeviceDocument> docs = new ArrayList<WifiDeviceDocument>();
    	//List<IndexQuery> indexQuerys = new ArrayList<IndexQuery>();
		WifiDeviceDocument doc1 = new WifiDeviceDocument();
		doc1.setId("84:82:f4:0a:64:68");
		doc1.setD_mac("84:82:f4:0a:64:68");
		doc1.setD_sn("BN205CD100343AA");
		doc1.setD_origswver("AP201P07V1.2.14z2");
		doc1.setD_origvapmodule("H106V1.3.2M8888");
		doc1.setD_workmodel("bridge-ap");
		doc1.setD_configmodel("basic");
		doc1.setD_type("H201");
		doc1.setD_geopoint(new double[]{109.407456,24.315300});
		doc1.setD_address("广西壮族自治区柳州市柳南区西堤路");
		doc1.setD_online("0");
		doc1.setD_monline("0");
		doc1.setD_hoc(0);
		doc1.setD_lastregedat(0l);
		doc1.setD_lastlogoutat(0l);
		doc1.setD_dut("TU");
		doc1.setD_uptime("3600");
		doc1.setUpdatedat(DateTimeHelper.getDateTime());
		doc1.setO_template("style001");
		doc1.setO_graylevel("gl1");
		doc1.setO_batch("20151104");
		doc1.setU_id("1");
		doc1.setU_nick("唐子超");
		doc1.setU_mno("13810048517");
		doc1.setU_mcc("86");
		doc1.setU_type("15");
		doc1.setA_id("101");
		doc1.setA_nick("代理商101");
		doc1.setA_org("代理商101的公司");
		doc1.setD_createdat(System.currentTimeMillis());
		
		
		WifiDeviceDocument doc2 = new WifiDeviceDocument();
		doc2.setId("62:68:75:00:00:01");
		doc2.setD_mac("62:68:75:00:00:01");
		doc2.setD_sn("BN007BF106629AA");
		doc2.setD_origswver("AP304P07V1.2.18");
		doc2.setD_origvapmodule("H106V1.3.2M8888");
		doc2.setD_workmodel("router-ap");
		doc2.setD_configmodel("wwan");
		doc2.setD_type("H304");
		doc2.setD_geopoint(new double[]{116.40387397,39.91488908});
		doc2.setD_address("北京市东城区中华路甲10号");
		doc2.setD_online("1");
		doc2.setD_monline("1");
		doc2.setD_hoc(10);
		doc2.setD_lastregedat(1446311400000l);//2015-11-01 01:10:00
		doc2.setD_lastlogoutat(1446310920000l);//2015-11-01 01:02:00
		doc2.setD_dut("TC");
		doc2.setD_uptime("3600");
		doc2.setUpdatedat(DateTimeHelper.getDateTime());
		doc2.setO_template("style002");
		doc2.setO_graylevel("gl2");
		doc2.setO_batch("20151104");
		doc2.setU_id("2");
		doc2.setU_nick("lawliet");
		doc2.setU_mno("13810048518");
		doc2.setU_mcc("86");
		doc2.setU_type("10");
		doc2.setA_id("101");
		doc2.setA_nick("代理商101");
		doc2.setA_org("代理商101的公司");
		doc2.setD_createdat(System.currentTimeMillis());
		
		WifiDeviceDocument doc3 = new WifiDeviceDocument();
		doc3.setId("12:d3:7f:be:34:12");
		doc3.setD_mac("12:d3:7f:be:34:12");
		doc3.setD_sn("BN007BD100075AA");
		doc3.setD_origswver("AP304P07V1.2.18Build8578");
		doc3.setD_origvapmodule("H305V1.2.17M8578");
		doc3.setD_workmodel("router-ap");
		doc3.setD_configmodel("wwan");
		doc3.setD_type("H305");
		doc3.setD_geopoint(new double[]{116.345581,40.017058});
		doc3.setD_address("北京市海淀区双清路");
		doc3.setD_online("2");
		doc3.setD_monline("2");
		doc3.setD_hoc(0);
		doc3.setD_lastregedat(1446311400000l);//2015-11-01 01:10:00
		doc3.setD_lastlogoutat(1446313800000l);//2015-11-01 01:50:00
		doc3.setD_dut("TC");
		doc3.setD_uptime("3600");
		doc3.setUpdatedat(DateTimeHelper.getDateTime());
		doc3.setO_template("style002");
		doc3.setO_graylevel("gl2");
		doc3.setO_batch("20151104");
		doc3.setU_id("3");
		doc3.setU_nick("tangzichao");
		doc3.setU_mno("13810048519");
		doc3.setU_mcc("87");
		doc3.setU_type("1");
		doc3.setA_id("102");
		doc3.setA_nick("代理商102");
		doc3.setA_org("代理商102的公司");
		doc3.setD_createdat(System.currentTimeMillis());
		
		WifiDeviceDocument doc4 = new WifiDeviceDocument();
		doc4.setId("84:82:f4:05:52:14");
		doc4.setD_mac("84:82:f4:05:52:14");
		doc4.setD_sn("BN007BD100075AA");
		doc4.setD_origswver("AP201P07V1.2.14r3");
		doc4.setD_origvapmodule("H305V1.2.17M8579");
		doc4.setD_workmodel("router-ap");
		doc4.setD_configmodel("wwan");
		doc4.setD_type("H305");
		doc4.setD_geopoint(new double[]{116.345581,40.017058});
		doc4.setD_address("北京市海淀区双清路");
		doc4.setD_online("1");
		doc4.setD_monline("1");
		doc4.setD_hoc(12);
		doc4.setD_lastregedat(1446312300000l);//2015-11-01 01:25:00
		doc4.setD_lastlogoutat(1446310800000l);//2015-11-01 01:00:00
		doc4.setD_dut("TC");
		doc4.setD_uptime("3600");
		doc4.setUpdatedat(DateTimeHelper.getDateTime());
		doc4.setO_template("style002");
		doc4.setO_graylevel("gl1");
		doc4.setO_batch("20151103");
		doc4.setU_id("3");
		doc4.setU_nick("tangzichao");
		doc4.setU_mno("13810048519");
		doc4.setU_mcc("87");
		doc4.setU_type("1");
		doc4.setA_id("102");
		doc4.setA_nick("代理商102");
		doc4.setA_org("代理商102的公司");
		doc4.setD_createdat(System.currentTimeMillis());
		
		WifiDeviceDocument doc5 = new WifiDeviceDocument();
		doc5.setId("84:82:f4:0a:60:a8");
		doc5.setD_mac("84:82:f4:0a:60:a8");
		doc5.setD_sn("BN007BD100075AA");
		doc5.setD_origswver("AP201P07V1.2.14r3");
		doc5.setD_origvapmodule("H305V1.2.17M8579");
		doc5.setD_workmodel("router-ap");
		doc5.setD_configmodel("wwan");
		doc5.setD_type("H305");
		doc5.setD_geopoint(new double[]{116.345581,40.017058});
		doc5.setD_address("北京市海淀区双清路");
		doc5.setD_online("1");
		doc5.setD_monline("1");
		doc5.setD_hoc(12);
		doc5.setD_lastregedat(1446312300000l);//2015-11-01 01:25:00
		doc5.setD_lastlogoutat(1446310800000l);//2015-11-01 01:00:00
		doc5.setD_dut("TC");
		doc5.setD_uptime("3600");
		doc5.setUpdatedat(DateTimeHelper.getDateTime());
		doc5.setO_template("style002");
		doc5.setO_graylevel("gl1");
		doc5.setO_batch("20151104");
		doc5.setU_id(null);
		doc5.setU_nick(null);
		doc5.setU_mno(null);
		doc5.setU_mcc(null);
		doc5.setU_type(null);
		doc5.setA_id("102");
		doc5.setA_nick("代理商102");
		doc5.setA_org("代理商102的公司");
		doc5.setD_createdat(System.currentTimeMillis());
		
		docs.add(doc1);
		docs.add(doc2);
		docs.add(doc3);
		docs.add(doc4);
		docs.add(doc5);
		/*indexQuerys.add(new IndexQueryBuilder().withId(doc1.getId()).withObject(doc1).build());
		indexQuerys.add(new IndexQueryBuilder().withId(doc2.getId()).withObject(doc2).build());
		indexQuerys.add(new IndexQueryBuilder().withId(doc3.getId()).withObject(doc3).build());
		indexQuerys.add(new IndexQueryBuilder().withId(doc4.getId()).withObject(doc4).build());
		indexQuerys.add(new IndexQueryBuilder().withId(doc5.getId()).withObject(doc5).build());
		//wifiDeviceDataSearchService.getRepository().save(docs);
		wifiDeviceDataSearchService.getElasticsearchTemplate().bulkIndex(indexQuerys);
		wifiDeviceDataSearchService.getElasticsearchTemplate().refresh(clazz, true);*/
		//wifiDeviceDataSearchService.refresh(true);
		
		//wifiDeviceDataSearchService.getRepository().save(docs);
		wifiDeviceDataSearchService.bulkIndex(docs);
		
		Map<String,Object> source_map = new HashMap<String,Object>();
		source_map.put("u_type","99");
		//source_map.put("u_mcc", "88");
		source_map.put("d_geopoint", new double[]{116.345582,40.017052});

		wifiDeviceDataSearchService.updateIndex(doc5.getId(), source_map);
//		
//		source_map = new HashMap<String,Object>();
//		source_map.put("a_org", null);
//		
//		wifiDeviceDataSearchService.updateIndex(doc5.getId(), source_map);
/*		List<String> ids = new ArrayList<String>();
		ids.add("84:82:f4:0a:60:a8");
		ids.add("84:82:f4:05:52:88");
		List<Map<String,Object>> sourceMaps = new ArrayList<Map<String,Object>>();
		Map<String,Object> source_map1 = new HashMap<String,Object>();
		source_map1.put("id", "84:82:f4:0a:60:a8");
		source_map1.put("o_batch","01");
		Map<String,Object> source_map2 = new HashMap<String,Object>();
		source_map2.put("id", "84:82:f4:05:52:88");
		source_map2.put("o_batch","02");
		sourceMaps.add(source_map1);
		sourceMaps.add(source_map2);*/
		
//		IndexRequest indexRequest = new IndexRequest();
//		indexRequest.source(source_map2);
//		UpdateQuery updateQuery = new UpdateQueryBuilder().withId("84:82:f4:05:52:88")
//				.withDoUpsert(true).withClass(WifiDeviceDocument.class)
//				.withIndexRequest(indexRequest).build();
		List<String> ids = new ArrayList<String>();
		ids.add(doc1.getId());
		ids.add(doc2.getId());
		ids.add(doc3.getId());
		ids.add(doc4.getId());
		ids.add(doc5.getId());
		source_map = new HashMap<String,Object>();
		source_map.put("u_mcc","86");
		wifiDeviceDataSearchService.bulkUpdate(ids, source_map, false, false, false);
		//wifiDeviceDataSearchService.bulkUpdate(ids, sourceMaps, true, false, false);
		//wifiDeviceDataSearchService.getElasticsearchTemplate().update(updateQuery);
		
		//wifiDeviceDataSearchService.getRepository().delete("84:82:f4:19:01:0c");
		
		wifiDeviceDataSearchService.refresh(true);
	}
	

	
	//@Test
	public void test001UpdateDocument(){

	}
	
	/**
	 * 满足条件
	 * 1：设备从未上线
	 * 2：设备业务线为urouter
	 */
	//@Test
	public void test002SearchConditionDocument(){
//		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备从未上线
		SearchCondition sc_neveronline = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "0");
//		SearchCondition sc_neveronline = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "0");
		//设备业务线为urouter
		SearchCondition sc_urouter = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), "TU TC");
//		SearchCondition sc_urouter = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), "TU TC");
		//searchConditions.add(sc_neveronline);
//		searchConditions.add(sc_urouter);
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(
				sc_neveronline, sc_urouter);
		SearchConditionMessage searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage(
				pack_must);
//		SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		System.out.println("JSON test002:"+ JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test002:"+ doc.getId());
    	}
	}
	
	/**
	 * 满足条件
	 * 1：设备在线
	 * 2：设备业务线为商业wifi
	 * 3：设备灰度为二级
	 */
	//@Test
	public void test003SearchConditionDocument(){
//		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备在线
		SearchCondition sc_neveronline = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "1");
//		SearchCondition sc_neveronline = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "1");
		//设备业务线为商业wifi
		SearchCondition sc_urouter = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.Equal.getPattern(), "TC");
//		SearchCondition sc_urouter = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.Equal.getPattern(), "TC");
		//设备灰度为二级
		SearchCondition sc_graylevel2 = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.O_GRAYLEVEL.getName(), SearchConditionPattern.Equal.getPattern(), "gl2");
//		SearchCondition sc_graylevel2 = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.O_GRAYLEVEL.getName(), SearchConditionPattern.Equal.getPattern(), "gl2");
//		searchConditions.add(sc_neveronline);
//		searchConditions.add(sc_urouter);
//		searchConditions.add(sc_graylevel2);
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(
				sc_neveronline, sc_urouter, sc_graylevel2);
		SearchConditionMessage searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage(
				pack_must);
//		SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		System.out.println("JSON test003:"+ JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test003:"+ doc.getId());
    	}
	}
	
	
	/**
	 * 满足条件
	 * 1：设备在线
	 * 2：设备业务线为商业wifi
	 * 3：设备灰度为一级或二级
	 */
	//@Test
	public void test004SearchConditionDocument(){
//		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备在线
		SearchCondition sc_neveronline = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "1");
		
//		SearchCondition sc_neveronline = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "1");
		
		//设备业务线为商业wifi
		SearchCondition sc_urouter = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.Equal.getPattern(), "TC");
		
//		SearchCondition sc_urouter = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.Equal.getPattern(), "TC");
		//设备灰度为一级或二级
		SearchCondition sc_graylevel2 = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.O_GRAYLEVEL.getName(), SearchConditionPattern.StringEqual.getPattern(), "gl1 gl2");
//		SearchCondition sc_graylevel2 = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.O_GRAYLEVEL.getName(), SearchConditionPattern.StringEqual.getPattern(), "gl1 gl2");
//		searchConditions.add(sc_neveronline);
//		searchConditions.add(sc_urouter);
//		searchConditions.add(sc_graylevel2);
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_neveronline,
				sc_urouter, sc_graylevel2);
		SearchConditionMessage searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage(pack_must);
//		SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		System.out.println("JSON test004:"+ JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test004:"+ doc.getId());
    	}
    	
   	
//    	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//    	boolQuery.must(QueryBuilders.termQuery(BusinessIndexDefine.WifiDevice.
//				Field.D_ONLINE.getName(), "1"));
//    	boolQuery.must(QueryBuilders.termQuery(BusinessIndexDefine.WifiDevice.
//				Field.D_DEVICEUNITTYPE.getName(), "TC"));
    	
//    	BoolQueryBuilder boolQuery2 = QueryBuilders.boolQuery();
//    	boolQuery2.should(QueryBuilders.termQuery(BusinessIndexDefine.WifiDevice.
//				Field.O_GRAYLEVEL.getName(), "GL1"));
//    	boolQuery2.should(QueryBuilders.termQuery(BusinessIndexDefine.WifiDevice.
//				Field.O_GRAYLEVEL.getName(), "GL2"));
    	
//    	boolQuery2.must(boolQuery2);
    	
//    	boolQuery.must(QueryBuilders.queryStringQuery("GL1 GL2").field(BusinessIndexDefine.WifiDevice.
//				Field.O_GRAYLEVEL.getName()));
    	
    			
//    	result = wifiDeviceDataSearchService.getRepository().search(FilterBuilders.boolFilter(), new PageRequest(0,10));
//        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//        		.withQuery(QueryBuilders.boolQuery())
//        		//.withFilter(FilterBuilders.boolFilter())
//                .withPageable(new PageRequest(0,10))
//                .build();
//        
//        result = wifiDeviceDataSearchService.getRepository().search(searchQuery);
//    	for(WifiDeviceDocument doc : result){
//    	    System.out.println("2="+doc.getId());
//    	}
	}
	

	/**
	 * 实例一：
	 * 满足条件
	 * 	设备最后上线时间在2015-11-01 01:20:00 (1446312000000l) 和 2015-11-01 01:30:00 (1446312600000l)之间
	 * 实例二：
	 * 	上述条件反向
	 * 
	 */
	//@Test
	public void test005SearchConditionDocument(){
//		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		
		/*********************************   实例一  *****************************************/
		//设备最后上线时间在2015-11-01 01:20:00 (1446312000000l) 和 2015-11-01 01:30:00 (1446312600000l)之间
		SearchConditionRangePayload rangeBetweenPayload = SearchConditionRangePayload.
				buildRangBetweenPayload(String.valueOf(1446312000000l), String.valueOf(1446312600000l));
		
		SearchCondition sc_betweenAndLastRegedAt = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_LASTREGEDAT.getName(), SearchConditionPattern.Between.getPattern(), 
				JsonHelper.getJSONString(rangeBetweenPayload));
//		SearchCondition sc_betweenAndLastRegedAt = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_LASTREGEDAT.getName(), SearchConditionPattern.Between.getPattern(), 
//				JsonHelper.getJSONString(rangeBetweenPayload));
		
//		searchConditions.add(sc_betweenAndLastRegedAt);
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_betweenAndLastRegedAt);
		SearchConditionMessage searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		//SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		System.out.println("JSON test005:"+ "实例一:" + JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test005:"+ "实例一:" + doc.getId() + " = " + doc.getD_lastregedat());
    	}
    	
    	System.out.println("----------------------------------------------------------------");
    	/*********************************   实例二  *****************************************/
//    	searchConditions.clear();
    	//设备最后上线时间不存在2015-11-01 01:20:00 (1446312000000l) 和 2015-11-01 01:30:00 (1446312600000l)之间
    	
//		SearchCondition sc_notBetweenAndLastRegedAt = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_LASTREGEDAT.getName(), SearchConditionPattern.Between.getPattern(), 
//				JsonHelper.getJSONString(rangeBetweenPayload));
//		
//		searchConditions.add(sc_notBetweenAndLastRegedAt);
    	SearchConditionPack pack_must_not = SearchConditionPack.builderSearchConditionPackWithConditions(
    			SearchConditionLogicEnumType.MustNot, sc_betweenAndLastRegedAt);
		searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage(pack_must_not);
		
		System.out.println("JSON test005:"+ "实例二:" + JsonHelper.getJSONString(searchConditionMessage));
		
		result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test005:"+ "实例二:" + doc.getId() + " = " + doc.getD_lastregedat());
    	}
    	
	}
	
	
//	/**
//	 * 满足条件
//	 * 1：已经绑定用户的设备
//	 * 2：设备在线状态为在线或离线
//	 * 3：代理商的公司名称中包含"公司"两个字
//	 * 4：导入的批次是20151104开头的
//	 * 5：按照mac地址降序排序
//	 */
//	@Test
//	public void test006SearchConditionDocument(){
////		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
//		//已经绑定用户的设备
//		SearchCondition sc_existingUid = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.U_ID.getName(), SearchConditionPattern.Existing.getPattern(), null);
//		//设备在线状态为在线或离线
//		SearchCondition sc_onlineOrOffline = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_ONLINE.getName(), SearchConditionPattern.StringEqual.getPattern(), "1 2");
//		//代理商的公司名称中包含"公司"两个字
//		SearchCondition sc_containOrgName = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.A_ORG.getName(), SearchConditionPattern.Contain.getPattern(), "公司");
//		//导入的批次是20151104
//		SearchCondition sc_equalBatch = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.O_BATCH.getName(), SearchConditionPattern.PrefixContain.getPattern(), "20151104");
//		//按照mac地址降序排序
//		SearchCondition sc_sortDescById = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.ID.getName(), SearchConditionSortPattern.SortDesc.getPattern(), null);
//		
////		searchConditions.add(sc_existingUid);
////		searchConditions.add(sc_onlineOrOffline);
////		searchConditions.add(sc_containOrgName);
////		searchConditions.add(sc_equalBatch);
////		searchConditions.add(sc_sortDescById);
//
//		SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
//		System.out.println("JSON test006:"+ JsonHelper.getJSONString(searchConditionMessage));
//		
//		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
//    	System.out.println("test006: total: " + result.getTotalElements() + " total pages: " + result.getTotalPages());
//		for(WifiDeviceDocument doc : result){
//    	    System.out.println("test006:"+ doc.getId());
//    	}
//	}
	
	/**
	 * 满足条件
	 * 按照设备位置与"广西壮族自治区柳州市柳南区西堤路"{109.407456,24.315300}的距离升序排序
	 */
	//@Test
	public void test007SearchConditionDocument(){
//		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//已经绑定用户的设备
		SearchConditionGeopointPayload geopointPayload = SearchConditionGeopointPayload.buildPayload(
				"广西壮族自治区柳州市柳南区西堤路", 24.315300d, 109.407456d);
//		SearchConditionGeopointPayload geopointPayload = SearchConditionGeopointPayload.buildPayload(
//				"北京市海淀区双清路", 40.017058d, 116.345581d);

		SearchConditionSort sc_sortByGeopoint = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.WifiDevice.
				Field.D_GEOPOINT.getName(), SearchConditionSortPattern.SortGeopointDistance.getPattern(),
				SortOrder.ASC, JsonHelper.getJSONString(geopointPayload));

//		SearchCondition sc_sortByGeopoint = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_GEOPOINT.getName(), SearchConditionSortPattern.SortGeopointDistanceAsc.getPattern(), 
//				JsonHelper.getJSONString(geopointPayload));
		
//		searchConditions.add(sc_sortByGeopoint);
		SearchConditionMessage searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage();
		searchConditionMessage.addSorts(sc_sortByGeopoint);
//		SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		System.out.println("JSON test007:"+ JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test007:"+ doc.getId());
    	}
	}
	
	/**
	 * 满足条件
	 * 按照圆心坐标是"北京市海淀区双清路" {116.345581,40.017058} 半径为30km内的设备
	 */
	//@Test
	public void test008SearchConditionDocument(){
//		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//已经绑定用户的设备
		SearchConditionGeopointDistancePayload geopointDistancePayload = SearchConditionGeopointDistancePayload.buildPayload(
				"北京市海淀区双清路", 40.017058d, 116.345581d, "30km");

		SearchCondition sc_geopointDistance = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_GEOPOINT.getName(), SearchConditionPattern.GeopointDistance.getPattern(), 
				JsonHelper.getJSONString(geopointDistancePayload));
//		SearchCondition sc_geopointDistance = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_GEOPOINT.getName(), SearchConditionPattern.GeopointDistance.getPattern(), 
//				JsonHelper.getJSONString(geopointDistancePayload));
		
//		searchConditions.add(sc_geopointDistance);

		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_geopointDistance);
//		SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		SearchConditionMessage searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		System.out.println("JSON test008:"+ JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test008:"+ doc.getId());
    	}
	}
	
	/**
	 * 满足条件
	 * 按照长方形坐标为"北京市"的区域查询区域内的设备
	 * topLeft {116.21520418420414, 40.07323716177983}
	 * bottomRight {116.5394884295654, 39.75419016772713}
	 */
	//@Test
	public void test009SearchConditionDocument(){
//		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchConditionGeopointRectanglePayload geopointRectanglePayload = SearchConditionGeopointRectanglePayload.buildPayload(
				"北京市区域", 40.07323716177983d, 116.21520418420414d, 39.75419016772713d, 116.5394884295654d);

		SearchCondition sc_geopointRectangle = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_GEOPOINT.getName(), SearchConditionPattern.GeopointRectangle.getPattern(), 
				JsonHelper.getJSONString(geopointRectanglePayload));
//		SearchCondition sc_geopointRectangle = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_GEOPOINT.getName(), SearchConditionPattern.GeopointRectangle.getPattern(), 
//				JsonHelper.getJSONString(geopointRectanglePayload));
		
//		searchConditions.add(sc_geopointRectangle);
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_geopointRectangle);
		SearchConditionMessage searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage(pack_must);
//		SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		System.out.println("JSON test009:"+ JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test009:"+ doc.getId());
    	}
	}
	
	
	/**
	 * 满足条件
	 * 1：设备按照软件版本号倒序排序
	 * 会按照字母和数字的大小进行排序
	 * 例如:倒序顺序如下
	 *  12:d3:7f:be:34:12 = AP304P07V1.2.18Build8578
		62:68:75:00:00:01 = AP304P07V1.2.18
		84:82:f4:0a:64:68 = AP201P07V1.2.14z2
		84:82:f4:05:52:14 = AP201P07V1.2.14r3
	 */
	//@Test
	public void test0010SearchConditionDocument(){
//		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备按照软件版本号倒序排序
		SearchConditionSort sc_sortDescByOrigswver = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.WifiDevice.
				Field.D_ORIGSWVER.getName(), SearchConditionSortPattern.Sort.getPattern(), SortOrder.DESC, null);
//		SearchCondition sc_sortDescByOrigswver = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_ORIGSWVER.getName(), SearchConditionSortPattern.SortDesc.getPattern(), null);
		
//		searchConditions.add(sc_sortDescByOrigswver);
		SearchConditionMessage searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage();
		searchConditionMessage.addSorts(sc_sortDescByOrigswver);
		
//		SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		System.out.println("JSON test0010:"+ JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test0010:"+ doc.getId() + " = " + doc.getD_origswver());
    	}
	}

	/**
	 * 满足条件
	 * 实例一:
	 * 	1:设备按照软件版本号大于AP201P07V1.2.14z2匹配
	 * 	2:设备按照软件版本号倒序排序
	 * 实例二:
	 * 	满足上述的条件1 只获取数量
	 */
	//@Test
	public void test0011SearchConditionDocument(){
		//设备按照软件版本号大于AP201P07V1.2.14z2匹配
		SearchConditionRangePayload rangeGreaterPayload = SearchConditionRangePayload.buildRangGreaterPayload("AP201P07V1.2.14z2");
		SearchCondition sc_greaterThanByOrigswver = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_ORIGSWVER.getName(), SearchConditionPattern.GreaterThan.getPattern(), 
				JsonHelper.getJSONString(rangeGreaterPayload));
//		SearchCondition sc_greaterThanByOrigswver = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_ORIGSWVER.getName(), SearchConditionPattern.GreaterThan.getPattern(), 
//				JsonHelper.getJSONString(rangeGreaterPayload));
		//设备按照软件版本号倒序排序
//		SearchCondition sc_sortDescByOrigswver = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_ORIGSWVER.getName(), SearchConditionSortPattern.SortDesc.getPattern(), null);
		
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(
				sc_greaterThanByOrigswver);
		
		SearchConditionMessage searchConditionMessage = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		//SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		System.out.println("JSON test0011 实例一:"+ JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test0011 实例一:"+ doc.getId() + " = " + doc.getD_origswver());
    	}
    	
    	SearchConditionMessage searchConditionCountMessage = SearchConditionMessage.builderSearchConditionMessage(
    			SearchType.COUNT.id(), pack_must);
    	
    	result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionCountMessage, 0, 10);
    	System.out.println("test0011 实例二 total:"+ result.getTotalElements() + " total pages: " + result.getTotalPages());
    	for(WifiDeviceDocument doc : result){
    	    System.out.println("test0011 实例二:"+ doc.getId() + " = " + doc.getD_origswver());
    	}
    	
    	
//    	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//    	boolQuery.must(QueryBuilders.rangeQuery(BusinessIndexDefine.WifiDevice.
//				Field.D_ORIGSWVER.getName()).lt("AP201P07V1.2.14z2"));
//    	
//    	result = wifiDeviceDataSearchService.getRepository().search(boolQuery, new PageRequest(0,10));
//    	for(WifiDeviceDocument doc : result){
//    	    System.out.println(doc.getId() + " = " + doc.getD_origswver());
//    	}

	}
	
	//@Test
	public void test0012SearchConditionDocument(){
		//String message = "{\"search_t\":1,\"search_cs\":[{\"key\":\"d_lastregedat\",\"pattern\":\"btn\",\"payload\":\"{\\\"gtv\\\":\\\"1448341200000\\\",\\\"ltv\\\":\\\"1448430600000\\\"}\"}]}";
		//String message = "{\"search_t\":1,\"search_cs\":[{\"key\":\"d_online\",\"pattern\":\"seq\",\"payload\":\"1\"}]}";
		//String message = "{\"search_t\":1,\"search_cs\":[{\"key\":\"d_dut\",\"pattern\":\"seq\",\"payload\":\"T\"}]}";
		//String message = "{\"search_t\":1,\"search_cs\":[{\"logic\":\"must\",\"cps\":null,\"cs\":[{\"logic\":\"must\",\"key\":\"d_mac\",\"pattern\":\"seq\",\"payload\":\"84:82:F4:23:06:68\"}]}]}";
		String message = "{\"search_t\":1,\"search_cs\":[{\"cs\":[{\"key\":\"d_mac\",\"pattern\":\"seq\",\"payload\":\"84:82:F4:23:06:68\"}]}]}";
		SearchConditionMessage searchConditionMessage = JsonHelper.getDTO(message, SearchConditionMessage.class);
	
		System.out.println("JSON test0012:"+ JsonHelper.getJSONString(searchConditionMessage));
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(searchConditionMessage, 0, 10);
    	System.out.println(result.getTotalElements());
		for(WifiDeviceDocument doc : result){
    	    System.out.println("test0012:"+ doc.getId() + " = " + doc.getD_lastregedat());
    	}
	}
	
	//@Test
	public void test0013SearchIteratorAll(){
		String message = "{\"search_t\":1,\"search_cs\":[{\"logic\":\"must\",\"cps\":null,\"cs\":[{\"logic\":\"must\",\"key\":\"d_dut\",\"pattern\":\"seq\",\"payload\":\"TU \"}]}]}";
		wifiDeviceDataSearchService.iteratorAll(BusinessIndexDefine.WifiDevice.IndexNameNew, 
				BusinessIndexDefine.WifiDevice.Type, message, new IteratorNotify<Page<WifiDeviceDocument>>(){
			@Override
			public void notifyComming(Page<WifiDeviceDocument> pages) {
				for(WifiDeviceDocument doc : pages){
					System.out.println("test0013:"+doc.getId());
				}
				System.out.println(pages.getTotalElements());
			}
		});
	}
	
	//@Test
//	public void test0014SearchTest(){
//		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchPageByUidAndDut(100153, "TU", 0, 10);
//    	System.out.println("test0014SearchTest" + result.getTotalElements());
//		for(WifiDeviceDocument doc : result){
//    	    System.out.println("test0014SearchTest:"+ doc.getId() + " = " + doc.getD_lastregedat());
//    	}
//	}
	
	//@Test
	public void test0015SearchTest(){
		SearchCondition sc_d_online = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_ONLINE.getName(), SearchConditionPattern.StringEqual.getPattern(), 
				WifiDeviceDocumentEnumType.OnlineEnum.Online.getType());
		SearchConditionPack pack_must_1 = SearchConditionPack.builderSearchConditionPackWithConditions(sc_d_online);
		
		SearchCondition sc_u_id_1 = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should, 
				BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(),
				String.valueOf("100153"));
		SearchCondition sc_u_id_2 = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should, 
				BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(),
				String.valueOf("100019"));
		SearchConditionPack pack_must_2 = SearchConditionPack.builderSearchConditionPackWithConditions(sc_u_id_1, sc_u_id_2);
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must_1, pack_must_2);
		
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(scm, 0, 10);
    	System.out.println("test0015SearchTest" + result.getTotalElements());
		for(WifiDeviceDocument doc : result){
    	    System.out.println("test0015SearchTest:"+ doc.getId() + " = " + doc.getD_lastregedat());
    	}
	}
	//商业wifi功能测试
	@Test
	public void test0016SearchTest(){
		//判断为商业wifi
		SearchCondition sc_d_dut = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), 
				VapEnumType.DUT_CWifi);
//		SearchCondition sc_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(),
//				String.valueOf("3"));
//		SearchCondition sc_d_onlinestatus = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_ONLINE.getName(), SearchConditionPattern.StringEqual.getPattern(),
//				WifiDeviceDocumentEnumType.OnlineEnum.Online.getType());
		//必须满足此条件
		SearchConditionPack pack_must_1 = SearchConditionPack.builderSearchConditionPackWithConditions(
				sc_d_dut);
		
		String content = "66.";
		SearchCondition sc_u_dnick = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
				BusinessIndexDefine.WifiDevice.Field.U_DNICK.getName(), SearchConditionPattern.Contain.getPattern(),
				content);
		SearchCondition sc_d_mac = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
				BusinessIndexDefine.WifiDevice.Field.D_MAC.getName(), SearchConditionPattern.Contain.getPattern(),
				content);
		SearchCondition sc_d_ip = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
				BusinessIndexDefine.WifiDevice.Field.D_WANIP.getName(), SearchConditionPattern.Contain.getPattern(),
				content);
		SearchCondition sc_d_origmodel = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
				BusinessIndexDefine.WifiDevice.Field.D_ORIGMODEL.getName(), SearchConditionPattern.Contain.getPattern(),
				content);
		SearchCondition sc_d_origswver = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
				BusinessIndexDefine.WifiDevice.Field.D_ORIGSWVER.getName(), SearchConditionPattern.Contain.getPattern(),
				content);
		SearchCondition sc_d_workmodel = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
				BusinessIndexDefine.WifiDevice.Field.D_WORKMODEL.getName(), SearchConditionPattern.StringEqual.getPattern(),
				content);
		SearchConditionPack pack_must_2 = SearchConditionPack.builderSearchConditionPackWithConditions(
				sc_u_dnick, sc_d_mac, sc_d_ip, sc_d_origmodel, sc_d_origswver, sc_d_workmodel);
//		SearchCondition sc_u_id_1 = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should, 
//				BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(),
//				String.valueOf("100153"));
//		SearchCondition sc_u_id_2 = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should, 
//				BusinessIndexDefine.WifiDevice.Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(),
//				String.valueOf("100019"));
//		SearchConditionPack pack_must_2 = SearchConditionPack.builderSearchConditionPackWithConditions(sc_u_id_1, sc_u_id_2);
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must_1, pack_must_2);
		System.out.println("JSON test0016:"+ JsonHelper.getJSONString(scm));
		Page<WifiDeviceDocument> result = wifiDeviceDataSearchService.searchByConditionMessage(scm, 0, 10);
    	System.out.println("test0016SearchTest" + result.getTotalElements());
		for(WifiDeviceDocument doc : result){
    	    System.out.println("test0016SearchTest:"+ doc.getId() + " = " + doc.getD_lastregedat());
    	}
	}
}
