package com.bhu.vas.api.vto.device;

/**
 * 设备运营信息
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class DeviceOperationVTO implements java.io.Serializable{
	private String dut;
	private int gl;
	private String gln;
	private String mstyle;
	public String getDut() {
		return dut;
	}
	public void setDut(String dut) {
		this.dut = dut;
	}
	public int getGl() {
		return gl;
	}
	public void setGl(int gl) {
		this.gl = gl;
	}
	public String getMstyle() {
		return mstyle;
	}
	public void setMstyle(String mstyle) {
		this.mstyle = mstyle;
	}
	public String getGln() {
		return gln;
	}
	public void setGln(String gln) {
		this.gln = gln;
	}
	
}
