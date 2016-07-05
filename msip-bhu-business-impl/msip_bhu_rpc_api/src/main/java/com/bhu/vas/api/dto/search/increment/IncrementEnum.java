package com.bhu.vas.api.dto.search.increment;

import java.util.HashMap;
import java.util.Map;

public class IncrementEnum {
	
	public static final String KafkaReceiveTopicName = "increment_receive";
	public static final String KafkaPerformReceiveTopicName = "increment_perform_receive";
	
	public enum IncrementActionEnum{
		WD_FullCreate("WD_FCT","设备索引完整创建数据"),
		;
		static Map<String, IncrementActionEnum> incrementActions;
		String key;
		String desc;
		
		IncrementActionEnum(String key,String desc){
			this.key = key;
			this.desc = desc;
		}

		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
		static {
			incrementActions = new HashMap<String,IncrementActionEnum>();
			IncrementActionEnum[] types = values();
			for (IncrementActionEnum type : types)
				incrementActions.put(type.getKey(), type);
		}
		
		public static IncrementActionEnum getIncrementActionFromKey(String key) {
			return incrementActions.get(key);
		}
	}
	
	public enum IncrementPrefixEnum{
		SinglePrefix("SGP","增量索引单条数据处理"),
		BulkPrefix("BKP","增量索引批量数据处理"),
		;
		static Map<String, IncrementPrefixEnum> incrementPrefixs;
		String key;
		String desc;
		
		IncrementPrefixEnum(String key,String desc){
			this.key = key;
			this.desc = desc;
		}

		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
		static {
			incrementPrefixs = new HashMap<String,IncrementPrefixEnum>();
			IncrementPrefixEnum[] types = values();
			for (IncrementPrefixEnum type : types)
				incrementPrefixs.put(type.getKey(), type);
		}
		
		public static IncrementPrefixEnum getIncrementPrefixFromKey(String key) {
			return incrementPrefixs.get(key);
		}
	}
}
