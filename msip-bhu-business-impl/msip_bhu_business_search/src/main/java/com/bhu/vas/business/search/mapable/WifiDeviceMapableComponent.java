package com.bhu.vas.business.search.mapable;

import com.bhu.vas.business.search.constants.BusinessIndexConstants;
import com.smartwork.msip.es.analyze.AnalyzerSupport;
import com.smartwork.msip.es.mapping.component.MapableComponent;
import com.smartwork.msip.es.mapping.field.GeoPointsMapableField;
import com.smartwork.msip.es.mapping.field.IntegerMapableField;
import com.smartwork.msip.es.mapping.field.LongMapableField;
import com.smartwork.msip.es.mapping.field.StringMapableField;
import com.smartwork.msip.es.mapping.field.type.IMapableFieldType;
/**
 * wifi设备索引映射类(用于映射索引的字段结构和类型)
 * @author lawliet
 *
 */
public class WifiDeviceMapableComponent extends MapableComponent{
	
	public static final String M_id = "id";//wifi id
	public static final String M_ghash = "ghash";//地理位置的geohash
	public static final String M_address = "address";//地理位置的详细地址
	public static final String M_show_address = "showaddress";//地理位置的详细地址
	public static final String M_online = "online";//wifi设备是否在线
	public static final String M_count = "count";//wifi设备上的移动设备在线数量
	public static final String M_register_at = "register_at";//wifi设备的注册时间
	public static final String M_i_update_at = "i_update_at";//索引记录的更新时间
	
	private StringMapableField id;
	private GeoPointsMapableField ghash;
	private StringMapableField address;
	private StringMapableField showaddress;
	private IntegerMapableField online;
	private IntegerMapableField count;
	private LongMapableField register_at;
	private StringMapableField i_update_at;

	public WifiDeviceMapableComponent(){
		id = new StringMapableField(M_id, StringMapableField.TYPE_STORED_NOT_ANALYZED);
		ghash = new GeoPointsMapableField(M_ghash);
		address = new StringMapableField(M_address, IMapableFieldType.Store_YES, 
				IMapableFieldType.Index_Analyzed, AnalyzerSupport.EDGENGRAM_WHITESPACE_ANALYZER);
		showaddress = new StringMapableField(M_show_address, StringMapableField.TYPE_STORED_NOT_INDEX);
		online = new IntegerMapableField(M_online, IntegerMapableField.TYPE_STORED_NOT_ANALYZED);
		count = new IntegerMapableField(M_count, IntegerMapableField.TYPE_STORED_NOT_ANALYZED);
		register_at = new LongMapableField(M_register_at, LongMapableField.TYPE_STORED_NOT_ANALYZED);
		i_update_at = new StringMapableField(M_i_update_at, StringMapableField.TYPE_STORED_NOT_ANALYZED);
	}

	public StringMapableField getId() {
		return id;
	}

	public GeoPointsMapableField getGhash() {
		return ghash;
	}

	public StringMapableField getAddress() {
		return address;
	}

	public StringMapableField getShowaddress() {
		return showaddress;
	}

	public IntegerMapableField getOnline() {
		return online;
	}

	public IntegerMapableField getCount() {
		return count;
	}

	public LongMapableField getRegister_at() {
		return register_at;
	}

	public StringMapableField getI_update_at() {
		return i_update_at;
	}

	@Override
	public String indextype() {
		return BusinessIndexConstants.Types.WifiDeviceType;
	}
	
}
