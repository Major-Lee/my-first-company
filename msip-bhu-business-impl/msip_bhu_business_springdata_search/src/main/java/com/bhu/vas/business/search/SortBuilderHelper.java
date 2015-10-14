package com.bhu.vas.business.search;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class SortBuilderHelper {
	public static FieldSortBuilder builderSort(String field,SortOrder sortOrder){
		return SortBuilders.fieldSort(field).order(sortOrder);
	}
}
