package com.bhu.vas.business.search.service;

import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import com.smartwork.msip.cores.helper.ReflectionHelper;
import com.smartwork.msip.cores.helper.StringHelper;

public abstract class AbstractDataSearchService<MODEL> {
    @Resource
	private ElasticsearchTemplate elasticsearchTemplate;
    
	@Resource
	private Client client;
	
	protected Class<MODEL> entityClass;

	@SuppressWarnings("unchecked")
	public AbstractDataSearchService() {
		this.entityClass = ReflectionHelper.getSuperClassGenricType(getClass(),0);
	}
	
	//public abstract ElasticsearchTemplate getElasticsearchTemplate();
	public ElasticsearchTemplate getElasticsearchTemplate(){
		return elasticsearchTemplate;
	}
	
	
	public void refresh(/*Class<T> classz,*/boolean waitForOperation){
		getElasticsearchTemplate().refresh(entityClass, waitForOperation);
	}
	
	public Map getSetting(){
		return getElasticsearchTemplate().getSetting(entityClass);
	}
	
	/**
	 * A time setting controlling how often the refresh operation will be executed. 
	 * Defaults to 1s. Can be set to -1 in order to disable it.
	 * @param time 1s
	 */
	public boolean openRefreshInterval(String indexname, String time){
		return updateIndexSettings(indexname, ImmutableSettings.settingsBuilder().put("index.refresh_interval", time).build());
	}
	/**
	 * 禁止索引库执行刷新数据操作
	 * @param indexname 
	 * @return 返回当前刷新频率
	 */
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
	
	
	
	/**
	 * 获取索引库的settings
	 * @param indexname
	 * @return
	 */
	public ImmutableOpenMap<String, Settings> getIndexSettings(String indexname){
		GetSettingsResponse response = client.admin().indices().prepareGetSettings(indexname).get();
		return response.getIndexToSettings();
	}
	
	/**
	 * 更新索引的settings
	 * @param indexname
	 * @param settings
	 * @return
	 */
	public boolean updateIndexSettings(String indexname, Settings settings){
		UpdateSettingsResponse response = client.admin().indices().prepareUpdateSettings(indexname).setSettings(settings).execute().actionGet();
		if(response != null){
			return response.isAcknowledged();
		}
		return false;
	}
	
	
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
