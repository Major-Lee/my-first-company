package com.bhu.vas.shard.impl.shardstrategy;

import java.util.List;
import java.util.Map;

import com.google.code.shardbatis.strategy.ShardStrategy;
import com.smartwork.msip.business.shard.impl.shardstrategy.ShardStratgyHelper;
import com.smartwork.msip.cores.orm.model.splitter.TableSplitable;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
@SuppressWarnings({"rawtypes","unchecked"})
public class OrderShardStrategyImpl implements ShardStrategy{

	@Override
	public String getTargetTableName(String baseTableName, Object params, String mapperId) {
		/*System.out.println("baseTableName:---------------:"+baseTableName);
		System.out.println("params:---------------:"+params);
		System.out.println("mapperId:---------------:"+mapperId);*/
		String targetTableName = null;
		if(params instanceof Integer){
			targetTableName = ShardStratgyHelper.generateSplitterTableByTableName(baseTableName, params);
		}else if(params instanceof TableSplitable){
			TableSplitable split = (TableSplitable)params;
			targetTableName = ShardStratgyHelper.generateSplitterTableByTableName(baseTableName, split.getBusinessValue4Spliter());
		}else if(params instanceof CommonCriteria){
			targetTableName = ShardStratgyHelper.generateSplitterTableByTableName(baseTableName, ((CommonCriteria)params).getBusinessValue4Spliter());
		}else if(params instanceof Map){
			List<Integer> pks = (List<Integer>)((Map<String,List<Integer>>)params).get("list");
			targetTableName = ShardStratgyHelper.generateSplitterTableByTableName(baseTableName, pks.get(0));
		}
		//int value = HashAlgorithmsHelper.additiveHash(String.valueOf(t), 5);
		//return tablekey.concat("-").concat(String.format("%04d", value));
		//return baseTableName+"_01";
		//System.out.println("targetTableName:---------------:"+targetTableName);
		return targetTableName;
	}
	
}
