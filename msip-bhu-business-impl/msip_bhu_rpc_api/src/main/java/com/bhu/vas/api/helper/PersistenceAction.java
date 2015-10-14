package com.bhu.vas.api.helper;

public class PersistenceAction {
		public static final String Oper_Update = "update";
		public static final String Oper_Remove = "remove";
		private String operation;
		private String key;
		
		public PersistenceAction() {
		}
		public PersistenceAction(String operation, String key) {
			this.operation = operation;
			this.key = key;
		}
		public String getOperation() {
			return operation;
		}
		public void setOperation(String operation) {
			this.operation = operation;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
}
