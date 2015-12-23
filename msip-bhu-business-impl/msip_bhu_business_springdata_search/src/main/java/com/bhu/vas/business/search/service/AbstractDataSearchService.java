package com.bhu.vas.business.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;

import com.bhu.vas.business.search.model.AbstractDocument;
import com.smartwork.msip.cores.helper.ReflectionHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

public abstract class AbstractDataSearchService<MODEL extends AbstractDocument> {
    @Resource
	private ElasticsearchTemplate elasticsearchTemplate;
    
/*	@Resource
	private Client client;*/
	
	protected Class<MODEL> entityClass;

	@SuppressWarnings("unchecked")
	public AbstractDataSearchService() {
		this.entityClass = ReflectionHelper.getSuperClassGenricType(getClass(),0);
	}
	
	//public abstract ElasticsearchTemplate getElasticsearchTemplate();
	public ElasticsearchTemplate getElasticsearchTemplate(){
		return elasticsearchTemplate;
	}
	public void refresh(/*Class<MODEL> classz,*/boolean waitForOperation){
		getElasticsearchTemplate().refresh(entityClass, waitForOperation);
	}
	
	public void bulkIndex(List<MODEL> models){
		bulkIndex(models,false,false);
	}
	
	/**
	 * 打包批量建索引
	 * 不主动刷新和合并索引，由参数控制（由于springdata save生成索引会自动合并刷新）
	 * 可以用于索引的批量生成，在打数据量的情况下使用提升性能
	 * @param models
	 * @param refresh
	 * @param waitForOperation
	 */
	public void bulkIndex(List<MODEL> models,boolean refresh,boolean waitForOperation){
		if(!models.isEmpty()){
			List<IndexQuery> indexQuerys = new ArrayList<IndexQuery>();
			for(MODEL model:models){
				indexQuerys.add(new IndexQueryBuilder().withId(model.getId()).withObject(model).build());
			}
			getElasticsearchTemplate().bulkIndex(indexQuerys);
			if(refresh){
				getElasticsearchTemplate().refresh(entityClass, waitForOperation);
			}
		}
	}
	
	public void updateIndex(String id, Map<String, Object> sourceMap){
		updateIndex(id, sourceMap, false, false);
	}
	/**
	 * 更新单条索引数据
	 * @param id 索引主键
	 * @param sourceMap 需要更新的字段数据map
	 * @param refresh
	 * @param waitForOperation
	 */
	public void updateIndex(String id, Map<String, Object> sourceMap, boolean refresh, boolean waitForOperation){
		if(StringUtils.isEmpty(id) || sourceMap == null || sourceMap.isEmpty()) return;
		
		IndexRequest indexRequest = new IndexRequest();
		indexRequest.source(sourceMap);
		UpdateQuery updateQuery = new UpdateQueryBuilder().withId(id)
				.withClass(entityClass).withIndexRequest(indexRequest).build();
		this.getElasticsearchTemplate().update(updateQuery);
		if(refresh){
			getElasticsearchTemplate().refresh(entityClass, waitForOperation);
		}
	}
	
	public void bulkUpdate(List<String> ids, List<Map<String, Object>> sourceMaps){
		bulkUpdate(ids, sourceMaps, false, false, false);
	}
	
	/**
	 * 打包批量更新索引数据 不同id 不同数据
	 * @param ids 索引主键的集合
	 * @param sourceMaps 需要更新的字段数据map的集合
	 * @param refresh
	 * @param waitForOperation
	 */
	public void bulkUpdate(List<String> ids, List<Map<String, Object>> sourceMaps, boolean upsert,
			boolean refresh, boolean waitForOperation){
		if(ids == null || ids.isEmpty()) return;
		if(sourceMaps == null || sourceMaps.isEmpty()) return;
		if(ids.size() != sourceMaps.size()) return;
		
		List<UpdateQuery> updateQuerys = new ArrayList<UpdateQuery>();
		int cursor = 0;
		for(String id : ids){
			if(StringUtils.isEmpty(id)) continue;
			Map<String, Object> sourceMap = sourceMaps.get(cursor);
			if(sourceMap == null || sourceMap.isEmpty()) continue;

			IndexRequest indexRequest = new IndexRequest();
			indexRequest.source(sourceMap);
			updateQuerys.add(new UpdateQueryBuilder().withId(id).withDoUpsert(upsert)
					.withClass(entityClass).withIndexRequest(indexRequest).build());
			cursor++;
		}
		
		if(!updateQuerys.isEmpty()){
			getElasticsearchTemplate().bulkUpdate(updateQuerys);
			if(refresh){
				getElasticsearchTemplate().refresh(entityClass, waitForOperation);
			}
		}
	}
	
