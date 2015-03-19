package com.bhu.vas.business.search.service.user;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.search.constants.IndexConstants;
import com.bhu.vas.business.search.indexable.UserLocationIndexableComponent;
import com.bhu.vas.business.search.mapable.UserLocationMapableComponent;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.es.ESConstants;
import com.smartwork.msip.es.exception.ESException;
import com.smartwork.msip.es.service.IndexService;

/**
 * 索引用户地理位置的业务service类 
 * @author lawliet
 *
 */
@Service
public class UserLocationIndexService extends IndexService{
	
	private final Logger logger = LoggerFactory.getLogger(UserLocationIndexService.class);
	
	/**
	 * 创建用户地理位置索引库, 如果存在, 则不创建
	 * @throws ESException 
	 * @throws IOException 
	 */
	public void createUserResponse() throws IOException, ESException{
		boolean created = super.createResponse(IndexConstants.UserLocationIndex, IndexConstants.UserShards, IndexConstants.UserReplicas);
		if(created){
			createUserMapping();
		}
	}
	
	public void deleteUserResponse(){
		super.deleteResponse(IndexConstants.UserLocationIndex);
	}
	
	
	public void createUserMapping() throws IOException, ESException{
		esclient.getChannelManager().getMappingChannel().putMapping(IndexConstants.UserLocationIndex, new UserLocationMapableComponent());
	}
	
//	/**
//	 * 创建用户地理位置数据索引
//	 * @param indexableEntity
//	 * @return
//	 * @throws ESException 
//	 * @throws IOException 
//	 */
//	public void createIndex4User(User entity) throws IOException, ESException{
//		UserIndexableComponent component = buildIndexableComponent(entity);
//		super.createIndexComponent(IndexConstants.UserIndex, IndexConstants.Types.UserType, component);
//	}
	
	public boolean deleteIndexByUid(int uid){
		return super.deleteIndex(IndexConstants.UserLocationIndex, IndexConstants.Types.UserLocationType, String.valueOf(uid));
	}
	
	public boolean createIndexsByComponents(List<UserLocationIndexableComponent> indexableComponents) throws ElasticsearchException, IOException, ESException{
//		   BulkResponse createDataResponse = esclient.getChannelManager().getIndexChannel().createIndexs(IndexConstants.UserIndex
//				   , IndexConstants.Types.UserType, indexableEntitys);
		BulkResponse createDataResponse = super.createIndexComponents(IndexConstants.UserLocationIndex, 
				IndexConstants.Types.UserLocationType, indexableComponents);
		if(createDataResponse.hasFailures()){
			   logger.info("createIndex4Users data error : " + createDataResponse.buildFailureMessage());
			   return false;
		}
		return true;
	}
	
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
	
	public void createIndex4UserLocation(Integer id, String area, double[]... lats_lons) throws IOException, ESException{
		UserLocationIndexableComponent component = buildIndexableComponent(id, area, lats_lons);
		super.createIndexComponent(IndexConstants.UserLocationIndex, IndexConstants.Types.UserLocationType, component);
	}
	
	public UserLocationIndexableComponent buildIndexableComponent(Integer id, String area, double[]... lats_lons){
		UserLocationIndexableComponent component = new UserLocationIndexableComponent();
		component.setId(String.valueOf(id));
		component.addLocations(lats_lons);
		component.setArea(area);
		component.setI_update_at(DateTimeHelper.getDateTime());
		return component;
	}

	/**
	 * 禁止用户索引库执行刷新数据操作
	 * @param indexname 
	 * @return 返回当前刷新频率
	 */
	public String disableIndexRefresh(){
		return esclient.getChannelManager().getSettingsChannel().disableRefreshInterval(IndexConstants.UserLocationIndex);
	}
	/**
	 * 开启用户索引库刷新数据操作
	 * @param indexname
	 * @param time 刷新频率
	 */
	public void openIndexRefresh(String time){
		if(StringHelper.isEmpty(time)){
			time = ESConstants.SettingsValue.IndexRefreshIntervalDefault;
		}
		esclient.getChannelManager().getSettingsChannel().openRefreshInterval(IndexConstants.UserLocationIndex, time);
	}
}
