package com.bhu.vas.business.search.test.location;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;

import javax.annotation.Resource;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;
import static org.elasticsearch.index.query.FilterBuilders.geoIntersectionFilter;
import static org.elasticsearch.index.query.QueryBuilders.*;
import com.smartwork.msip.es.ESClient;
import com.smartwork.msip.localunit.BaseTest;
/**
 * 基于geo_shape mapping 索引的搜索
 * @author lawliet
 *
 */
public class GeoShapeTests extends BaseTest{
	@Resource
	protected ESClient esclient;
	
	//@Test
	public void doInit() throws IOException{
		indexed();
	}
	
	@Test
	public void doShapeBuilderNewPoint() throws IOException{
		doShapeBuilderNewPointSearch();
	}

	
	public void indexed() throws IOException{
		//mapping
        String mapping = XContentFactory.jsonBuilder().startObject().startObject("type1")
                .startObject("properties").startObject("location")
                .field("type", "geo_shape")
                .field("tree", "quadtree")
                //.field("precision","5m")
                .endObject().endObject()
                .endObject().endObject().string();
        esclient.getChannelManager().getMappingChannel().putMapping("geoshape_index", "type1", mapping);
        
        //indexed data

//        esclient.getTransportClient().prepareIndex("geoshape_index", "type1", "2").setSource(jsonBuilder().startObject()
//                .field("name", "area A")
//                .startObject("location")
//                .field("type", "polygon")
//                .startArray("coordinates").startArray()
//                .startArray().value(-74.0087625973755).value(40.71593854960517).endArray()
//                .startArray().value(-74.00589799814759).value(40.71460489603703).endArray()
//                .startArray().value(-74.00720691614686).value(40.71300285002779).endArray()
//                .startArray().value(-74.01013588839112).value(40.714263346303724).endArray() // close the polygon
//                .endArray().endArray()
//                .endObject()
//                .endObject()).execute().actionGet();
//        
//        
//        esclient.getTransportClient().prepareIndex("geoshape_index", "type1", "1").setSource(jsonBuilder().startObject()
//                .field("name", "area A")
//                .startObject("location")
//                .field("type", "polygon")
//                .startArray("coordinates").startArray()
//                .startArray().value(40.71593854960517).value(-74.0087625973755).endArray()
//                .startArray().value(40.71460489603703).value(-74.00589799814759).endArray()
//                .startArray().value(40.71300285002779).value(-74.00720691614686).endArray()
//                .startArray().value(40.714263346303724).value(-74.01013588839112).endArray() // close the polygon
//                .endArray().endArray()
//                .endObject()
//                .endObject()).execute().actionGet();
        
        PolygonBuilder builder = ShapeBuilder.newPolygon()
                .point(-74.0087625973755, 40.71593854960517).point(-74.00589799814759, 40.71460489603703)
                .point(-74.00720691614686, 40.71300285002779).point(-74.01100492411194, 40.71322242203457)
                .point(-74.01099419527588, 40.71494644396391)
                .close();

        BytesReference data = jsonBuilder().startObject().field("location", builder).endObject().bytes();
        esclient.getTransportClient().prepareIndex("geoshape_index", "type1", "3").setSource(data).execute().actionGet();
//        ShapeBuilder shape = ShapeBuilder.newPolygon()
//        		.point(170, -10).point(190, -10)
//        		.point(190, 10).point(170, 10).hole().point(175, -5).point(185, -5)
//        		.point(185, 5).point(175, 5).close().close();
//        XContentBuilder shapeContent = jsonBuilder().startObject()
//                .field("shape", shape);
//        shapeContent.endObject();
//        client().prepareIndex("shapes", "shape_type", "Big_Rectangle").setSource(shapeContent).execute().actionGet();
		
	}
	
	/**
	 * 以边界框方式搜索, 矩形. 对角的坐标确定范围
	 */
	public void doShapeBuilderNewPointSearch(){
        ShapeBuilder query = ShapeBuilder.newPoint(-74.00970673494874, 40.71391366261324);
        // This search would fail if both geoshape indexing and geoshape filtering
        // used the bottom-level optimization in SpatialPrefixTree#recursiveGetNodes.
        SearchRequestBuilder builder = esclient.getTransportClient().prepareSearch("geoshape_index")
        		.setTypes("type1")
                .setQuery(filteredQuery(matchAllQuery(),
                        geoIntersectionFilter("location", query)));
        System.out.println(builder.toString());
        SearchResponse searchResponse = builder.execute().actionGet();

        for (SearchHit hit : searchResponse.getHits()) {
        	System.out.println(hit.getSource());
            System.out.println("分数:"   
                    + hit.getScore()  
                    + ",ID:"  
                    + hit.id() 
                    + ", 名称:"  
                    + hit.getSource().get("name"));
        }
	}
	
	
}
