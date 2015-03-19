package com.bhu.vas.business.search.test.user.mapping;

import com.smartwork.msip.es.analyze.AnalyzerSupport;
import com.smartwork.msip.es.mapping.component.MapableComponent;
import com.smartwork.msip.es.mapping.field.CompletionSuggestMapableField;
import com.smartwork.msip.es.mapping.field.StringMapableField;
import com.smartwork.msip.es.mapping.field.type.IMapableFieldType;
import com.smartwork.msip.es.test.index.IndexStructureConstants;

public class CompletionSuggestMappingComponent extends MapableComponent{
	
	public static final String Mapable_id = "id";
	public static final String Mapable_name = "name";
	public static final String Mapable_name_suggest = "name_suggest";
	
	private StringMapableField id;
	private StringMapableField name;
	private CompletionSuggestMapableField name_suggest;
	
	public CompletionSuggestMappingComponent(){
		id = new StringMapableField(Mapable_id, StringMapableField.TYPE_STORED_NOT_ANALYZED);
		name = new StringMapableField(Mapable_name, IMapableFieldType.Store_YES, IMapableFieldType.Index_Analyzed, AnalyzerSupport.IK_ANALYZER);
		name_suggest = new CompletionSuggestMapableField(Mapable_name_suggest, AnalyzerSupport.STANDARD_ANALYZER, 
				AnalyzerSupport.STANDARD_ANALYZER,IMapableFieldType.Payloads_NO);
	}
	
	public StringMapableField getId() {
		return id;
	}

	public StringMapableField getName() {
		return name;
	}
	
	public CompletionSuggestMapableField getName_suggest() {
		return name_suggest;
	}

	@Override
	public String indextype() {
		return IndexStructureConstants.PerformanceIndexTypes.SuggestType;
	}
	
}
