package com.bhu.vas.business.search.core.condition.component.payload;

/**
 * 对于geopoint方式条件匹配和排序的扩展condition payload类
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchConditionGeopointPayload implements java.io.Serializable{
	//业务上下文id
	private String contextId;
	//纬度
	private double lat;
	//经度
	private double lon;
	
	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public static SearchConditionGeopointPayload buildPayload(String contextId, double lat, double lon){
		SearchConditionGeopointPayload geopointPayload = new SearchConditionGeopointPayload();
		geopointPayload.setContextId(contextId);
		geopointPayload.setLat(lat);
		geopointPayload.setLon(lon);
		return geopointPayload;
	}
	
}
