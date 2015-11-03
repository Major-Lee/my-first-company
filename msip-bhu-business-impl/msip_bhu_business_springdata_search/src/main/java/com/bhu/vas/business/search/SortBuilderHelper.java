package com.bhu.vas.business.search;

import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class SortBuilderHelper {
	public static FieldSortBuilder builderSort(String field,SortOrder sortOrder){
		return SortBuilders.fieldSort(field).order(sortOrder);
	}
	
	public static GeoDistanceSortBuilder builderDistanceSort(String field, double lat, double lon, SortOrder sortOrder){
		return SortBuilders.geoDistanceSort(field).point(lat, lon).unit(DistanceUnit.METERS).order(sortOrder);
	}
}