	/**
	 * 打包批量更新索引数据 不同id 相同数据
	 * @param ids 索引主键的集合
	 * @param sourceMap 需要更新的字段数据map
	 * @param refresh
	 * @param waitForOperation
	 */
	public void bulkUpdate(List<String> ids, Map<String, Object> sourceMap, boolean upsert,
			boolean refresh, boolean waitForOperation){
		if(ids == null || ids.isEmpty()) return;
		if(sourceMap == null || sourceMap.isEmpty()) return;
		
		List<UpdateQuery> updateQuerys = new ArrayList<UpdateQuery>();
		for(String id : ids){
			if(StringUtils.isEmpty(id)) continue;

			IndexRequest indexRequest = new IndexRequest();
			indexRequest.source(sourceMap);
			updateQuerys.add(new UpdateQueryBuilder().withId(id).withDoUpsert(upsert)
					.withClass(entityClass).withIndexRequest(indexRequest).build());
		}
		
		if(!updateQuerys.isEmpty()){
			getElasticsearchTemplate().bulkUpdate(updateQuerys);
			if(refresh){
				getElasticsearchTemplate().refresh(entityClass, waitForOperation);
			}
		}
	}
	
	
	public void iteratorAll(String indices,String types,IteratorNotify<Page<MODEL>> notify){
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
	    .withQuery(QueryBuilders.matchAllQuery())
	    .withIndices(indices)
	    .withTypes(types)
	    .withPageable(new PageRequest(0,10))
	    .build();

		String scrollId = elasticsearchTemplate.scan(searchQuery, 60000, false);
		boolean hasRecords = true;
		while (hasRecords) {
			Page<MODEL> page = elasticsearchTemplate.scroll(scrollId, 60000, entityClass);
			if (page.hasContent()) {
				notify.notifyComming(page);
			} else {
				hasRecords = false;
			}
		}
	}
	
/*	public Map getSetting(){
		return getElasticsearchTemplate().getSetting(entityClass);
	}
	
	*//**
	 * A time setting controlling how often the refresh operation will be executed. 
	 * Defaults to 1s. Can be set to -1 in order to disable it.
	 * @param time 1s
	 *//*
	public boolean openRefreshInterval(String indexname, String time){
		return updateIndexSettings(indexname, ImmutableSettings.settingsBuilder().put("index.refresh_interval", time).build());
	}
	*//**
	 * 禁止索引库执行刷新数据操作
	 * @param indexname 
	 * @return 返回当前刷新频率
	 *//*
	public String disableRefreshInterval(String indexname){
		//String nowIndexRefreshInterval = ImmutableSettings.settingsBuilder().get(ESConstants.Settings.IndexRefreshInterval.getName());
		GetSettingsResponse getSettingsResponse = client.admin().indices().prepareGetSettings(indexname).get();
		String nowIndexRefreshInterval = getSettingsResponse.getSetting(indexname, "index.refresh_interval");
		System.out.println(nowIndexRefreshInterval);
		if(StringHelper.isEmpty(nowIndexRefreshInterval)){
			nowIndexRefreshInterval = "-1";
		}
		
		this.updateIndexSettings(indexname, ImmutableSettings.settingsBuilder().put("refresh_interval","100s").build());
		return nowIndexRefreshInterval;
	}
	
	
	
	*//**
	 * 获取索引库的settings
	 * @param indexname
	 * @return
	 *//*
	public ImmutableOpenMap<String, Settings> getIndexSettings(String indexname){
		GetSettingsResponse response = client.admin().indices().prepareGetSettings(indexname).get();
		return response.getIndexToSettings();
	}
	
	*//**
	 * 更新索引的settings
	 * @param indexname
	 * @param settings
	 * @return
	 *//*
	public boolean updateIndexSettings(String indexname, Settings settings){
		UpdateSettingsResponse response = client.admin().indices().prepareUpdateSettings(indexname).setSettings(settings).execute().actionGet();
		if(response != null){
			return response.isAcknowledged();
		}
		return false;
	}*/
	
	
/*	*//**
	 * 禁止索引库执行刷新数据操作
	 * @param indexname 
	 * @return 返回当前刷新频率
	 *//*
	public String disableIndexRefresh(){
		return client.getChannelManager().getSettingsChannel().disableRefreshInterval(getIndexName());
	}
	*//**
	 * 开启索引库刷新数据操作
	 * @param indexname
	 * @param time 刷新频率
	 *//*
	public void openIndexRefresh(){
		String time = getIndexRefresh();
		if(StringHelper.isEmpty(time)){
			time = ESConstants.SettingsValue.IndexRefreshIntervalDefault;
		}
		esclient.getChannelManager().getSettingsChannel().openRefreshInterval(getIndexName(), time);
	}*/
}
