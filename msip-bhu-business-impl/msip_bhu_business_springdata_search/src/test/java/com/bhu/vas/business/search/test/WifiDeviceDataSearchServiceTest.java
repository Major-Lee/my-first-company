package com.bhu.vas.business.search.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.index.query.FilterBuilders;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.localunit.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WifiDeviceDataSearchServiceTest extends BaseTest{
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	
	@BeforeClass
    public static void setUp() throws Exception {
		System.out.println("setUp");
		Thread.sleep(1000);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	System.out.println("tearDown");
    	Thread.sleep(1000);
    }
    
    
    @Test
	public void test002SearchDocument(){
    	Page<WifiDeviceDocument> searchByKeywords = wifiDeviceDataSearchService.searchByKeywords(null, null, null, null, "北京市", null,null,null, null, null, 
    			null, null, null, null, null, null, 0, 4);
    	System.out.println("test002SearchDocument 地址");
    	System.out.println(searchByKeywords.getTotalElements());
    	System.out.println(searchByKeywords.getContent().size());
    	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    	}
    	System.out.println("test002SearchDocument canOperateable");
    	searchByKeywords = wifiDeviceDataSearchService.searchByKeywords(null, null, null, null, null, null,null,null, null, null, 
    			null, Boolean.TRUE, null, null, null, null, 0, 10);
    	System.out.println(searchByKeywords.getTotalElements());
    	System.out.println(searchByKeywords.getContent().size());
    	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    	}
    }    
    
    @Test
   	public void test003SearchOnline(){
       	Page<WifiDeviceDocument> searchByKeywords = wifiDeviceDataSearchService.getRepository().findByOnlineTrue(new PageRequest(1,2));
       	System.out.println(searchByKeywords.getTotalElements());
       	System.out.println(searchByKeywords.getContent().size());
    }
    
    @Test
   	public void test004SearchGroup(){
       	Page<WifiDeviceDocument> searchByKeywords = wifiDeviceDataSearchService.getRepository().findByGroups("1 2",new PageRequest(0,5));
       	System.out.println("test004SearchGroup1");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    	}
       	
       	searchByKeywords = wifiDeviceDataSearchService.getRepository().findByGroups("2",new PageRequest(0,5));
       	System.out.println("test004SearchGroup2");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    	}
    }
    
    @Test
   	public void test005RegisteratGreaterThen(){
       	Page<WifiDeviceDocument> searchByKeywords = wifiDeviceDataSearchService.findByRegisteredatGreaterThan(1438169971000l,0,5);
       	System.out.println("test005RegisteratGreaterThen");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getRegisteredat());
    	}
    }
    
    @Test
   	public void test006CountByAddress(){
       	Long count = wifiDeviceDataSearchService.countByAddressMatchAll("北京市 海淀区 荷清路");//findByRegisteratGreaterThan(1438169971000l,0,5);
       	System.out.println("test005CountByAddress");
       	System.out.println(count);
       	
       	count = wifiDeviceDataSearchService.countByAddressMatchAll("北京市");//findByRegisteratGreaterThan(1438169971000l,0,5);
       	System.out.println("test005CountByAddress");
       	System.out.println(count);
       	
       	count = wifiDeviceDataSearchService.countByAddressMatchAll(null);//findByRegisteratGreaterThan(1438169971000l,0,5);
       	System.out.println("test005CountByAddress");
       	System.out.println(count);
       	
    }
    
    @Test
   	public void test007SearchByAddress(){
    	Page<WifiDeviceDocument> searchByKeywords = wifiDeviceDataSearchService.searchByAddressMatchEach("北京市 海淀区 荷清路",0,10);//findByRegisteratGreaterThan(1438169971000l,0,5);
       	System.out.println("test007SearchByAddress");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    	}
    } 
    
     
    
    
    @Test
   	public void test008SearchGeoInRectangle(){
    	Page<WifiDeviceDocument> searchByKeywords = 
    			wifiDeviceDataSearchService.searchGeoInBoundingBox(new double[]{40.06104160344549,116.27770453955074}, new double[]{40.04100280909214,116.31143600012203}, 0, 10);
    			//wifiDeviceDataSearchService.searchGeoInRectangle(new double[]{38.052107,110.295214}, new double[]{45.052107,119.295214}, 0, 10);
       	System.out.println("test008SearchGeoInRectangle");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    	}
    } 
    @Test
   	public void test009SearchGeoInRectangle(){
		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
			.withFilter(
					FilterBuilders.geoBoundingBoxFilter("geopoint")
					.topLeft(40.06104160344549,116.27770453955074)
					.bottomRight(40.04100280909214,116.31143600012203));
		//When
		List<WifiDeviceDocument> geoAuthorsForGeoCriteria = wifiDeviceDataSearchService.getElasticsearchTemplate().queryForList(queryBuilder.build(), WifiDeviceDocument.class);
       	System.out.println("test009SearchGeoInRectangle");
       	for(WifiDeviceDocument doc:geoAuthorsForGeoCriteria){
    		System.out.println(doc.getAddress());
    	}
    }
    
    @Test
   	public void test010SearchGeoInRangeBox(){
		/*NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
			.withFilter(
					FilterBuilders.geoBoundingBoxFilter("geopoint")
					.topLeft(40.06104160344549,116.27770453955074)
					.bottomRight(40.04100280909214,116.31143600012203));
		//When
		List<WifiDeviceDocument> geoAuthorsForGeoCriteria = wifiDeviceDataSearchService.getElasticsearchTemplate().queryForList(queryBuilder.build(), WifiDeviceDocument.class);
       	System.out.println("test009SearchGeoInRectangle");
       	for(WifiDeviceDocument doc:geoAuthorsForGeoCriteria){
    		System.out.println(doc.getAddress());
    	}*/
    	Page<WifiDeviceDocument> searchByKeywords = 
    			wifiDeviceDataSearchService.searchGeoInRangeBox(new double[]{40.052107,116.29521399999999},"10km", 0, 10);
    			//wifiDeviceDataSearchService.searchGeoInRectangle(new double[]{38.052107,110.295214}, new double[]{45.052107,119.295214}, 0, 10);
       	System.out.println("test010SearchGeoInRangeBox");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    		System.out.println(doc.getGeopoint()[0]+" ----- "+doc.getGeopoint()[1]);
    	}
    }
    
   /* @Test
    public void test011Refresh(){
    	System.out.println("test011Refresh0");
    	System.out.println("before refresh false:"+wifiDeviceDataSearchService.getSetting());
    	//wifiDeviceDataSearchService.refresh(false);
    	wifiDeviceDataSearchService.disableRefreshInterval("wifi_device_index3");
    	System.out.println("after refresh false:"+wifiDeviceDataSearchService.getSetting());
		WifiDeviceDocument doc5 = new WifiDeviceDocument();
		doc5.setId("84:82:f4:23:09:c9");
		doc5.setSn("BN207DE100063BB");
		doc5.setAddress("北京市西城区里仁街31号院-41号楼");
		doc5.setCount(5);
		doc5.setOnline(true);
		//doc5.setGeopoint(new double[]{116.377757, 39.882544});
		doc5.setGeopoint(new double[]{116.377757,39.882544});
		doc5.setConfigmodel("basic");
		doc5.setWorkmodel("router-ap");
		doc5.setOrigswver("AP106P06V1.2.15Build8064");
		doc5.setDevicetype("H106");
		doc5.setGroups("1 2 5");
		doc5.setRegisteredat(DateTimeHelper.getDateDaysAgo(6).getTime());
		doc5.setUpdatedat(DateTimeHelper.getDateTime());
		wifiDeviceDataSearchService.getRepository().save(doc5);
		
		Page<WifiDeviceDocument> searchByKeywords = wifiDeviceDataSearchService.getRepository().findByGroups("5",new PageRequest(0,5));
       	System.out.println("test011Refresh1");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    	}
       	System.out.println("after refresh true:"+wifiDeviceDataSearchService.getSetting());
       	//wifiDeviceDataSearchService.refresh(true);
       	wifiDeviceDataSearchService.openRefreshInterval("wifi_device_index3", "1s");
       	System.out.println("after refresh true:"+wifiDeviceDataSearchService.getSetting());
       	searchByKeywords = wifiDeviceDataSearchService.getRepository().findByGroups("5",new PageRequest(0,5));
       	System.out.println("test011Refresh2");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    	}
    }*/
    
    @Test
 	public void test000BatchEmptyDocument(){
    	//wifiDeviceDataSearchService.getRepository().deleteAll();
 	}
    
    @Test
	public void test001BatchCreateDocument(){
    	//wifiDeviceDataSearchService.refresh(false);
    	
		//List<WifiDeviceDocument> docs = new ArrayList<>();
    	List<IndexQuery> indexQuerys = new ArrayList<IndexQuery>();
		WifiDeviceDocument doc1 = new WifiDeviceDocument();
		doc1.setId("62:68:75:10:11:80");
		doc1.setSn("BN009BC100053AA");
		doc1.setAddress("中国北京市海淀区上地西路6号");
		doc1.setCount(1);
		doc1.setOnline(false);
		//doc1.setGeopoint(new double[]{116.345451, 40.016870});
		doc1.setGeopoint(new double[]{116.29770308996578,40.05151569815288});
		doc1.setConfigmodel("basic");
		doc1.setWorkmodel("router-ap");
		doc1.setOrigswver("AP106P06V1.2.15Build7631");
		doc1.setDevicetype("H106");
		doc1.setGroups("2 3");
		doc1.setRegisteredat(DateTimeHelper.getDateDaysAgo(10).getTime());
		doc1.setUpdatedat(DateTimeHelper.getDateTime());
		
		WifiDeviceDocument doc2 = new WifiDeviceDocument();
		doc2.setId("84:82:f4:23:06:8c");
		doc2.setSn("BN207DE100048AA");
		doc2.setAddress("中国北京市海淀区开拓路1号");
		doc2.setCount(2);
		doc2.setOnline(true);
		//doc2.setGeopoint(new double[]{116.416750, 39.996959});
		doc2.setGeopoint(new double[]{116.30431205297847,40.0446825046788});
		doc2.setConfigmodel("basic");
		doc2.setWorkmodel("router-ap");
		doc2.setOrigswver("AP106P06V1.2.15Build8084");
		doc2.setDevicetype("H106");
		doc2.setGroups("1 3");
		doc2.setRegisteredat(DateTimeHelper.getDateDaysAgo(9).getTime());
		doc2.setUpdatedat(DateTimeHelper.getDateTime());
		
		WifiDeviceDocument doc3 = new WifiDeviceDocument();
		doc3.setId("84:82:f4:23:06:a4");
		doc3.setSn("BN207DE100054AA");
		doc3.setAddress("中国北京市海淀区软件园三号路");
		doc3.setCount(3);
		doc3.setOnline(true);
		//doc3.setGeopoint(new double[]{116.295214, 40.052107});
		doc3.setGeopoint(new double[]{ 116.295214,40.052107});
		doc3.setConfigmodel("basic");
		doc3.setWorkmodel("router-ap");
		doc3.setOrigswver("AP106P06V1.2.15Build8064");
		doc3.setDevicetype("H106");
		doc3.setGroups("1 2 3");
		doc3.setRegisteredat(DateTimeHelper.getDateDaysAgo(8).getTime());
		doc3.setUpdatedat(DateTimeHelper.getDateTime());
		
		
		WifiDeviceDocument doc4 = new WifiDeviceDocument();
		doc4.setId("84:82:f4:23:06:b4");
		doc4.setSn("BN207DE100058AA");
		doc4.setAddress("江西省南昌市新建县琴江路");
		doc4.setCount(4);
		doc4.setOnline(true);
		//doc4.setGeopoint(new double[]{115.850464, 28.668567});
		doc4.setGeopoint(new double[]{115.850464,28.668567});
		doc4.setConfigmodel("basic");
		doc4.setWorkmodel("router-ap");
		doc4.setOrigswver("AP106P06V1.2.15Build8084");
		doc4.setDevicetype("H106");
		doc4.setGroups("1 3 20");
		doc4.setRegisteredat(DateTimeHelper.getDateDaysAgo(7).getTime());
		doc4.setUpdatedat(DateTimeHelper.getDateTime());
		
		WifiDeviceDocument doc5 = new WifiDeviceDocument();
		doc5.setId("84:82:f4:23:06:c8");
		doc5.setSn("BN207DE100063AA");
		doc5.setAddress("北京市西城区里仁街3号院-4号楼");
		doc5.setCount(5);
		doc5.setOnline(true);
		//doc5.setGeopoint(new double[]{116.377757, 39.882544});
		doc5.setGeopoint(new double[]{116.377757,39.882544});
		doc5.setConfigmodel("basic");
		doc5.setWorkmodel("router-ap");
		doc5.setOrigswver("AP106P06V1.2.15Build8064");
		doc5.setDevicetype("H106");
		doc5.setGroups("1 2");
		doc5.setRegisteredat(DateTimeHelper.getDateDaysAgo(6).getTime());
		doc5.setUpdatedat(DateTimeHelper.getDateTime());
		
//		docs.add(doc1);
//		docs.add(doc2);
//		docs.add(doc3);
//		docs.add(doc4);
//		docs.add(doc5);
		indexQuerys.add(new IndexQueryBuilder().withId(doc1.getId()).withObject(doc1).build());
		indexQuerys.add(new IndexQueryBuilder().withId(doc2.getId()).withObject(doc2).build());
		indexQuerys.add(new IndexQueryBuilder().withId(doc3.getId()).withObject(doc3).build());
		indexQuerys.add(new IndexQueryBuilder().withId(doc4.getId()).withObject(doc4).build());
		indexQuerys.add(new IndexQueryBuilder().withId(doc5.getId()).withObject(doc5).build());
		//wifiDeviceDataSearchService.getRepository().save(docs);
		wifiDeviceDataSearchService.getElasticsearchTemplate().bulkIndex(indexQuerys);
		
		//wifiDeviceDataSearchService.refresh(true);
	}
}
