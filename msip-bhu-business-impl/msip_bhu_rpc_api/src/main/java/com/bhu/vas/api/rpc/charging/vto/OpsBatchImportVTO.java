package com.bhu.vas.api.rpc.charging.vto;

@SuppressWarnings("serial")
public class OpsBatchImportVTO implements java.io.Serializable{
	private String id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public static OpsBatchImportVTO fromBatchImportVTO(BatchImportVTO vto){
		if(vto == null)
			return null;
		OpsBatchImportVTO o = new OpsBatchImportVTO();
		o.setId(vto.getId());
		return o;
	}
}
