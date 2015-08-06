package com.bhu.vas.business.search.test.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.ElasticsearchException;
import org.junit.Test;

import com.bhu.vas.api.dto.search.WifiDeviceIndexDTO;
import com.bhu.vas.api.dto.search.WifiDeviceSearchDTO;
import com.bhu.vas.business.search.indexable.WifiDeviceIndexableComponent;
import com.bhu.vas.business.search.service.device.WifiDeviceIndexService;
import com.bhu.vas.business.search.service.device.WifiDeviceSearchService;
import com.smartwork.msip.es.exception.ESException;
import com.smartwork.msip.es.exception.ESQueryValidateException;
import com.smartwork.msip.es.request.QueryResponse;
import com.smartwork.msip.localunit.BaseTest;


public class WifiDeviceTest extends BaseTest{
	
	@Resource
	WifiDeviceSearchService wifiDeviceSearchService;
	
	@Resource
	WifiDeviceIndexService wifiDeviceIndexService;
	
	//@Test
	public void init() throws ElasticsearchException, IOException, ESException, InstantiationException, IllegalAccessException{
		//userIndexService.deleteUserResponse();
		wifiDeviceIndexService.createResponseAndMapping();
		
		List<WifiDeviceIndexableComponent> components = new ArrayList<WifiDeviceIndexableComponent>();
		
		WifiDeviceIndexDTO indexDto = new WifiDeviceIndexDTO();
		indexDto.setWifiId("01:05:58:76:48:98");
		indexDto.setCountry("中国");
		indexDto.setCity("北京市");
		indexDto.setProvince("北京市");
		indexDto.setDistrict("西城区");
		indexDto.setStreet("黄寺大街");
		indexDto.setFormat_address("中国北京市西城区黄寺大街");
		indexDto.setCount(1);
		indexDto.setOnline(1);
		indexDto.setLat("40.7143528");
		indexDto.setLon("-74.0059731");
		indexDto.setRegister_at(new Date().getTime());
		components.add(wifiDeviceIndexService.buildIndexableComponent(indexDto));

		//to NY: 5.286 km
		indexDto = new WifiDeviceIndexDTO();
		indexDto.setWifiId("01:05:58:76:48:97");
		indexDto.setCountry("中国");
		indexDto.setCity("北京市");
		indexDto.setProvince("北京市");
		indexDto.setDistrict("西城区");
		indexDto.setStreet("南锣鼓巷");
		indexDto.setFormat_address("中国北京市西城区南锣鼓巷");
		indexDto.setCount(2);
		indexDto.setOnline(1);
		indexDto.setLat("40.759011");
		indexDto.setLon("-73.9844722");
		indexDto.setRegister_at(new Date().getTime());
		components.add(wifiDeviceIndexService.buildIndexableComponent(indexDto));
		
		//to NY: 0.4621 km
		indexDto = new WifiDeviceIndexDTO();
		indexDto.setWifiId("01:05:58:76:48:96");
		indexDto.setCountry("中国");
		indexDto.setCity("北京市");
		indexDto.setProvince("北京市");
		indexDto.setDistrict("西城区");
		indexDto.setStreet("虎坊桥");
		indexDto.setFormat_address("中国北京市西城区虎坊桥");
		indexDto.setCount(3);
		indexDto.setOnline(1);
		indexDto.setLat("40.718266");
		indexDto.setLon("-74.007819");
		indexDto.setRegister_at(new Date().getTime());
		components.add(wifiDeviceIndexService.buildIndexableComponent(indexDto));
		
		//to NY: 1.258 km
		indexDto = new WifiDeviceIndexDTO();
		indexDto.setWifiId("01:05:58:76:48:95");
		indexDto.setCountry("中国");
		indexDto.setCity("北京市");
		indexDto.setProvince("北京市");
		indexDto.setDistrict("西城区");
		indexDto.setStreet("西单");
		indexDto.setFormat_address("中国北京市西城区西单");
		indexDto.setCount(4);
		indexDto.setOnline(1);
		indexDto.setLat("40.7247222");
		indexDto.setLon("-74");
		indexDto.setRegister_at(new Date().getTime());
		components.add(wifiDeviceIndexService.buildIndexableComponent(indexDto));
		
		//to NY: 8.572 km
		indexDto = new WifiDeviceIndexDTO();
		indexDto.setWifiId("01:05:58:76:48:94");
		indexDto.setCountry("中国");
		indexDto.setCity("北京市");
		indexDto.setProvince("北京市");
		indexDto.setDistrict("西城区");
		indexDto.setStreet("东单");
		indexDto.setFormat_address("中国北京市西城区东单");
		indexDto.setCount(5);
		indexDto.setOnline(0);
		indexDto.setLat("40.65");
		indexDto.setLon("-73.95");
		indexDto.setRegister_at(new Date().getTime());
		components.add(wifiDeviceIndexService.buildIndexableComponent(indexDto));
		
		wifiDeviceIndexService.createIndexComponents(components);
	}
	
