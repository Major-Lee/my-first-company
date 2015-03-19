package com.bhu.vas.business.search.service.user;
//package com.whisper.business.search.service.user;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.elasticsearch.ElasticsearchException;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import com.smartwork.msip.cores.helper.DateTimeHelper;
//import com.smartwork.msip.cores.helper.StringHelper;
//import com.smartwork.msip.cores.helper.phone.PhoneHelper;
//import com.smartwork.msip.es.ESConstants;
//import com.smartwork.msip.es.exception.ESException;
//import com.smartwork.msip.es.index.resolver.IndexableResolver;
//import com.smartwork.msip.es.service.IndexService;
//import com.whisper.api.user.model.User;
//import com.whisper.business.search.constants.IndexConstants;
//import com.whisper.business.search.indexable.UserIndexableComponent;
//import com.whisper.business.search.mapable.UserMapableComponent;
//import com.whisper.helper.UserSymbolHelper;
//
///**
// * 索引用户的业务service类 
// * @author lawliet
// *
// */
//@Service
//public class UserIndexService extends IndexService{
//	
//	private final Logger logger = LoggerFactory.getLogger(UserIndexService.class);
//	
//	/**
//	 * 创建用户索引库, 如果存在, 则不创建
//	 * @throws ESException 
//	 * @throws IOException 
//	 */
//	public void createUserResponse() throws IOException, ESException{
//		boolean created = super.createResponse(IndexConstants.UserIndex, IndexConstants.UserShards, IndexConstants.UserReplicas);
//		if(created){
//			createUserMapping();
//		}
//	}
//	
//	public void deleteUserResponse(){
//		super.deleteResponse(IndexConstants.UserIndex);
//	}
//	
//	
//	public void createUserMapping() throws IOException, ESException{
//		esclient.getChannelManager().getMappingChannel().putMapping(IndexConstants.UserIndex, new UserMapableComponent());
//	}
//	
//	/**
//	 * 创建用户数据索引
//	 * @param indexableEntity
//	 * @return
//	 * @throws ESException 
//	 * @throws IOException 
//	 */
//	public void createIndex4User(User entity) throws IOException, ESException{
//		UserIndexableComponent component = buildIndexableComponent(entity);
//		//super.createIndexComponent(IndexConstants.UserIndex, IndexConstants.Types.UserType, component);
//		this.createIndex(component);
//	}
//	
//	public void createIndex(UserIndexableComponent component) throws IOException, ESException{
//		super.createIndexComponent(IndexConstants.UserIndex, IndexConstants.Types.UserType, component);
//	}
//	
//	public void updateUserAvatar(int uid, String avatar) throws ElasticsearchException, Exception{
//		UserIndexableComponent component = this.buildAvatarIndexableComponent(uid, avatar);
//		super.updateIndexComponent(IndexConstants.UserIndex, IndexConstants.Types.UserType, component);
//	}
//	
//	public boolean deleteIndexByUid(int uid){
//		return super.deleteIndex(IndexConstants.UserIndex, IndexConstants.Types.UserType, String.valueOf(uid));
//	}
//	
//	public boolean createIndexsByComponents(List<UserIndexableComponent> indexableComponents) throws ElasticsearchException, IOException, ESException{
////		   BulkResponse createDataResponse = esclient.getChannelManager().getIndexChannel().createIndexs(IndexConstants.UserIndex
////				   , IndexConstants.Types.UserType, indexableEntitys);
//		BulkResponse createDataResponse = super.createIndexComponents(IndexConstants.UserIndex, IndexConstants.Types.UserType, indexableComponents);
//		if(createDataResponse.hasFailures()){
//			   logger.info("createIndex4Users data error : " + createDataResponse.buildFailureMessage());
//			   return false;
//		}
//		return true;
//	}
//	
//	public boolean createIndexsByEntitys(List<User> entitys) throws ElasticsearchException, IOException, ESException{
//		if(entitys == null || entitys.isEmpty()) return false;
//		
//		List<UserIndexableComponent> components = new ArrayList<UserIndexableComponent>();
//		for(User entity : entitys){
//			UserIndexableComponent component = buildIndexableComponent(entity);
//			if(component != null){
//				components.add(component);
//			}
//		}
//		return this.createIndexsByComponents(components);
//	}
//	
//	public UserIndexableComponent buildIndexableComponent(User entity){
//		UserIndexableComponent component = new UserIndexableComponent();
//		component.setId(String.valueOf(entity.getId()));
//		String symbol = UserSymbolHelper.generate(entity);
//		component.setSymbol(IndexableResolver.standardString(symbol));
//		component.setShownick(entity.getNick());
//		String mobilenoWithCountryCode = PhoneHelper.format(entity.getCountrycode(), entity.getMobileno());
//		component.setMobileno(mobilenoWithCountryCode);
//		component.setMno(generateMobileno(entity.getMobileno(), mobilenoWithCountryCode));
//		component.setAvatar(entity.getAvatar());
//		String standardNick = IndexableResolver.standardString(entity.getNick());
//		component.setNick(standardNick);
//		component.setPinyin(generatePinyin(standardNick));
//		component.setSort(0);
//		component.setCreate_at(entity.getCreated_at().getTime());
//		component.setI_update_at(DateTimeHelper.getDateTime());
//		return component;
//	}
//	
//	public UserIndexableComponent buildIndexableComponentWithSort(User entity, int sort){
//		UserIndexableComponent component = this.buildIndexableComponent(entity);
//		component.setSort(sort);
//		return component;
//	}
//
//	public UserIndexableComponent buildAvatarIndexableComponent(int uid, String avatar){
//		UserIndexableComponent component = new UserIndexableComponent();
//		component.setId(String.valueOf(uid));
//		component.setAvatar(avatar);
//		component.setI_update_at(DateTimeHelper.getDateTime());
//		return component;
//	}
//	
//	/**
//	 * 用户主要信息修改, 昵称，头像
//	 * 由于是使用script脚本的机制操作的es
//	 * 不安全，不建议使用
//	 * 如果修改索引，直接索引整条数据就好了
//	 * @param uid
//	 * @param nick
//	 * @param avatar
//	 */
////	@Deprecated
////	public void updateIndex4UserInfo(String uid, String nick, String avatar){
////		if(StringHelper.isEmpty(nick)) return;
////		
////		List<String> fieldnames = new ArrayList<String>();
////		List<Object> values = new ArrayList<Object>();
////		
////		//showname
////		fieldnames.add(UserMapableComponent.M_shownick);
////		values.add(nick);
////		//标准化字符串
////		String standardNick = IndexableResolver.standardString(nick);
////		//取后两位中文名字
////		fieldnames.add(UserMapableComponent.M_nick);
////		values.add(standardNick);
////		//拼音
////		fieldnames.add(UserMapableComponent.M_pinyin);
////		values.add(generatePinyin(standardNick));
////		//头像
////		fieldnames.add(UserMapableComponent.M_avatar);
////		values.add(avatar);
////		
////		super.updateIndexById(IndexConstants.UserIndex, IndexConstants.Types.UserType, uid, fieldnames, values);
////	}
//	
////	/**
////	 * 用户头像更新
////	 * 更新索引
////	 * @param uid
////	 * @param avatar
////	 */
////	public void updateIndex4UserAvatar(String uid, String avatar){
////		if(StringHelper.isEmpty(avatar)) return;
////		
////		super.updateIndexById(IndexConstants.UserIndex, IndexConstants.Types.UserType, uid, UserMapableComponent.M_avatar, avatar);
////	}
//	
//	/**
//	 * 中文名称倒转, 非中文返回空字符串
//	 * @param standardString
//	 * @return
//	 */
//	protected String generateRevName(String standardString){
//		return IndexableResolver.reverseString(standardString);
//	}
//	
//	/**
//	 * 中文名称取后两位, 非中文返回空字符串
//	 * @param standardString
//	 * @return
//	 */
//	protected String generateSubName(String standardString){
//		return IndexableResolver.lastName(standardString);
//	}
//	/**
//	 * 全拼和首字母
//	 * 以空格分隔
//	 * @return
//	 */
//	protected String generatePinyin(String standardString){
//		//全拼
//		String fullspell = IndexableResolver.toHanyuPinyinString(standardString);
//		StringBuffer pinyinsb = new StringBuffer();
//		if(StringHelper.isNotEmpty(fullspell)){
//			pinyinsb.append(fullspell);
//			pinyinsb.append(StringHelper.WHITESPACE_STRING_GAP);
//			//首字母拼音
//			pinyinsb.append(IndexableResolver.getPinYinFristChar(standardString));
//		}
//		return pinyinsb.toString();
//	}
//	/**
//	 * 生成手机号匹配索引数据
//	 * @param mobilenos
//	 * @return
//	 */
//	protected String generateMobileno(String... mobilenos){
//		if(mobilenos == null || mobilenos.length == 0) return null;
//		StringBuffer mobilenosb = new StringBuffer();
//		for(String mobileno : mobilenos){
//			if(mobilenosb.length() > 0)
//				mobilenosb.append(StringHelper.WHITESPACE_STRING_GAP);
//			mobilenosb.append(IndexableResolver.standardString(mobileno));
//		}
//		return mobilenosb.toString();
//	}
//	
//	/**
//	 * 禁止用户索引库执行刷新数据操作
//	 * @param indexname 
//	 * @return 返回当前刷新频率
//	 */
//	public String disableIndexRefresh(){
//		return esclient.getChannelManager().getSettingsChannel().disableRefreshInterval(IndexConstants.UserIndex);
//	}
//	/**
//	 * 开启用户索引库刷新数据操作
//	 * @param indexname
//	 * @param time 刷新频率
//	 */
//	public void openIndexRefresh(String time){
//		if(StringHelper.isEmpty(time)){
//			time = ESConstants.SettingsValue.IndexRefreshIntervalDefault;
//		}
//		esclient.getChannelManager().getSettingsChannel().openRefreshInterval(IndexConstants.UserIndex, time);
//	}
//}
