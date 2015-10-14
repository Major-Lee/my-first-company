package com.bhu.vas.business.search.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.SortBuilderHelper;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.repository.WifiDeviceDocumentRepository;
import com.smartwork.msip.cores.helper.StringHelper;

@Service
public class WifiDeviceDataSearchService extends AbstractDataSearchService<WifiDeviceDocument>{
    @Resource
    private WifiDeviceDocumentRepository wifiDeviceDocumentRepository;

	/**
	 * 搜索注册时间大于此时间的数据
	 * @param register_at
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public Page<WifiDeviceDocument> findByRegisteredatGreaterThan(
			long registeredat,
			int pageno, int pagesize){
		return wifiDeviceDocumentRepository.findByRegisteredatGreaterThanOrderByRegisteredatDesc(registeredat, new PageRequest(pageno,pagesize));
	}
	
	/**
	 * 匹配字段address 带分词,搜索不空格分词，匹配中所有的就可以命中
	 * eg。北京市 海淀区 荷清路  三个中都匹配就在结果集中
	 * @param address
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	public Page<WifiDeviceDocument> searchByAddressMatchAll(String address,int pageno, int pagesize){
		return wifiDeviceDocumentRepository.findByAddress(address,new PageRequest(pageno,pagesize));
	}
	
	public Long countByAddressMatchAll(String address){
		return wifiDeviceDocumentRepository.countByAddress(address);
	}
	/**
	 * 匹配字段address 带分词,搜索进行空格分词，匹配中其中一个就可以命中
	 * eg。北京市 海淀区 荷清路  三个中任何一个匹配就在结果集中
	 * @param address
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	public Page<WifiDeviceDocument> searchByAddressMatchEach(String address,int pageno, int pagesize){
	    	BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
	    	queryBuilder.must(QueryBuilders.queryStringQuery(address).field(BusinessIndexDefine.WifiDevice.Field.ADDRESS));
	        SearchQuery searchQuery = new NativeSearchQueryBuilder()
	                .withQuery(queryBuilder)
	                .withPageable(new PageRequest(pageno,pagesize))
	                .build();
	        return wifiDeviceDocumentRepository.search(searchQuery);
	}
	
	/**
	 * region表示 需要包含的地域名称
	 * region_excepts表示 需要不包含的地域名称 逗号分割
	 * 根据keyword进行分词来搜索地理位置
	 * @param keyword 可以是 mac 或 地理名称
	 * @param region 北京市
	 * @param region_excepts 广东省,上海市
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public Page<WifiDeviceDocument> searchByKeyword(String keyword, String region,String region_excepts, int pageno, int pagesize){
		FilterBuilder filter;//QueryBuilders.boolQuery();
		if(StringHelper.hasLeastOneNotEmpty(keyword, region, region_excepts)){
			BoolFilterBuilder boolfilter = FilterBuilders.boolFilter();
			if(StringUtils.isNotEmpty(keyword))	
				boolfilter.must(FilterBuilders.orFilter(
						FilterBuilders.prefixFilter(BusinessIndexDefine.WifiDevice.Field.ID, keyword.toLowerCase()),
						FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.ADDRESS, keyword.toLowerCase())
						));
			if(!StringUtils.isEmpty(region)){
				//String standardRegion = IndexableResolver.standardString(region);
				boolfilter.must(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.ADDRESS, region));
			}
			
			if(!StringUtils.isEmpty(region_excepts)){
				//String standardRegion = IndexableResolver.standardString(excepts);
				String[] except_array = region_excepts.split(StringHelper.COMMA_STRING_GAP);
				for(String except : except_array){
					boolfilter.mustNot(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.ADDRESS, except));
				}
			}
			filter = boolfilter;
		}else{
			filter = FilterBuilders.matchAllFilter();
		}
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withFilter(filter)
				.withPageable(new PageRequest(pageno,pagesize))
				.withSort(SortBuilderHelper.builderSort(BusinessIndexDefine.WifiDevice.Field.ONLINE, SortOrder.DESC))//SortBuilders.fieldSort(BusinessIndexDefine.WifiDevice.Field.ONLINE).order(SortOrder.DESC))
				.withSort(SortBuilderHelper.builderSort(BusinessIndexDefine.WifiDevice.Field.ONLINE, SortOrder.DESC))//SortBuilders.fieldSort(BusinessIndexDefine.WifiDevice.Field.COUNT).order(SortOrder.DESC))
				.build();
		return wifiDeviceDocumentRepository.search(searchQuery);
		
	}
	
	/**
	 * 根据多个条件来进行搜索
	 * @param mac 
	 * @param sn
	 * @param orig_swver 软件版本号
	 * @param adr 位置参数
	 * @param work_mode 工作模式
	 * @param config_mode 配置模式
	 * @param online null表示全部 true为在线 
	 * @param newVersionDevice null 标识全部 true为新版本设备 大于1.2.7的设备 false为老版本 小于等于1.2.7
	 * @param canOperateable   null 标识全部 true为可运营设备 存在的origvapmodule设备 false为不存在
	 * @param region 地区
	 * @param excepts 排除地区
	 * @param groupids 所属群组ids 空格分隔
	 * @param groupids_excepts 排序所属群组ids 空格分隔
	 * @param start
	 * @param size
	 * @return
	 * @throws ESQueryValidateException
	 */
	public Page<WifiDeviceDocument> searchByKeywords(
			String mac, 
			String sn, 
			String orig_swver, 
			String origvapmodule,
			String adr, 
			String work_mode, 
			String config_mode, 
			String devicetype, 
			Boolean online, 
			Boolean moduleonline,
			Boolean newVersionDevice,
			Boolean canOperateable,
			String region, String region_excepts, 
			String groupids, String groupids_excepts, 
			int pageno, int pagesize){
		FilterBuilder filter;//QueryBuilders.boolQuery();
		if(StringHelper.hasLeastOneNotEmpty(mac, sn, orig_swver,origvapmodule, adr, work_mode, config_mode, 
				devicetype, region, region_excepts, groupids, groupids_excepts) || online != null || moduleonline != null || newVersionDevice != null || canOperateable !=null){
			BoolFilterBuilder boolfilter = FilterBuilders.boolFilter();
			if(StringUtils.isNotEmpty(mac))	
				boolfilter.must(FilterBuilders.prefixFilter(BusinessIndexDefine.WifiDevice.Field.ID, mac.toLowerCase()));//QueryBuilders.queryStringQuery("中国 南城").field("address"));
			if(StringUtils.isNotEmpty(sn))
				boolfilter.must(FilterBuilders.prefixFilter(BusinessIndexDefine.WifiDevice.Field.SN, sn));
			if(StringUtils.isNotEmpty(orig_swver))
				boolfilter.must(FilterBuilders.queryFilter(QueryBuilders.wildcardQuery(BusinessIndexDefine.WifiDevice.Field.ORIGSWVER, "*"+orig_swver+"*")));
			if(StringUtils.isNotEmpty(origvapmodule))
				boolfilter.must(FilterBuilders.queryFilter(QueryBuilders.wildcardQuery(BusinessIndexDefine.WifiDevice.Field.ORIGVAPMODULE, "*"+origvapmodule+"*")));
			
			if(StringUtils.isNotEmpty(adr)){
				//boolfilter.must(FilterBuilders.termFilter("address", adr));
				boolfilter.must(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.ADDRESS, adr));
			}
			if(StringUtils.isNotEmpty(work_mode)){
				boolfilter.must(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.WORKMODEL, work_mode));
			}
			if(StringUtils.isNotEmpty(config_mode)){
				boolfilter.must(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.CONFIGMODEL, config_mode));
			}
			if(StringUtils.isNotEmpty(devicetype)){
				boolfilter.must(FilterBuilders.prefixFilter(BusinessIndexDefine.WifiDevice.Field.DEVICETYPE, devicetype));
			}
			if(online != null){
				boolfilter.must(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.ONLINE, online ? 1 : 0));
			}
			
			if(moduleonline != null){
				boolfilter.must(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.MODULEONLINE, moduleonline ? 1 : 0));
			}
			
			if(newVersionDevice != null){
				boolfilter.must(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.NVD, newVersionDevice ? 1 : 0));
			}
			
			if(canOperateable != null){
				if(canOperateable.booleanValue()){
					//existsFilter 字段值不为空
					boolfilter.must(FilterBuilders.existsFilter(BusinessIndexDefine.WifiDevice.Field.ORIGVAPMODULE));//.termFilter(BusinessIndexDefine.WifiDevice.Field.NVD, newVersionDevice ? 1 : 0));
				}else{
					//existsFilter 字段值为空
					boolfilter.must(FilterBuilders.missingFilter(BusinessIndexDefine.WifiDevice.Field.ORIGVAPMODULE));//.termFilter(BusinessIndexDefine.WifiDevice.Field.NVD, newVersionDevice ? 1 : 0));
				}
			}
			
			if(StringUtils.isNotEmpty(region)){
				boolfilter.must(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.ADDRESS, region));
			}
			if(StringUtils.isNotEmpty(region_excepts)){
				String[] except_array = region_excepts.split(StringHelper.COMMA_STRING_GAP);
				for(String except : except_array){
					boolfilter.mustNot(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.ADDRESS, except));
				}
			}
			if(StringUtils.isNotEmpty(groupids)){
				String[] groupids_array = groupids.split(StringHelper.WHITESPACE_STRING_GAP);
				for(String groupid : groupids_array){
					boolfilter.must(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.GROUPS, groupid));
				}
			}
			if(StringUtils.isNotEmpty(groupids_excepts)){
				String[] groupids_array = groupids_excepts.split(StringHelper.WHITESPACE_STRING_GAP);
				for(String groupid : groupids_array){
					boolfilter.mustNot(FilterBuilders.termFilter(BusinessIndexDefine.WifiDevice.Field.GROUPS, groupid));
				}
			}
			filter = boolfilter;
		}else{
			filter = FilterBuilders.matchAllFilter();
		}
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withFilter(filter)
				.withPageable(new PageRequest(pageno,pagesize))
				.withSort(SortBuilderHelper.builderSort(BusinessIndexDefine.WifiDevice.Field.ONLINE, SortOrder.DESC))//SortBuilders.fieldSort(BusinessIndexDefine.WifiDevice.Field.ONLINE).order(SortOrder.DESC))
				.withSort(SortBuilderHelper.builderSort(BusinessIndexDefine.WifiDevice.Field.ONLINE, SortOrder.DESC))//SortBuilders.fieldSort(BusinessIndexDefine.WifiDevice.Field.COUNT).order(SortOrder.DESC))
				.build();
		return wifiDeviceDocumentRepository.search(searchQuery);
	}
	
	/**
	 * 根据geobox上左下右坐标进行选定区域的查询数据
	 */
	
	/**
	 * 建立索引的时候 数组为 经度lon 纬度lat
	 * 查询索引的时候 数组为 纬度lat 经度lon 
	 * @param topLeft lat,lon
	 * @param bottomRight lat,lon
	 * @param page
	 * @param pagesize
	 * @return
	 */
	public Page<WifiDeviceDocument> searchGeoInBoundingBox(double[] topLeft,double[] bottomRight,int page,int pagesize) {
    	//使用ES原生filter方式
    	SearchQuery searchGeoQuery = new NativeSearchQueryBuilder()
    			.withFilter(FilterBuilders.geoBoundingBoxFilter(BusinessIndexDefine.WifiDevice.Field.GEOPOINT)
    							.topLeft(topLeft[0], topLeft[1])
    							.bottomRight(bottomRight[0], bottomRight[1]))
        		.withPageable(new PageRequest(page,pagesize))
        		.build();
    	/*SearchQuery searchGeoQuery = new NativeSearchQueryBuilder()
		.withQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),geoBoundingBoxFilter("geopoint")
				.topLeft(topLeft[0], topLeft[1])
				.bottomRight(bottomRight[0], bottomRight[1])))
		.withPageable(new PageRequest(0,10))
        .build();*/
    	return wifiDeviceDocumentRepository.search(searchGeoQuery);
		
		
		/*CriteriaQuery geoLocationCriteriaQuery3 = new CriteriaQuery(
				new Criteria("geopoint").boundedBy(
						new GeoBox(new GeoPoint(topLeft[0], topLeft[1]),
								new GeoPoint(bottomRight[0], bottomRight[1]))
				)
		).setPageable(new PageRequest(0,10));
		return elasticsearchTemplate.queryForPage(geoLocationCriteriaQuery3, WifiDeviceDocument.class);*/
    	/*//使用ES原生Query方式
        SearchQuery searchQuery2 = new NativeSearchQueryBuilder()
        		.withQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),geoBoundingBoxFilter("geopoint")
        				.topLeft(40.716499651972605, -74.01086544924317)
        				.bottomRight(40.70999382697558, -74.0012524121338)))
        		.withPageable(new PageRequest(0,10))
                .build();
        //使用springdata方式
		CriteriaQuery geoLocationCriteriaQuery3 = new CriteriaQuery(
				new Criteria("geopoint").boundedBy(
						new GeoBox(new GeoPoint(40.716499651972605, -74.01086544924317),
								new GeoPoint(40.70999382697558, -74.0012524121338))
				)
		).setPageable(new PageRequest(0,10));
        //Page<WifiDeviceDocument> document_page = wifiDeviceDocumentRepository.search(searchQuery2);
        //Page<WifiDeviceDocument> geoAuthorsForGeoCriteria = elasticsearchTemplate.queryForPage(geoLocationCriteriaQuery3, WifiDeviceDocument.class);
        //assertThat(documents.getNumberOfElements(), is(equalTo(1)));
        System.out.println(geoAuthorsForGeoCriteria.getContent().size());*/
    }
	
	
	public Page<WifiDeviceDocument> searchGeoInRangeBox(double[] geopoint,String distance,int page,int pagesize) {
		/*CriteriaQuery geoLocationCriteriaQuery = new CriteriaQuery(
				new Criteria("geopoint").within(new GeoPoint(geopoint[0], geopoint[1]), "0.5km"))
				.setPageable(new PageRequest(page,pagesize));*/
		//使用es原生方式进行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
        		.withQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), 
        				FilterBuilders.geoDistanceFilter(BusinessIndexDefine.WifiDevice.Field.GEOPOINT)
                .distance(distance)//"0.5km")
                .point(geopoint[0], geopoint[1])))
                .withPageable(new PageRequest(page,pagesize))
                .withSort(SortBuilders.geoDistanceSort(BusinessIndexDefine.WifiDevice.Field.GEOPOINT)
                		.point(geopoint[0], geopoint[1]).unit(DistanceUnit.METERS).order(SortOrder.ASC))
                .build();
        //return wifiDeviceDocumentRepository.search(searchQuery);
		//when
		/*List<WifiDeviceDocument> geoAuthorsForGeoCriteria = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery, 
				WifiDeviceDocument.class);*/
        return wifiDeviceDocumentRepository.search(searchQuery);
	}
	
	/*@Override
	public ElasticsearchTemplate getElasticsearchTemplate(){
		return elasticsearchTemplate;
	}*/
	
	public WifiDeviceDocumentRepository getRepository(){
		return wifiDeviceDocumentRepository;
	}
}
