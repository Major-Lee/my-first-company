package com.bhu.vas.business.search.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.data.domain.Page;

import com.bhu.vas.api.dto.search.condition.SearchCondition;
import com.bhu.vas.api.dto.search.condition.SearchConditionPattern;
import com.bhu.vas.api.dto.search.condition.SearchConditionSortPattern;
import com.bhu.vas.api.dto.search.condition.payload.SearchConditionRangePayload;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.model.WifiDeviceDocument1;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService1;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WifiDeviceDataSearchService1Test extends BaseTest{
	@Resource
	private WifiDeviceDataSearchService1 wifiDeviceDataSearchService1;
	
	
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
    
    //@Test
	public void test001BatchCreateDocument(){
    	//wifiDeviceDataSearchService.refresh(false);
    	
		List<WifiDeviceDocument1> docs = new ArrayList<WifiDeviceDocument1>();
    	//List<IndexQuery> indexQuerys = new ArrayList<IndexQuery>();
		WifiDeviceDocument1 doc1 = new WifiDeviceDocument1();
		doc1.setId("84:82:f4:0a:64:68");
		doc1.setD_sn("BN205CD100343AA");
		doc1.setD_origswver("AP201P07V1.2.14z2");
		doc1.setD_origvapmodule("H106V1.3.2M8888");
		doc1.setD_workmodel("bridge-ap");
		doc1.setD_configmodel("basic");
		doc1.setD_type("H201");
		doc1.setD_geopoint(new double[]{109.407456,24.315300});
		doc1.setD_address("广西壮族自治区柳州市柳南区西堤路");
		doc1.setD_online("0");
		doc1.setD_monline(false);
		doc1.setD_hoc(0);
		doc1.setD_lastregedat(0l);
		doc1.setD_lastlogoutat(0l);
		doc1.setD_dut("TU");
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
		
		
		WifiDeviceDocument1 doc2 = new WifiDeviceDocument1();
		doc2.setId("62:68:75:00:00:01");
		doc2.setD_sn("BN007BF106629AA");
		doc2.setD_origswver("AP304P07V1.2.18");
		doc2.setD_origvapmodule("H106V1.3.2M8888");
		doc2.setD_workmodel("router-ap");
		doc2.setD_configmodel("wwan");
		doc2.setD_type("H304");
		doc2.setD_geopoint(new double[]{116.40387397,39.91488908});
		doc2.setD_address("北京市东城区中华路甲10号");
		doc2.setD_online("1");
		doc2.setD_monline(true);
		doc2.setD_hoc(10);
		doc2.setD_lastregedat(1446311400000l);//2015-11-01 01:10:00
		doc2.setD_lastlogoutat(1446310920000l);//2015-11-01 01:02:00
		doc2.setD_dut("TC");
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
		
		WifiDeviceDocument1 doc3 = new WifiDeviceDocument1();
		doc3.setId("12:d3:7f:be:34:12");
		doc3.setD_sn("BN007BD100075AA");
		doc3.setD_origswver("AP304P07V1.2.18Build8578");
		doc3.setD_origvapmodule("H305V1.2.17M8578");
		doc3.setD_workmodel("router-ap");
		doc3.setD_configmodel("wwan");
		doc3.setD_type("H305");
		doc3.setD_geopoint(new double[]{116.345581,40.017058});
		doc3.setD_address("北京市海淀区双清路");
		doc3.setD_online("2");
		doc3.setD_monline(false);
		doc3.setD_hoc(0);
		doc3.setD_lastregedat(1446311400000l);//2015-11-01 01:10:00
		doc3.setD_lastlogoutat(1446313800000l);//2015-11-01 01:50:00
		doc3.setD_dut("TC");
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
		
		WifiDeviceDocument1 doc4 = new WifiDeviceDocument1();
		doc4.setId("84:82:f4:05:52:14");
		doc4.setD_sn("BN007BD100075AA");
		doc4.setD_origswver("AP201P07V1.2.14r3");
		doc4.setD_origvapmodule("H305V1.2.17M8579");
		doc4.setD_workmodel("router-ap");
		doc4.setD_configmodel("wwan");
		doc4.setD_type("H305");
		doc4.setD_geopoint(new double[]{116.345581,40.017058});
		doc4.setD_address("北京市海淀区双清路");
		doc4.setD_online("1");
		doc4.setD_monline(true);
		doc4.setD_hoc(12);
		doc4.setD_lastregedat(1446312300000l);//2015-11-01 01:25:00
		doc4.setD_lastlogoutat(1446310800000l);//2015-11-01 01:00:00
		doc4.setD_dut("TC");
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
		
		WifiDeviceDocument1 doc5 = new WifiDeviceDocument1();
		doc5.setId("84:82:f4:0a:60:a8");
		doc5.setD_sn("BN007BD100075AA");
		doc5.setD_origswver("AP201P07V1.2.14r3");
		doc5.setD_origvapmodule("H305V1.2.17M8579");
		doc5.setD_workmodel("router-ap");
		doc5.setD_configmodel("wwan");
		doc5.setD_type("H305");
		doc5.setD_geopoint(new double[]{116.345581,40.017058});
		doc5.setD_address("北京市海淀区双清路");
		doc5.setD_online("1");
		doc5.setD_monline(true);
		doc5.setD_hoc(12);
		doc5.setD_lastregedat(1446312300000l);//2015-11-01 01:25:00
		doc5.setD_lastlogoutat(1446310800000l);//2015-11-01 01:00:00
		doc5.setD_dut("TC");
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
		
		wifiDeviceDataSearchService1.getRepository().save(docs);
	}
	
	/**
	 * 满足条件
	 * 1：设备从未上线
	 * 2：设备业务线为urouter
	 */
	//@Test
	public void test002SearchConditionDocument(){
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备从未上线
		SearchCondition sc_neveronline = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "0");
		//设备业务线为urouter
		SearchCondition sc_urouter = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.Equal.getPattern(), "TU");
		searchConditions.add(sc_neveronline);
		searchConditions.add(sc_urouter);
		
		Page<WifiDeviceDocument1> result = wifiDeviceDataSearchService1.searchByCondition(searchConditions, 0, 10);
    	for(WifiDeviceDocument1 doc : result){
    	    System.out.println(doc.getId());
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
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备在线
		SearchCondition sc_neveronline = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "1");
		//设备业务线为商业wifi
		SearchCondition sc_urouter = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.Equal.getPattern(), "TC");
		//设备灰度为二级
		SearchCondition sc_graylevel2 = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.O_GRAYLEVEL.getName(), SearchConditionPattern.Equal.getPattern(), "gl2");
		searchConditions.add(sc_neveronline);
		searchConditions.add(sc_urouter);
		searchConditions.add(sc_graylevel2);
		
		Page<WifiDeviceDocument1> result = wifiDeviceDataSearchService1.searchByCondition(searchConditions, 0, 10);
    	for(WifiDeviceDocument1 doc : result){
    	    System.out.println(doc.getId());
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
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备在线
		SearchCondition sc_neveronline = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "1");
		//设备业务线为商业wifi
		SearchCondition sc_urouter = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.Equal.getPattern(), "TC");
		//设备灰度为一级或二级
		SearchCondition sc_graylevel2 = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.O_GRAYLEVEL.getName(), SearchConditionPattern.StringEqual.getPattern(), "gl1 gl2");
		searchConditions.add(sc_neveronline);
		searchConditions.add(sc_urouter);
		searchConditions.add(sc_graylevel2);
		
		Page<WifiDeviceDocument1> result = wifiDeviceDataSearchService1.searchByCondition(searchConditions, 0, 10);
    	for(WifiDeviceDocument1 doc : result){
    	    System.out.println(doc.getId());
    	}
    	
   	
