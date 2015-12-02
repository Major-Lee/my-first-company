package com.bhu.vas.api.dto;

import org.apache.commons.lang.StringUtils;


@SuppressWarnings("serial")
public class DownCmds implements java.io.Serializable{
	private String ctx;
	private String mac;
	private String[] cmds;
	
	public DownCmds() {
	}
	public DownCmds(String mac, String[] cmds) {
		this(null,mac,cmds);
	}
	public DownCmds(String ctx, String mac, String[] cmds) {
		super();
		this.ctx = ctx;
		this.mac = mac;
		this.cmds = cmds;
	}
	public String getCtx() {
		return ctx;
	}
	public void setCtx(String ctx) {
		this.ctx = ctx;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String[] getCmds() {
		return cmds;
	}
	public void setCmd(String[] cmds) {
		this.cmds = cmds;
	}
	
	public boolean valid(){
		return (StringUtils.isNotEmpty(mac) || cmds != null || cmds.length >0 );
	}
	
	/*public static DownCmds builderDownCmds(int aa,String... cmds){
		return null;
	}*/
	
	public static DownCmds builderDownCmds(String mac,String... cmds){
		return new DownCmds(mac,cmds);
	}
	public static DownCmds builderDownCmdsWithCtx(String ctx,String mac,String... cmds){
		return new DownCmds(ctx,mac,cmds);
	}
}
