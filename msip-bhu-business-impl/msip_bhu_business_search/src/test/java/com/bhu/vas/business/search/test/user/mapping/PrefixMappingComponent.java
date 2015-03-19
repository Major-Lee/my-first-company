package com.bhu.vas.business.search.test.user.mapping;

import com.smartwork.msip.es.analyze.AnalyzerSupport;
import com.smartwork.msip.es.mapping.component.MapableComponent;
import com.smartwork.msip.es.mapping.field.StringMapableField;
import com.smartwork.msip.es.mapping.field.type.IMapableFieldType;
import com.smartwork.msip.es.test.index.IndexStructureConstants;
/**
 * 用于测试prefix搜索的结构模型
 * @author lawliet
 *
 */
public class PrefixMappingComponent extends MapableComponent{
	
	public static final String Mapable_id = "id";
	public static final String Mapable_showname= "showname";
	public static final String Mapable_name = "name";
	public static final String Mapable_pinyin = "pinyin";
	
	private StringMapableField id;
	private StringMapableField showname;
	private StringMapableField name;
	private StringMapableField pinyin;
	
	public PrefixMappingComponent(){
		id = new StringMapableField(Mapable_id, StringMapableField.TYPE_STORED_NOT_ANALYZED);
		showname = new StringMapableField(Mapable_showname, StringMapableField.TYPE_STORED_NOT_INDEX);
		name = new StringMapableField(Mapable_name, IMapableFieldType.Store_YES, IMapableFieldType.Index_Analyzed, AnalyzerSupport.EDGENGRAM_WHITESPACE_ANALYZER);
		pinyin = new StringMapableField(Mapable_pinyin, IMapableFieldType.Store_YES, IMapableFieldType.Index_Analyzed, AnalyzerSupport.EDGENGRAM_WHITESPACE_ANALYZER);	}
	
	public StringMapableField getId() {
		return id;
	}

	public StringMapableField getName() {
		return name;
	}

	public StringMapableField getShowname() {
		return showname;
	}

	public StringMapableField getPinyin() {
		return pinyin;
	}

	@Override
	public String indextype() {
		return IndexStructureConstants.PerformanceIndexTypes.PrefixType;
	}
	
}
