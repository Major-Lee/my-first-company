package com.bhu.vas.api.vto.device;

@SuppressWarnings("serial")
public class ModuleStyleVTO implements java.io.Serializable{
	private String dref;
    private String style;
    private String version;
	private String memo;
	public String getDref() {
		return dref;
	}
	public void setDref(String dref) {
		this.dref = dref;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
}
