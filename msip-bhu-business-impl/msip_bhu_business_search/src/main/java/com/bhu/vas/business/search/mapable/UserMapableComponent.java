package com.bhu.vas.business.search.mapable;

import com.bhu.vas.business.search.constants.IndexConstants;
import com.smartwork.msip.es.analyze.AnalyzerSupport;
import com.smartwork.msip.es.mapping.component.MapableComponent;
import com.smartwork.msip.es.mapping.field.LongMapableField;
import com.smartwork.msip.es.mapping.field.StringMapableField;
import com.smartwork.msip.es.mapping.field.type.IMapableFieldType;
/**
 * 用户索引映射类(用于映射索引的字段结构和类型)
 * @author lawliet
 *
 */
public class UserMapableComponent extends MapableComponent{
	
	public static final String M_id = "id";//用户id
	public static final String M_symbol = "symbol";//用户唯一标示 (昵称#尾号后4位)
	public static final String M_shownick= "shownick";//用户显示名字
	public static final String M_nick = "nick";//匹配名字
	public static final String M_showmobileno = "mobileno";//显示的手机号
	public static final String M_mno = "mno";//匹配的手机号
	public static final String M_avatar = "avatar";//头像
	public static final String M_pinyin = "pinyin";//名字的拼音全拼, 首字母
	//public static final String M_location = "location";//地理位置字段
	public static final String M_sort = "sort";//业务评分字段
	public static final String M_create_at = "create_at";//注册时间
	public static final String M_i_update_at = "i_update_at";//索引记录的更新时间
	private StringMapableField id;
	private StringMapableField symbol;
	private StringMapableField shownick;
	private StringMapableField nick;
	private StringMapableField avatar;
	private StringMapableField mobileno;
	private StringMapableField mno;
	private StringMapableField pinyin;
	//private GeoPointsMapableField location;
	private LongMapableField sort;
	private LongMapableField create_at;
	private StringMapableField i_update_at;

	public UserMapableComponent(){
		id = new StringMapableField(M_id, StringMapableField.TYPE_STORED_NOT_ANALYZED);
		symbol = new StringMapableField(M_symbol, StringMapableField.TYPE_STORED_NOT_ANALYZED);
		shownick = new StringMapableField(M_shownick, StringMapableField.TYPE_STORED_NOT_INDEX);
		nick = new StringMapableField(M_nick, IMapableFieldType.Store_YES, IMapableFieldType.Index_Analyzed, AnalyzerSupport.LOWERCASE_WHITESPACE_ANALYZER);
		avatar = new StringMapableField(M_avatar, StringMapableField.TYPE_STORED_NOT_INDEX);
		mobileno = new StringMapableField(M_showmobileno, StringMapableField.TYPE_STORED_NOT_ANALYZED);
		mno = new StringMapableField(M_mno, IMapableFieldType.Store_YES, IMapableFieldType.Index_Analyzed, AnalyzerSupport.LOWERCASE_WHITESPACE_ANALYZER);
		pinyin = new StringMapableField(M_pinyin, IMapableFieldType.Store_YES, IMapableFieldType.Index_Analyzed, AnalyzerSupport.LOWERCASE_WHITESPACE_ANALYZER);
		//location = new GeoPointsMapableField(M_location);
		sort = new LongMapableField(M_sort, LongMapableField.TYPE_STORED_NOT_ANALYZED);
		create_at = new LongMapableField(M_create_at, LongMapableField.TYPE_STORED_NOT_ANALYZED);
		i_update_at = new StringMapableField(M_i_update_at, StringMapableField.TYPE_STORED_NOT_ANALYZED);
	}

	public StringMapableField getId() {
		return id;
	}
	
	public StringMapableField getSymbol() {
		return symbol;
	}

	public StringMapableField getShownick() {
		return shownick;
	}

	public StringMapableField getNick() {
		return nick;
	}

	public StringMapableField getMobileno() {
		return mobileno;
	}
	
	public StringMapableField getMno() {
		return mno;
	}
	
	/*public GeoPointsMapableField getLocation() {
		return location;
	}*/

	public StringMapableField getPinyin() {
		return pinyin;
	}

	public LongMapableField getSort() {
		return sort;
	}
	
	public LongMapableField getCreate_at() {
		return create_at;
	}
	
	public StringMapableField getAvatar() {
		return avatar;
	}

	public StringMapableField getI_update_at() {
		return i_update_at;
	}

	@Override
	public String indextype() {
		return IndexConstants.Types.UserType;
	}
	
}