//    	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//    	boolQuery.must(QueryBuilders.termQuery(BusinessIndexDefine.WifiDevice.
//				Field1.D_ONLINE.getName(), "1"));
//    	boolQuery.must(QueryBuilders.termQuery(BusinessIndexDefine.WifiDevice.
//				Field1.D_DEVICEUNITTYPE.getName(), "TC"));
    	
//    	BoolQueryBuilder boolQuery2 = QueryBuilders.boolQuery();
//    	boolQuery2.should(QueryBuilders.termQuery(BusinessIndexDefine.WifiDevice.
//				Field1.O_GRAYLEVEL.getName(), "GL1"));
//    	boolQuery2.should(QueryBuilders.termQuery(BusinessIndexDefine.WifiDevice.
//				Field1.O_GRAYLEVEL.getName(), "GL2"));
    	
//    	boolQuery2.must(boolQuery2);
    	
//    	boolQuery.must(QueryBuilders.queryStringQuery("GL1 GL2").field(BusinessIndexDefine.WifiDevice.
//				Field1.O_GRAYLEVEL.getName()));
    	
    			
//    	result = wifiDeviceDataSearchService1.getRepository().search(FilterBuilders.boolFilter(), new PageRequest(0,10));
//        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//        		.withQuery(QueryBuilders.boolQuery())
//        		//.withFilter(FilterBuilders.boolFilter())
//                .withPageable(new PageRequest(0,10))
//                .build();
//        
//        result = wifiDeviceDataSearchService1.getRepository().search(searchQuery);
//    	for(WifiDeviceDocument1 doc : result){
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
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		
		/*********************************   实例一  *****************************************/
		//设备最后上线时间在2015-11-01 01:20:00 (1446312000000l) 和 2015-11-01 01:30:00 (1446312600000l)之间
		SearchConditionRangePayload rangeBetweenPayload = SearchConditionRangePayload.
				buildRangBetweenPayload(String.valueOf(1446312000000l), String.valueOf(1446312600000l));
		
		SearchCondition sc_betweenAndLastRegedAt = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_LASTREGEDAT.getName(), SearchConditionPattern.Between.getPattern(), 
				JsonHelper.getJSONString(rangeBetweenPayload));
		
		searchConditions.add(sc_betweenAndLastRegedAt);
		
		Page<WifiDeviceDocument1> result = wifiDeviceDataSearchService1.searchByCondition(searchConditions, 0, 10);
    	for(WifiDeviceDocument1 doc : result){
    	    System.out.println("实例一:" + doc.getId() + " = " + doc.getD_lastregedat());
    	}
    	
    	System.out.println("----------------------------------------------------------------");
    	/*********************************   实例二  *****************************************/
    	searchConditions.clear();
    	//设备最后上线时间不存在2015-11-01 01:20:00 (1446312000000l) 和 2015-11-01 01:30:00 (1446312600000l)之间
		SearchCondition sc_notBetweenAndLastRegedAt = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_LASTREGEDAT.getName(), SearchConditionPattern.NotBetween.getPattern(), 
				JsonHelper.getJSONString(rangeBetweenPayload));
		
		searchConditions.add(sc_notBetweenAndLastRegedAt);
		
		result = wifiDeviceDataSearchService1.searchByCondition(searchConditions, 0, 10);
    	for(WifiDeviceDocument1 doc : result){
    	    System.out.println("实例二:" + doc.getId() + " = " + doc.getD_lastregedat());
    	}
    	
	}
	
	
	/**
	 * 满足条件
	 * 1：已经绑定用户的设备
	 * 2：设备在线状态为在线或离线
	 * 3：代理商的公司名称中包含"公司"两个字
	 * 4：导入的批次是20151104开头的
	 * 5：按照mac地址降序排序
	 */
	@Test
	public void test006SearchConditionDocument(){
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//已经绑定用户的设备
		SearchCondition sc_existingUid = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.U_ID.getName(), SearchConditionPattern.Existing.getPattern(), null);
		//设备在线状态为在线或离线
		SearchCondition sc_onlineOrOffline = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_ONLINE.getName(), SearchConditionPattern.StringEqual.getPattern(), "1 2");
		//代理商的公司名称中包含"公司"两个字
		SearchCondition sc_containOrgName = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.A_ORG.getName(), SearchConditionPattern.Contain.getPattern(), "公司");
		//导入的批次是20151104
		SearchCondition sc_equalBatch = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.O_BATCH.getName(), SearchConditionPattern.PrefixContain.getPattern(), "20151104");
		//按照mac地址降序排序
		SearchCondition sc_sortDescById = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.ID.getName(), SearchConditionSortPattern.SortDesc.getPattern(), null);
		
		searchConditions.add(sc_existingUid);
		searchConditions.add(sc_onlineOrOffline);
		searchConditions.add(sc_containOrgName);
		searchConditions.add(sc_equalBatch);
		searchConditions.add(sc_sortDescById);

		Page<WifiDeviceDocument1> result = wifiDeviceDataSearchService1.searchByCondition(searchConditions, 0, 10);
    	for(WifiDeviceDocument1 doc : result){
    	    System.out.println("test006:"+ doc.getId());
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
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备按照软件版本号倒序排序
		SearchCondition sc_sortDescByOrigswver = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_ORIGSWVER.getName(), SearchConditionSortPattern.SortDesc.getPattern(), null);
		
		searchConditions.add(sc_sortDescByOrigswver);
		
		Page<WifiDeviceDocument1> result = wifiDeviceDataSearchService1.searchByCondition(searchConditions, 0, 10);
    	for(WifiDeviceDocument1 doc : result){
    	    System.out.println(doc.getId() + " = " + doc.getD_origswver());
    	}
	}
	
	/**
	 * 满足条件
	 * 1:设备按照软件版本号大于AP201P07V1.2.14z2匹配
	 * 2:设备按照软件版本号倒序排序
	 */
	//@Test
	public void test0011SearchConditionDocument(){
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备按照软件版本号大于AP201P07V1.2.14z2匹配
		SearchConditionRangePayload rangeGreaterPayload = SearchConditionRangePayload.buildRangGreaterPayload("AP201P07V1.2.14z2");
		SearchCondition sc_greaterThanByOrigswver = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_ORIGSWVER.getName(), SearchConditionPattern.GreaterThan.getPattern(), 
				JsonHelper.getJSONString(rangeGreaterPayload));
		//设备按照软件版本号倒序排序
		SearchCondition sc_sortDescByOrigswver = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_ORIGSWVER.getName(), SearchConditionSortPattern.SortDesc.getPattern(), null);
		
		searchConditions.add(sc_greaterThanByOrigswver);
		searchConditions.add(sc_sortDescByOrigswver);
		
		Page<WifiDeviceDocument1> result = wifiDeviceDataSearchService1.searchByCondition(searchConditions, 0, 10);
    	for(WifiDeviceDocument1 doc : result){
    	    System.out.println(doc.getId() + " = " + doc.getD_origswver());
    	}
    	
    	
//    	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//    	boolQuery.must(QueryBuilders.rangeQuery(BusinessIndexDefine.WifiDevice.
//				Field1.D_ORIGSWVER.getName()).lt("AP201P07V1.2.14z2"));
//    	
//    	result = wifiDeviceDataSearchService1.getRepository().search(boolQuery, new PageRequest(0,10));
//    	for(WifiDeviceDocument1 doc : result){
//    	    System.out.println(doc.getId() + " = " + doc.getD_origswver());
//    	}

	}
}
