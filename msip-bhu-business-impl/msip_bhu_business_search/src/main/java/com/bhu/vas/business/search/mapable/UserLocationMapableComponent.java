//package com.bhu.vas.business.search.mapable;
//
//import com.bhu.vas.business.search.constants.IndexConstants;
//import com.smartwork.msip.es.mapping.component.MapableComponent;
//import com.smartwork.msip.es.mapping.field.GeoPointsMapableField;
//import com.smartwork.msip.es.mapping.field.LongMapableField;
//import com.smartwork.msip.es.mapping.field.StringMapableField;
///**
// * 用户索引映射类(用于映射索引的字段结构和类型)
// * @author lawliet
// *
// */
//public class UserLocationMapableComponent extends MapableComponent{
//	
//	public static final String M_id = "id";//用户id
//	public static final String M_location = "location";//地理位置坐标字段
//	public static final String M_area = "area";//地理位置所属区域
//	public static final String M_create_at = "create_at";//注册时间
//	public static final String M_i_update_at = "i_update_at";//索引记录的更新时间
//	private StringMapableField id;
//	private GeoPointsMapableField location;
//	private StringMapableField area;
//	private LongMapableField create_at;
//	private StringMapableField i_update_at;
//
//	public UserLocationMapableComponent(){
//		id = new StringMapableField(M_id, StringMapableField.TYPE_STORED_NOT_ANALYZED);
//		location = new GeoPointsMapableField(M_location);
//		area = new StringMapableField(M_area, StringMapableField.TYPE_STORED_NOT_ANALYZED);
//		create_at = new LongMapableField(M_create_at, LongMapableField.TYPE_STORED_NOT_ANALYZED);
//		i_update_at = new StringMapableField(M_i_update_at, StringMapableField.TYPE_STORED_NOT_ANALYZED);
//	}
//
//	public StringMapableField getId() {
//		return id;
//	}
//
//	public GeoPointsMapableField getLocation() {
//		return location;
//	}
//	
//	public StringMapableField getArea() {
//		return area;
//	}
//
//	public LongMapableField getCreate_at() {
//		return create_at;
//	}
//
//	public StringMapableField getI_update_at() {
//		return i_update_at;
//	}
//
//	@Override
//	public String indextype() {
//		return IndexConstants.Types.UserLocationType;
//	}
//	
//}
