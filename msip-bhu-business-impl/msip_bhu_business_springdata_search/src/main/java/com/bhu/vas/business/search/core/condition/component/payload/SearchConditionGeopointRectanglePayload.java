package com.bhu.vas.business.search.core.condition.component.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 对于geopoint Rectangle方式条件匹配扩展condition payload类
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchConditionGeopointRectanglePayload implements java.io.Serializable{
	//业务上下文id
	private String contextId;
	//左上坐标纬度
	@JsonProperty("tl_lat")
	private double topLeft_lat;
	//左上坐标经度
	@JsonProperty("tl_lon")
	private double topLeft_lon;
	//右下坐标纬度
	@JsonProperty("br_lat")
	private double bottomRight_lat;
	//右下坐标经度
	@JsonProperty("br_lon")
	private double bottomRight_lon;

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public double getTopLeft_lat() {
		return topLeft_lat;
	}

	public void setTopLeft_lat(double topLeft_lat) {
		this.topLeft_lat = topLeft_lat;
	}

	public double getTopLeft_lon() {
		return topLeft_lon;
	}

	public void setTopLeft_lon(double topLeft_lon) {
		this.topLeft_lon = topLeft_lon;
	}

	public double getBottomRight_lat() {
		return bottomRight_lat;
	}

	public void setBottomRight_lat(double bottomRight_lat) {
		this.bottomRight_lat = bottomRight_lat;
	}

	public double getBottomRight_lon() {
		return bottomRight_lon;
	}

	public void setBottomRight_lon(double bottomRight_lon) {
		this.bottomRight_lon = bottomRight_lon;
	}

	public static SearchConditionGeopointRectanglePayload buildPayload(String contextId, double topLeft_lat, 
			double topLeft_lon, double bottomRight_lat, double bottomRight_lon){
		SearchConditionGeopointRectanglePayload geopointRectanglePayload = new SearchConditionGeopointRectanglePayload();
		geopointRectanglePayload.setContextId(contextId);
		geopointRectanglePayload.setTopLeft_lat(topLeft_lat);
		geopointRectanglePayload.setTopLeft_lon(topLeft_lon);
		geopointRectanglePayload.setBottomRight_lat(bottomRight_lat);
		geopointRectanglePayload.setBottomRight_lon(bottomRight_lon);
		return geopointRectanglePayload;
	}
	
}