	//@Test
	public void testSearchByKeyword() throws ESQueryValidateException{
		String keyword = "辽宁";
		String region = "";
		String excepts = "北京市,广东省,浙江省,上海市";
		//String keyword = "西城区";
		QueryResponse<List<WifiDeviceSearchDTO>> result = wifiDeviceSearchService.searchByKeyword(keyword, region, 
				excepts,0, 10);
		System.out.println(result.getTotal());
		for(WifiDeviceSearchDTO dto : result.getResult()){
			System.out.println("id:"+dto.getId() + "="+dto.getAddress()+"="+dto.getLat());
		}
	}
	
	@Test
	public void testSearchByKeywords() throws ESQueryValidateException{
		String mac = "";//"84:82:f4:6f:00";
		String sn = "BJN";
		String orig_swver="";//"V1.2.5";
		String adr=""; 
		String work_mode=""; 
		String config_mode="";//"basic"; 
		String devicetype="";
		String region="";
		String excepts = "";//"北京市,广东省,浙江省,上海市";
		String groupids = "";
		String groupids_excepts = "";
		QueryResponse<List<WifiDeviceSearchDTO>> result = wifiDeviceSearchService.searchByKeywords(mac, sn, orig_swver,
				adr, work_mode, config_mode, devicetype, null, false, region, excepts, groupids, groupids_excepts, 0, 10);
		System.out.println(result.getTotal());
		for(WifiDeviceSearchDTO dto : result.getResult()){
			System.out.println("id:"+dto.getId() + "="+dto.getAddress()+"="+dto.getLat()+"="+dto.getOrigswver()
					+"="+dto.getConfigmodel()+"="+dto.getWorkmodel() + "="+dto.getGroups());
		}
	}
	
	//@Test
	public void countSearchByKeyword() throws ESQueryValidateException{
		//String keyword = "黄寺";
		String keyword = "黄";
		long count = wifiDeviceSearchService.countByKeyword(keyword);
		System.out.println(count);
	}
	
	//@Test
	public void testSearchByDistance() throws ESQueryValidateException{
		double[] coordinate = {40.7143528,-74.0059731};
		String distance = "2km";
		QueryResponse<List<WifiDeviceSearchDTO>> result = wifiDeviceSearchService.searchByGeoDistanceSort(coordinate,
				distance, 0, 10);
		System.out.println(result.getTotal());
		for(WifiDeviceSearchDTO dto : result.getResult()){
			System.out.println("id:"+dto.getId() + "="+dto.getAddress());
		}
	}
	
	//@Test
	public void testCountByDistance() throws ESQueryValidateException{
		double[] coordinate = {40.7143528,-74.0059731};
		String distance = "2km";
		long count = wifiDeviceSearchService.countByGeoDistanceSort(coordinate,distance);
		System.out.println(count);
	}
	
	//@Test
	public void testSearchByGeoBoundingBox() throws ESQueryValidateException{
		double[] topleft_coordinate = {40.651693098789316,-73.95274658203124};
		double[] bottomRight_coordinate = {40.648241736557516,-73.94648094177245};
		QueryResponse<List<WifiDeviceSearchDTO>> result = wifiDeviceSearchService.searchByGeoBoundingBox(
				topleft_coordinate, bottomRight_coordinate, 0, 10);
		System.out.println(result.getTotal());
		for(WifiDeviceSearchDTO dto : result.getResult()){
			System.out.println("id:"+dto.getId() + "="+dto.getAddress());
		}
	}
	
	//@Test
	public void testCountByGeoBoundingBox() throws ESQueryValidateException{
		double[] topleft_coordinate = {40.651693098789316,-73.95274658203124};
		double[] bottomRight_coordinate = {40.648241736557516,-73.94648094177245};
		long count= wifiDeviceSearchService.countByGeoBoundingBox(
				topleft_coordinate, bottomRight_coordinate);
		System.out.println(count);
	}
	
	//@Test
	public void testSearchGtByRegisterAt() throws ESQueryValidateException{
		System.out.println(System.currentTimeMillis());
		System.out.println(new Date().getTime());
		long d30_ts = 30 * 3600 * 24 * 1000l;
		System.out.println(d30_ts);
		long minRegisterAt = System.currentTimeMillis() - d30_ts;
		System.out.println(minRegisterAt);
		QueryResponse<List<WifiDeviceSearchDTO>> result = wifiDeviceSearchService.
				searchGtByRegisterAt(minRegisterAt, 0, 10);
		System.out.println(result.getTotal());
		for(WifiDeviceSearchDTO dto : result.getResult()){
			System.out.println("id:"+dto.getId() + "="+dto.getAddress());
		}
	}
}
