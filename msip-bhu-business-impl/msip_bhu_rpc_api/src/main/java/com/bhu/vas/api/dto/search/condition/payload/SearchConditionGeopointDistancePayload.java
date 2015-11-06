package com.bhu.vas.api.dto.search.condition.payload;

/**
 * 对于geopoint distance方式条件匹配扩展condition payload类
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchConditionGeopointDistancePayload extends SearchConditionGeopointPayload{
	//与原点坐标的距离 eq:10km 5m 3cm 
	private String distance;

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public static SearchConditionGeopointDistancePayload buildPayload(String contextId, double lat, double lon, String distance){
		SearchConditionGeopointDistancePayload geopointDistancePayload = new SearchConditionGeopointDistancePayload();
		geopointDistancePayload.setContextId(contextId);
		geopointDistancePayload.setLat(lat);
		geopointDistancePayload.setLon(lon);
		geopointDistancePayload.setDistance(distance);
		return geopointDistancePayload;
	}
	
}
