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
import org.springframework.data.domain.PageRequest;

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
    	Page<WifiDeviceDocument> searchByKeywords = wifiDeviceDataSearchService.searchByKeywords(null, null, null, "北京市", null, null, null, 
    			null, null, null, null, null, null, 0, 4);
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
       	Page<WifiDeviceDocument> searchByKeywords = wifiDeviceDataSearchService.getRepository().findByGroups("1 2",new PageRequest(0,2));
       	System.out.println(searchByKeywords.getTotalElements());
       	System.out.println(searchByKeywords.getContent().size());
    }
    
    @Test
   	public void test005RegisteratGreaterThen(){
       	Page<WifiDeviceDocument> searchByKeywords = wifiDeviceDataSearchService.findByRegisteratGreaterThan(1438169971000l,0,5);
       	System.out.println("test005RegisteratGreaterThen");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getRegisterat());
    	}
    }
    
    @Test
   	public void test006CountByAddress(){
       	Long count = wifiDeviceDataSearchService.countByAddressMatchAll("北京市 海淀区 荷清路");//findByRegisteratGreaterThan(1438169971000l,0,5);
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
    			wifiDeviceDataSearchService.searchGeoInRectangle(new double[]{0,0}, new double[]{150,50}, 0, 10);
    			//wifiDeviceDataSearchService.searchGeoInRectangle(new double[]{38.052107,110.295214}, new double[]{45.052107,119.295214}, 0, 10);
       	System.out.println("test008SearchGeoInRectangle");
       	for(WifiDeviceDocument doc:searchByKeywords){
    		System.out.println(doc.getAddress());
    	}
    } 
    
    @Test
 	public void test000BatchEmptyDocument(){
    	wifiDeviceDataSearchService.getRepository().deleteAll();
 	}
    
    @Test
	public void test001BatchCreateDocument(){
		List<WifiDeviceDocument> docs = new ArrayList<>();
		WifiDeviceDocument doc1 = new WifiDeviceDocument();
		doc1.setId("62:68:75:10:11:80");
		doc1.setSn("BN009BC100053AA");
		doc1.setAddress("北京市海淀区荷清路");
		doc1.setCount(1);
		doc1.setOnline(false);
		//doc1.setGeopoint(new double[]{116.345451, 40.016870});
		doc1.setGeopoint(new double[]{40.016870,116.345451});
		doc1.setConfigmodel("basic");
		doc1.setWorkmodel("router-ap");
		doc1.setOrigswver("AP106P06V1.2.15Build7631");
		doc1.setDevicetype("H106");
		doc1.setGroups("2 3");
		doc1.setRegisterat(DateTimeHelper.getDateDaysAgo(10).getTime());
		doc1.setUpdateat(DateTimeHelper.getDateTime());
		
		WifiDeviceDocument doc2 = new WifiDeviceDocument();
		doc2.setId("84:82:f4:23:06:8c");
		doc2.setSn("BN207DE100048AA");
		doc2.setAddress("北京市朝阳区北四环东路辅路");
		doc2.setCount(2);
		doc2.setOnline(true);
		//doc2.setGeopoint(new double[]{116.416750, 39.996959});
		doc2.setGeopoint(new double[]{39.996959,116.416750});
		doc2.setConfigmodel("basic");
		doc2.setWorkmodel("router-ap");
		doc2.setOrigswver("AP106P06V1.2.15Build8084");
		doc2.setDevicetype("H106");
		doc2.setGroups("1 3");
		doc2.setRegisterat(DateTimeHelper.getDateDaysAgo(9).getTime());
		doc2.setUpdateat(DateTimeHelper.getDateTime());
		
		WifiDeviceDocument doc3 = new WifiDeviceDocument();
		doc3.setId("84:82:f4:23:06:a4");
		doc3.setSn("BN207DE100054AA");
		doc3.setAddress("北京市海淀区软件园三号路");
		doc3.setCount(3);
		doc3.setOnline(true);
		//doc3.setGeopoint(new double[]{116.295214, 40.052107});
		doc3.setGeopoint(new double[]{ 40.052107,116.295214});
		doc3.setConfigmodel("basic");
		doc3.setWorkmodel("router-ap");
		doc3.setOrigswver("AP106P06V1.2.15Build8064");
		doc3.setDevicetype("H106");
		doc3.setGroups("1 2 3");
		doc3.setRegisterat(DateTimeHelper.getDateDaysAgo(8).getTime());
		doc3.setUpdateat(DateTimeHelper.getDateTime());
		
		
		WifiDeviceDocument doc4 = new WifiDeviceDocument();
		doc4.setId("84:82:f4:23:06:b4");
		doc4.setSn("BN207DE100058AA");
		doc4.setAddress("江西省南昌市新建县琴江路");
		doc4.setCount(4);
		doc4.setOnline(true);
		//doc4.setGeopoint(new double[]{115.850464, 28.668567});
		doc4.setGeopoint(new double[]{28.668567,115.850464});
		doc4.setConfigmodel("basic");
		doc4.setWorkmodel("router-ap");
		doc4.setOrigswver("AP106P06V1.2.15Build8084");
		doc4.setDevicetype("H106");
		doc4.setGroups("1 3");
		doc4.setRegisterat(DateTimeHelper.getDateDaysAgo(7).getTime());
		doc4.setUpdateat(DateTimeHelper.getDateTime());
		
		WifiDeviceDocument doc5 = new WifiDeviceDocument();
		doc5.setId("84:82:f4:23:06:c8");
		doc5.setSn("BN207DE100063AA");
		doc5.setAddress("北京市西城区里仁街3号院-4号楼");
		doc5.setCount(5);
		doc5.setOnline(true);
		//doc5.setGeopoint(new double[]{116.377757, 39.882544});
		doc5.setGeopoint(new double[]{39.882544,116.377757});
		doc5.setConfigmodel("basic");
		doc5.setWorkmodel("router-ap");
		doc5.setOrigswver("AP106P06V1.2.15Build8064");
		doc5.setDevicetype("H106");
		doc5.setGroups("1 2");
		doc5.setRegisterat(DateTimeHelper.getDateDaysAgo(6).getTime());
		doc5.setUpdateat(DateTimeHelper.getDateTime());
		
		docs.add(doc1);
		docs.add(doc2);
		docs.add(doc3);
		docs.add(doc4);
		docs.add(doc5);
		wifiDeviceDataSearchService.getRepository().save(docs);
	}
}
