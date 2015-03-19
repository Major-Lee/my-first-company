package com.bhu.vas.business.search.test.user.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.search.constants.IndexConstants;
import com.bhu.vas.business.search.test.user.index.CompletionSuggestIndexableComponent;
import com.bhu.vas.business.search.test.user.index.PrefixIndexableComponent;
import com.bhu.vas.business.search.test.user.mapping.CompletionSuggestMappingComponent;
import com.bhu.vas.business.search.test.user.mapping.PrefixMappingComponent;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.es.ESConstants;
import com.smartwork.msip.es.exception.ESException;
import com.smartwork.msip.es.index.field.CompletionSuggestIndexableField;
import com.smartwork.msip.es.index.resolver.IndexableResolver;
import com.smartwork.msip.es.service.IndexService;
import com.smartwork.msip.es.test.index.IndexStructureConstants;

/**
 * 测试搜索性能的业务service类 
 * @author lawliet
 *
 */
@Service
public class PerformanceIndexService extends IndexService{
	
	private final Logger logger = LoggerFactory.getLogger(PerformanceIndexService.class);
	
	/**
	 * 创建用户索引库, 如果存在, 则不创建
	 * @throws ESException 
	 * @throws IOException 
	 */
	public void createPerformanceResponse() throws IOException, ESException{
		boolean created = super.createResponse(IndexStructureConstants.PerformanceIndex, IndexConstants.UserShards, IndexConstants.UserReplicas);
		if(created){
			createPerformanceMapping();
		}
	}
	
	public void deleteUserResponse(){
		super.deleteResponse(IndexStructureConstants.PerformanceIndex);
	}
	
	
	public void createPerformanceMapping() throws IOException, ESException{
		createPrefixPerformanceMapping();
	}
	
	public void createPrefixPerformanceMapping() throws IOException, ESException{
		esclient.getChannelManager().getMappingChannel().putMapping(IndexStructureConstants.PerformanceIndex, new PrefixMappingComponent());
	}
	
	public void createSuggestPerformanceMapping() throws IOException, ESException{
		esclient.getChannelManager().getMappingChannel().putMapping(IndexStructureConstants.PerformanceIndex, new CompletionSuggestMappingComponent());
	}
	
	public boolean createPrefixIndexsByComponents(List<PrefixIndexableComponent> indexableComponents) throws ElasticsearchException, IOException, ESException{
//		   BulkResponse createDataResponse = esclient.getChannelManager().getIndexChannel().createIndexs(IndexConstants.UserIndex
//				   , IndexConstants.Types.UserType, indexableEntitys);
		BulkResponse createDataResponse = super.createIndexComponents(IndexStructureConstants.PerformanceIndex, 
				IndexStructureConstants.PerformanceIndexTypes.PrefixType, indexableComponents);
		if(createDataResponse.hasFailures()){
			   logger.info("createPrefixIndexsByComponents data error : " + createDataResponse.buildFailureMessage());
			   return false;
		}
		return true;
	}
	
	public boolean createSuggestIndexsByComponents(List<CompletionSuggestIndexableComponent> indexableComponents) throws ElasticsearchException, IOException, ESException{
//		   BulkResponse createDataResponse = esclient.getChannelManager().getIndexChannel().createIndexs(IndexConstants.UserIndex
//				   , IndexConstants.Types.UserType, indexableEntitys);
		BulkResponse createDataResponse = super.createIndexComponents(IndexStructureConstants.PerformanceIndex, 
				IndexStructureConstants.PerformanceIndexTypes.SuggestType, indexableComponents);
		if(createDataResponse.hasFailures()){
			   logger.info("createSuggestIndexsByComponents data error : " + createDataResponse.buildFailureMessage());
			   return false;
		}
		return true;
	}
	
	public PrefixIndexableComponent buildPrefixIndexableComponent(String id, String name){
		PrefixIndexableComponent component = new PrefixIndexableComponent();
		component.setId(id);
		component.setShowname(name);
		String standardname = IndexableResolver.standardString(name);
		component.setName(standardname);
		component.setPinyin(generatePinyin(standardname));
		return component;
	}
	
	public CompletionSuggestIndexableComponent buildSuggestIndexableComponent(String id, String name){
		CompletionSuggestIndexableComponent component = new CompletionSuggestIndexableComponent();
		component.setId(id);
		component.setName(name);
		List<String> input = new ArrayList<String>();
		input.add(name);
		CompletionSuggestIndexableField name_suggest_field = new CompletionSuggestIndexableField(input,name.concat("op"));
		if(name.endsWith("英")){
			name_suggest_field.setWeight(5);
		}
		component.setName_suggest(name_suggest_field);
		return component;
	}
	
	/**
	 * 全拼和首字母
	 * 以空格分隔
	 * @return
	 */
	protected String generatePinyin(String standardString){
		//全拼
		String fullspell = IndexableResolver.toHanyuPinyinString(standardString);
		StringBuffer pinyinsb = new StringBuffer();
		if(StringHelper.isNotEmpty(fullspell)){
			pinyinsb.append(fullspell);
			pinyinsb.append(StringHelper.WHITESPACE_STRING_GAP);
			//首字母拼音
			pinyinsb.append(IndexableResolver.getPinYinFristChar(standardString));
		}
		return pinyinsb.toString();
	}

	/**
	 * 禁止用户索引库执行刷新数据操作
	 * @param indexname 
	 * @return 返回当前刷新频率
	 */
	public String disableIndexRefresh(){
		return esclient.getChannelManager().getSettingsChannel().disableRefreshInterval(IndexStructureConstants.PerformanceIndex);
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
		esclient.getChannelManager().getSettingsChannel().openRefreshInterval(IndexStructureConstants.PerformanceIndex, time);
	}
}
