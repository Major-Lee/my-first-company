package com.bhu.vas.business.search.test.location;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.FilterBuilders.geoBoundingBoxFilter;
import static org.elasticsearch.index.query.FilterBuilders.geoDistanceFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;

import javax.annotation.Resource;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import com.smartwork.msip.es.ESClient;
import com.smartwork.msip.localunit.BaseTest;
/**
 * 基于geo_point mapping 索引的搜索
 * @author lawliet
 *
 */
public class GeoPointTests extends BaseTest{
	@Resource
	protected ESClient esclient;
	
	//@Test
	public void doInit() throws IOException{
		indexed();
	}
	
	//@Test
	public void doGeoBoundingBox() throws IOException{
		doGeoBoundingBoxSearch();
	}
	//@Test
	public void doGeoPolygon(){
		doGeoPolygonSearch();
	}
	@Test
	public void doGeoDistance(){
		doGeoDistanceSearch();
	}
	
	public void indexed() throws IOException{
		//mapping
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject().startObject("type1")
                .startObject("properties").startObject("location").field("type", "geo_point").field("lat_lon", true).endObject().endObject()
                .endObject().endObject();
        esclient.getChannelManager().getMappingChannel().putMapping("geopoint_index", "type1", xContentBuilder);
        
        //indexed data
		esclient.getTransportClient().prepareIndex("geopoint_index", "type1", "1").setSource(jsonBuilder().startObject()
                .field("name", "New York")
                .startObject("location").field("lat", 40.7143528).field("lon", -74.0059731).endObject()
                .endObject()).execute().actionGet();
		
        // to NY: 5.286 km
		esclient.getTransportClient().prepareIndex("geopoint_index", "type1", "2").setSource(jsonBuilder().startObject()
                .field("name", "Times Square")
                .startObject("location").field("lat", 40.759011).field("lon", -73.9844722).endObject()
                .endObject()).execute().actionGet();

        // to NY: 0.4621 km
		esclient.getTransportClient().prepareIndex("geopoint_index", "type1", "3").setSource(jsonBuilder().startObject()
                .field("name", "Tribeca")
                .startObject("location").field("lat", 40.718266).field("lon", -74.007819).endObject()
                .endObject()).execute().actionGet();

        // to NY: 1.055 km
		esclient.getTransportClient().prepareIndex("geopoint_index", "type1", "4").setSource(jsonBuilder().startObject()
                .field("name", "Wall Street")
                .startObject("location").field("lat", 40.7051157).field("lon", -74.0088305).endObject()
                .endObject()).execute().actionGet();

        // to NY: 1.258 km
		esclient.getTransportClient().prepareIndex("geopoint_index", "type1", "5").setSource(jsonBuilder().startObject()
                .field("name", "Soho")
                .startObject("location").field("lat", 40.7247222).field("lon", -74).endObject()
                .endObject()).execute().actionGet();

        // to NY: 2.029 km
		esclient.getTransportClient().prepareIndex("geopoint_index", "type1", "6").setSource(jsonBuilder().startObject()
                .field("name", "Greenwich Village")
                .startObject("location").field("lat", 40.731033).field("lon", -73.9962255).endObject()
                .endObject()).execute().actionGet();

        // to NY: 8.572 km
		esclient.getTransportClient().prepareIndex("geopoint_index", "type1", "7").setSource(jsonBuilder().startObject()
                .field("name", "Brooklyn")
                .startObject("location").field("lat", 40.65).field("lon", -73.95).endObject()
                .endObject()).execute().actionGet();
	}
	
	/**
	 * 以边界框方式搜索, 矩形. 对角的坐标确定范围
	 */
	public void doGeoBoundingBoxSearch(){
        SearchResponse searchResponse = esclient.getTransportClient().prepareSearch("geopoint_index") // from NY
        		.setTypes("type1")
                .setQuery(filteredQuery(matchAllQuery(), geoBoundingBoxFilter("location").topLeft(40.7185668710285, -74.00899917196655).
                		bottomRight(40.71322242207956, -74.00473928385316)))
                .execute().actionGet();

        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("分数:"   
                    + hit.getScore()  
                    + ",ID:"  
                    + hit.id() 
                    + ", 名称:"  
                    + hit.getSource().get("name"));
        }
	}

	/**
	 * 以多边形方式搜索. 给定多边形所有点的坐标确定范围
	 */
	public void doGeoPolygonSearch(){
        SearchResponse searchResponse = esclient.getTransportClient().prepareSearch("geopoint_index") // from NY
        		.setTypes("type1")
                .setQuery(QueryBuilders.filteredQuery(
                        QueryBuilders.matchAllQuery(),
                        FilterBuilders.geoPolygonFilter("location")
                                .addPoint(40.71615173261129, -74.00850564550781)
                                .addPoint(40.71407805909013, -74.00694996427917)
                                .addPoint(40.71374288623752, -74.00499677591858)))
                .execute().actionGet();

        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("分数:"   
                    + hit.getScore()  
                    + ",ID:"  
                    + hit.id() 
                    + ", 名称:"  
                    + hit.getSource().get("name"));
        }
	}
	
	/**
	 * 以指定的坐标计算半径单位内的范围数据
	 */
	public void doGeoDistanceSearch(){
        SearchResponse searchResponse = esclient.getTransportClient().prepareSearch("geopoint_index")
	            .setQuery(filteredQuery(matchAllQuery(), geoDistanceFilter("location")
	                    .distance("200m")
	                    .optimizeBbox("none")
	                    .geoDistance(GeoDistance.PLANE)
	                    .point(40.71374288623752, -74.00499677591858)))
                .addSort(SortBuilders.geoDistanceSort("location").point(40.71374288623752, -74.00499677591858).order(SortOrder.ASC))
                .execute().actionGet();

        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("分数:"   
                    + hit.getScore()  
                    + ",ID:"  
                    + hit.id() 
                    + ", 名称:"  
                    + hit.getSource().get("name"));
        }
	}
	
	
}
