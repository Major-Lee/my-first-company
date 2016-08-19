package com.bhu.vas.api.dto.commdity;

public class RewardQueryExportRecordVTO {
	//下载地址
	private String url;
	private String filename;
	private byte[] bs;
	public byte[] getBs() {
		return bs;
	}

	public void setBs(byte[] bs) {
		this.bs = bs;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
