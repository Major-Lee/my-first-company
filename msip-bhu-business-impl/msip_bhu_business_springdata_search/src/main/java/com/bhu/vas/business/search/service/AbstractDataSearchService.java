package com.bhu.vas.business.search.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

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
	/*public void refresh(Class<T> classz,boolean waitForOperation){
		getElasticsearchTemplate().refresh(entityClass, waitForOperation);
	}*/
	
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
